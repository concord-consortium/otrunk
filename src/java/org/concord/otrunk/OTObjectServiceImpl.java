/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*
 * Last modification information:
 * $Revision: 1.12 $
 * $Date: 2007-05-21 06:23:33 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.Vector;

import org.concord.framework.otrunk.OTControllerService;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.otrunk.datamodel.DataObjectUtil;
import org.concord.otrunk.datamodel.OTDataList;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTRelativeID;

public class OTObjectServiceImpl
    implements OTObjectService
{
    OTrunkImpl otrunk;
    protected OTDatabase creationDb;
    protected OTDatabase mainDb;
    protected Vector listeners = new Vector();

    public OTObjectServiceImpl(OTrunkImpl otrunk)
    {
        this.otrunk = otrunk;
    }
    
    public void setCreationDb(OTDatabase creationDb)
    {
        this.creationDb = creationDb;
    }
    
    public OTDatabase getCreationDb()
    {
        return creationDb;
    }
    
    public void setMainDb(OTDatabase mainDb)
    {
        this.mainDb = mainDb;
    }
    
    public OTObject createObject(Class objectClass) 
        throws Exception
    {
        OTDataObject dataObject = createDataObject();
        
        OTObject newObject = loadOTObject(dataObject, objectClass);
        dataObject.setResource(OTrunkImpl.RES_CLASS_NAME, objectClass.getName());

        // TODO this is actually called twice, once by loadOTObject and 
        // once here.  is that what should happen?
        newObject.init();
        
        return newObject;
    }

    public OTObject getOTObject(OTID childID) throws Exception
    {
        // sanity check
        if(childID == null) {
            throw new Exception("Null child id");
        }
        
        OTDataObject childDataObject = getOTDataObject(childID);
        if(childDataObject == null) {
            // we have a null data object that means the child doesn't 
            // exist in our database.
            // 
            // in the case of reporting it is possible that an object managed 
            // by one object service (reporting obj service) will want to 
            // access an object managed by another object service.
            // so in this case we delegate to the otrunk so it can find the 
            // appropriated object service.
        	return otrunk.getOrphanOTObject(childID, this);
        }

        return getOTObject(childDataObject);
    }

    public OTID getOTID(String otidStr)
    {
        return otrunk.getOTID(otidStr);
    }

    public OTControllerService createControllerService() {
    	OTControllerRegistry registry = 
    		(OTControllerRegistry) otrunk.getService(OTControllerRegistry.class);
    	return new OTControllerServiceImpl(this, registry);
    }
    
    public OTObject loadOTObject(OTDataObject dataObject, Class otObjectClass)
    throws  Exception
    {
        OTObject otObject = null;
        
        if(otObjectClass.isInterface()) {
            OTBasicObjectHandler handler = new OTBasicObjectHandler(dataObject, otrunk, this, otObjectClass);

            otObject = (OTObject)Proxy.newProxyInstance(otObjectClass.getClassLoader(),
                    new Class[] { otObjectClass }, handler);            
        } else {                    
            otObject = setResourcesFromSchema(dataObject, otObjectClass);
        }
        
        notifyLoaded(otObject);
        
        otObject.init();
        
        otrunk.putLoadedObject(otObject, dataObject);
         
        return otObject;        
    }
    
    /**
	 * @param otObject
	 */
	protected void notifyLoaded(OTObject otObject) 
	{
		for(int i=0; i < listeners.size(); i++) {
			((OTObjectServiceListener)listeners.get(i)).objectLoaded(otObject);
		}
	}

	/**
     * Track down the objects schema by looking at the type
     * of class of the argument to setResources method
     * 
     * @param dataObject
     * @param otObject
     */
    public OTObject setResourcesFromSchema(OTDataObject dataObject, Class otObjectClass)
    {
        Constructor [] memberConstructors = otObjectClass.getConstructors();
        Constructor resourceConstructor = memberConstructors[0]; 
        Class [] params = resourceConstructor.getParameterTypes();
        
        if(memberConstructors.length > 1) {
            System.err.println("OTObjects should only have 1 constructor");
            return null;
        }
        
        if(params == null | params.length == 0) {
            try {
                return (OTObject)otObjectClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        
        OTResourceSchemaHandler handler = null;
        
        Object constructorParams [] = new Object [params.length];
        int nextParam = 0;
        if(params[0].isInterface() && 
                OTResourceSchema.class.isAssignableFrom(params[0])){
            Class schemaClass = params[0];
                
            handler = 
                new OTResourceSchemaHandler(dataObject, otrunk, this, schemaClass);

            Class [] interfaceList = new Class[] { schemaClass };
            
            Object resources = 
                Proxy.newProxyInstance(schemaClass.getClassLoader(),
                    interfaceList, handler);
            
            constructorParams[0] = resources;
            nextParam++;
        }
        
        for(int i=nextParam; i<params.length; i++) {
            // look for a service in the services list to can 
            // be used for this param
            constructorParams[i] = otrunk.getService(params[i]);
            
            if(constructorParams[i] == null) {
                System.err.println("No service could be found to handle the\n" +
                        " requirement of: " + otObjectClass + "\n" +
                        " for: " + params[i]);              
                return null;                
            }
        }
        
        OTObject otObject = null;
        try {
            otObject = (OTObject)resourceConstructor.newInstance(constructorParams);
            
            // now we need to pass the otObject to the schema handler so it can
            // set that as the source of OTChangeEvents
            handler.setEventSource(otObject);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }       
        
        return otObject;
    }

    boolean managesObject(OTID id)
    {
        if(id instanceof OTRelativeID) {
            OTID rootRelativeId = ((OTRelativeID)id).getRootId();
            if(rootRelativeId != null) {
                OTDatabase rootDb = otrunk.getOTDatabase(rootRelativeId);
                return rootDb == mainDb;
            }
        }
        
        return false;
    }
    
    /**
     * This method is only used internally. Once a data object has
     * been tracked down then method is used to get the OTObject
     * it checks the cache of loadedObjects before making a new one
     * 
     * FIXME: this seems a little unclear and maybe dangerous
     * there is an assumption that there will only be one OTObject for each 
     * id.  And the the getLoadedObject helps with this.  However there could
     * be two OTObjectServices what are both asked for the the object.
     * And depending who gets it first that will determine which Object service
     * is used.  This breaks the idea of the object service as being a context
     * 
     * @param childDataObject
     * @return
     * @throws Exception
     */
    OTObject getOTObject(OTDataObject childDataObject)
        throws Exception
    {
        OTObject otObject = null;
        
        otObject = otrunk.getLoadedObject(childDataObject);
        if(otObject != null) {
            return otObject;
        }
        
        String otObjectClassStr = 
        	OTrunkImpl.getClassName(childDataObject);
        if(otObjectClassStr == null) {
            return null;
        }
            
        Class otObjectClass = Class.forName(otObjectClassStr);
    
        return loadOTObject(childDataObject, otObjectClass);        
    }
    
    private OTDataObject createDataObject()
        throws Exception
    {
        return creationDb.createDataObject();
    }
    
    /**
     *  
     * @param dataParent
     * @param childID
     * @return
     * @throws Exception
     */
    private OTDataObject getOTDataObject(OTID childID)
        throws Exception
    {
        // sanity check
        if(childID == null) {
            throw new Exception("Null child Id");
        }

        OTDataObject childDataObject = mainDb.getOTDataObject(null, childID);
        /*
         * FIXME: this is a bit a o	public OTObject copyObject(OTObject original, OTObjectList orphanObjectList, 
	                           int maxDepth) 
f a hack
         * it is to solve the problem caused by reports.  The report creates a 
         * compound document with links to objects in each users database.  When
         * the compound document resolves these links it uses itself as the
         * dataParent.   But the compound documents database is the authored
         * database, so it won't find the user objects. 
         * In this case the childID will be relative.  And the rootId will 
         * the id of the users template database.  
         * In this case the OTObjectService returned should be the object service
         * of that users template database not the current object service.  This 
         * could be resolved by having this ask the otrunk for the object service
         * that should handle this object.  And then letting that object service
         * create the object.
         * 
         */ 
        /*
         * We don't actually want to do this here because then the object will have
         * the wrong object service.  Instead this case is handled higher up.
         * 
        if(childDataObject == null && childID instanceof OTRelativeID) {
            OTDatabase parentDb = mainDb;            
            OTID rootRelativeId = ((OTRelativeID)childID).getRootId();
            if(rootRelativeId != null) {
                parentDb = otrunk.getOTDatabase(rootRelativeId);
            }

            childDataObject = parentDb.getOTDataObject(null, childID);
        }	public OTObject copyObject(OTObject original, OTObjectList orphanObjectList, 
	                           int maxDepth) 

        */
        return childDataObject;
    }

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTObjectService#copyObject(org.concord.framework.otrunk.OTObject, int)
	 */
	public OTObject copyObject(OTObject original, int maxDepth) 
	throws Exception	
	{
		OTObjectList orphanObjectList = null;
		OTObject root = otrunk.getRealRoot();
		if(root instanceof OTSystem) {
			orphanObjectList = ((OTSystem)root).getLibrary();
		}

		return copyObject(original, orphanObjectList, maxDepth);
	}
	
	public OTObject copyObject(OTObject original, OTObjectList orphanObjectList, 
	                           int maxDepth) 
		throws Exception	
		{
		// make a copy of the original objects data object
		// it is easy to copy data objects than the actual objects
		OTDataObject originalDataObject = 
			getOTDataObject(original.getGlobalId());
		
		// Assume the object list is our object list impl
		OTDataList orphanDataList = 
			((OTObjectListImpl)orphanObjectList).getDataList();
		
		OTDataObject copyDataObject = 
			DataObjectUtil.copy(originalDataObject, creationDb, 
					orphanDataList, maxDepth);

		return getOTObject(copyDataObject.getGlobalId());		
	}

	public void addObjectServiceListener(OTObjectServiceListener listener)
	{
		if(listeners.contains(listener)) {
			return;
		}
		listeners.add(listener);
	}

	public void removeObjectServiceListener(OTObjectServiceListener listener)
	{
		listeners.remove(listener);
	}

}
