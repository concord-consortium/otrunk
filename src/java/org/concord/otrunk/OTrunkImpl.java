
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
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
 */

/*
 * Created on Aug 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Hashtable;
import java.util.Vector;
import java.util.WeakHashMap;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.OTUser;
import org.concord.framework.otrunk.OTWrapper;
import org.concord.framework.otrunk.OTrunk;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTIDFactory;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OTrunkImpl implements OTrunk
{
	public static final String RES_CLASS_NAME = "otObjectClass";

	protected Hashtable loadedObjects = new Hashtable();
	protected Hashtable userDataObjects = new Hashtable();
	protected WeakHashMap objectWrappers = new WeakHashMap();
	protected Vector services = null;
	
	protected OTDatabase db;
	
	public OTrunkImpl(OTDatabase db)
	{
		this(db, null);
	}

	public OTrunkImpl(OTDatabase db, Object [] services)
	{		
		this.db = db;
		if(services != null) {
			this.services = new Vector();
			for(int i=0; i<services.length; i++) {
				this.services.add(services[i]);
			}
		}
		
		// We should look up if there are any sevices.
		try {
			OTObject root = getRealRoot();
			if(!(root instanceof OTSystem)) {
				return;
			}
			
			OTObjectList serviceList = ((OTSystem)root).getServices();
			
			if(this.services == null) {
				this.services = new Vector();
			}
			this.services.addAll(serviceList.getVector());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 *  (non-Javadoc)
     * @see org.concord.framework.otrunk.OTrunk#getOTID(java.lang.String)
     */
    public OTID getOTID(String otidStr)
    {
        // TODO Auto-generated method stub
        OTID id = OTIDFactory.createOTID(otidStr);
        if(id == null) {
            // lookup the id in the user object list
            id = (OTID)userDataObjects.get(otidStr);
        }
        return id;
    }
	
	/* (non-Javadoc)
	 */
	public OTObject createObject(Class objectClass)
		throws Exception
	{
    	OTDataObject dataObject = createDataObject();
    	
		OTObject newObject = loadOTObject(dataObject, objectClass);
		dataObject.setResource(RES_CLASS_NAME, objectClass.getName());
		newObject.init();
		
		return newObject;
	}

	public OTDataObject createDataObject()
		throws Exception
	{
		return db.createDataObject();
	}
	
	public void setRoot(OTObject obj) throws Exception
	{
		// FIXME this doesn't do a good job if there
		// is an OTSystem
		OTID id = obj.getGlobalId();
		db.setRoot(id);
	}
		
	protected OTObject getRealRoot() throws Exception
	{
		OTDataObject rootDO = getRootDataObject();
		if(rootDO == null) {
			return null;
		}
		return getOTObject(rootDO);
	}
	
	public OTObject getRoot() throws Exception
	{
		OTObject root = getRealRoot();
		if(root instanceof OTSystem) {
			return ((OTSystem)root).getRoot();
		}
		
		return root;
	}
	/**
	 * The dataParent must be set so the database can correctly look up the 
	 * child object.
	 *  
	 * @param dataParent
	 * @param childID
	 * @return
	 * @throws Exception
	 */
	public OTDataObject getOTDataObject(OTDataObject dataParent, OTID childID)
		throws Exception
	{
		// sanity check
		if(childID == null) {
			throw new Exception("Null child Id");
		}
		
		if(childID instanceof OTUserDataObject) {
		    return (OTUserDataObject)childID;
		}
		
		OTDataObject childDataObject = db.getOTDataObject(dataParent, childID);
		
		// if the mode is authoring then just return the requested object
		// if the mode is student then the requested node needs to be looked up
		// in the users object table.  Then that object is setup to reference 
		// the authoring object.
		if ( (dataParent == null) || 
				!(dataParent instanceof OTUserDataObject)) {
			return childDataObject;
		}	
		
		OTUserStateMap user = ((OTUserDataObject)dataParent).getUser();	
		OTUserDataObject userDataObject = user.getUserDataObject(childDataObject);
		userDataObjects.put(userDataObject.toString(), userDataObject);
		
		return userDataObject;
	}
	
	public void close()
	{
		db.close();
	}
	
	public OTObject loadOTObject(OTDataObject dataObject, Class otObjectClass)
	throws	Exception
	{
		OTObject otObject = null;
		
		if(otObjectClass.isInterface()) {
			OTBasicObjectHandler handler = new OTBasicObjectHandler(dataObject, this);

		    otObject = (OTObject)Proxy.newProxyInstance(otObjectClass.getClassLoader(),
		    		new Class[] { otObjectClass }, handler);		    
		} else {					
			otObject = setResourcesFromSchema(dataObject, otObjectClass);
			
			// this is a necessary evil for the time being
			if(otObject instanceof DefaultOTObject) {
				((DefaultOTObject)otObject).setOTDatabase(this);
			}
		}
		
		otObject.init();
		
		WeakReference objRef = new WeakReference(otObject);
		loadedObjects.put(dataObject, objRef);
		
		/*
		if(otObject instanceof OTWrapper){
		    // save the wrapped object in a weak hashmap
		    // so it can be searched out later
		    Method getWrappedObject = otObjectClass.getMethod("getWrappedObject", null);
		    Object wrappedObject = getWrappedObject.invoke(otObject, null);
		    objectWrappers.put(wrappedObject, new WeakReference(otObject));
		}
		*/
		
		return otObject;		
	}
	
	public OTWrapper getWrapper(Object wrappedObject)
	{
	    WeakReference objRef = (WeakReference)objectWrappers.get(wrappedObject);
	    if(objRef != null){
	        return (OTWrapper)objRef.get();
	    }
	    
	    return null;
	}
	
	public OTWrapper putWrapper(Object wrappedObject, OTWrapper wrapper)
	{
	    WeakReference objRef = new WeakReference(wrapper);
	    WeakReference oldRef = (WeakReference)objectWrappers.put(wrappedObject, objRef);
	    
	    if(oldRef != null) {
	        return (OTWrapper)oldRef.get();
	    }
	    
	    return null;
	}
	
	/**
	 * Warning: this is method should only be used when you don't know
	 * which object is requesting the new OTObject.  The requestion object
	 * is currently used to keep the context of user mode or authoring mode
	 * @param childID
	 * @return
	 * @throws Exception
	 */
	public OTObject getOTObject(OTID childID)
		throws Exception
	{
		return getOTObject(getRootDataObject(), childID);
	}
	
	public OTObject getOTObject(OTID referingId , OTID childID)
		throws Exception
	{
		OTDataObject referingObj = getOTDataObject(null, referingId);
		return getOTObject(referingObj, childID);
	}
	
	/**
	 * 
	 * @param referingDataObject this is the data object that is refering
	 *   to this new object
	 * @param childID the id of the new object
	 * @return the requested object or null if there is a problem
	 * @throws Exception
	 */
	public OTObject getOTObject(OTDataObject referingDataObject, OTID childID)
	throws Exception
	{
		// sanity check
		if(childID == null) {
			throw new Exception("Null child id");
		}
		OTDataObject childDataObject = getOTDataObject(referingDataObject, childID);
		if(childDataObject == null) {
			//hmmm we have a null data object that means the child doesn't 
			//exist in the database.  
			// I suppose we could throw a special "not found" exception here
			return null;
		}

		return getOTObject(childDataObject);
	}

	/**
	 * This method is only used internally. Once a data object has
	 * been tracked down then method is used to get the OTObject
	 * it checks the cache of loadedObjects before making a new one
	 * @param childDataObject
	 * @return
	 * @throws Exception
	 */
	private OTObject getOTObject(OTDataObject childDataObject)
		throws Exception
	{
		OTObject otObject = null;
		
		Reference otObjectRef = (Reference)loadedObjects.get(childDataObject);
		if(otObjectRef != null) {
		    otObject = (OTObject)otObjectRef.get();
		    if(otObject != null) {
		        return otObject;
		    }
		    
		    loadedObjects.remove(childDataObject);
		}
		
		String otObjectClassStr = 
		    (String)childDataObject.getResource(RES_CLASS_NAME);
		if(otObjectClassStr == null) {
			return null;
		}
			
		Class otObjectClass = Class.forName(otObjectClassStr);
	
		return loadOTObject(childDataObject, otObjectClass);		
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
		
		Object constructorParams [] = new Object [params.length];
		int nextParam = 0;
		if(params[0].isInterface() && 
				OTResourceSchema.class.isAssignableFrom(params[0])){
			Class schemaClass = params[0];
				
			InvocationHandler handler = 
				new OTResourceSchemaHandler(dataObject, this, schemaClass);

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
			if(services == null) {
				System.err.println("There are no services defined and the current\n" +
						"object needs at least one: " + otObjectClass);
				// we should be careful that this isn't service
				// itself.  In this case the services vector will
				// be null, but the error message will be incorrect
				return null;
			}
			
			constructorParams[i] = null;
			for(int j=0; j<services.size(); j++) {
				Object service = services.get(j);
				if(params[i].isInstance(service)) {
					constructorParams[i] = service;
					break;
				}
			}
			
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
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }	    
	    
	    return otObject;
	}

	public OTObject getUserRuntimeObject(OTObject authoredObject, OTUser user)
		throws Exception
	{
		OTID authoredId = authoredObject.getGlobalId();
		OTDataObject authoredDataObj = getOTDataObject(null, authoredId);
		OTUserDataObject userData = 
			new OTUserDataObject(authoredDataObj, (OTUserStateMap)user, this);
		userDataObjects.put(userData.toString(), userData);
		
		String otObjectClassStr = (String)userData.getResource("otObjectClass");
		if(otObjectClassStr == null) {
			return null;
		}

		try {
			Class otObjectClass = Class.forName(otObjectClassStr);
			OTObject userObject = loadOTObject(userData, otObjectClass);
			return userObject;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return
	 */
	public OTDataObject getRootDataObject()
		throws Exception
	{
		return db.getRoot();
	}
}
