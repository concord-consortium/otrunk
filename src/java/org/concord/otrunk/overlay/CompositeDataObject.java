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
 * $Revision: 1.3 $
 * $Date: 2007-10-05 18:03:39 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.overlay;

import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTID;
import org.concord.otrunk.datamodel.OTDataCollection;
import org.concord.otrunk.datamodel.OTDataList;
import org.concord.otrunk.datamodel.OTDataMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDataObjectType;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTObjectRevision;
import org.concord.otrunk.datamodel.OTTransientMapID;
import org.concord.otrunk.xml.XMLDataObject;


/**
 * OTUserDataObject
 * Class name and description
 *
 * Date created: Aug 24, 2004
 *
 * @author scott<p>
 *
 */
public class CompositeDataObject
	implements OTDataObject
{
	private static final Logger logger =
        Logger.getLogger(CompositeDataObject.class.getCanonicalName());
	
	private OTDataObject baseObject;
	private OTDataObject activeDeltaObject = null;
	private OTDataObject [] middleDeltas;
	private CompositeDatabase database;
	
	
    // The object can represent two types of data objects
    // it is either a bridge between an authored object (template object)
    // and the user modifications to that object object
    // or it is a wrapper around a user created object
	private boolean composite;
	
	private HashMap<String, OTDataCollection> resourceCollections = 
		new HashMap<String, OTDataCollection>();
	
	public CompositeDataObject(OTDataObject baseObject, 
	        CompositeDatabase db, OTDataObject[] middleDeltas, boolean composite)
	{
		this.baseObject = baseObject;
		database = db;
		this.middleDeltas = middleDeltas;
		this.composite = composite;
	}
	
	public OTDatabase getDatabase()
	{
	    return database;
	}
	
	public OTDataObject getBaseObject()
	{
		return baseObject;
	}

	public OTDataObject getActiveDeltaObject()
	{
	    if(activeDeltaObject != null) {
	        return activeDeltaObject;
	    } else {
            // I don't know if I should do this but it should speed things
            // up
            activeDeltaObject = database.getActiveDeltaObject(baseObject);
	        return activeDeltaObject;
	    }
	}
			
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getGlobalId()
	 */
	public OTID getGlobalId()
	{
	    OTID dbId = database.getDatabaseId();
	    
	    return new OTTransientMapID(dbId, baseObject.getGlobalId());
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getCurrentRevision()
	 */
	public OTObjectRevision getCurrentRevision()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getResource(java.lang.String)
	 */
	public Object getResource(String key)
	{
		// If we are not a composite then do the basic thing
		if(!composite){
			return baseObject.getResource(key);
		}
		
		// first look in the userObject
		// then look in the middle deltas
		// then look in the authoringObject

		Object value = null;
		OTDataObject localActiveDelta = getActiveDeltaObject();
		if (localActiveDelta != null) {
			value = localActiveDelta.getResource(key);
			if(value != null) {
				return value;
			} else if(localActiveDelta.containsKey(key)){
				// check if the localActiveDelta contains this key in which we should
				// return null, otherwise we should go on down the line
				return null;
			}
		}

		return getNonActiveDeltaResource(key);
	}

	public Object getNonActiveDeltaResource(String key)
	{
		Object value = null;
		if(middleDeltas != null){
			for(int i=0; i<middleDeltas.length; i++){
				OTDataObject delta = middleDeltas[i];
				if (delta != null) {
					value = delta.getResource(key);
					if(value != null) {
						return value;
					} else if(delta.containsKey(key)){
						return null;
					}
				}
				
			}
		}
		
		return baseObject.getResource(key);		
	}
	
	/**
	 * This will return the union of all the keys set between the base object and the overlay objects.
	 * 
	 * @see org.concord.otrunk.OTDataObject#getResourceKeys()
	 */
	public String[] getResourceKeys()
	{

        HashMap<String, OTDataObject> keyTable = new HashMap<String, OTDataObject>();
        OTDataObject localActiveDelta = getActiveDeltaObject();
        if (localActiveDelta != null) {
            String [] userKeys = localActiveDelta.getResourceKeys();
            for (String userKey : userKeys) {
                // we can put any object, but lets use the userObject
                // so we might take advantage of that later
                keyTable.put(userKey, localActiveDelta);
            }
        }
        
		if(middleDeltas != null){
			for (OTDataObject delta : middleDeltas) {
	            String [] userKeys = delta.getResourceKeys();
	            for (String userKey : userKeys) {
	                keyTable.put(userKey, delta);
	            }				
			}
		}
        
        if(baseObject != null) {
            String [] authorKeys = baseObject.getResourceKeys();
            for (String key : authorKeys) {
            	keyTable.put(key, baseObject);
            }
        }

        Set<String> keySet = keyTable.keySet();
        String [] strKeys = new String [keySet.size()];
        strKeys = keySet.toArray(strKeys);
        return strKeys;
	}

	public OTDataObject getOrCreateActiveDeltaObject()
	{
	    OTDataObject localActiveDelta = getActiveDeltaObject();
		
		if(localActiveDelta == null) {
			localActiveDelta = database.createActiveDeltaObject(baseObject);			
			logger.fine("created delta object: " + localActiveDelta.getGlobalId().toExternalForm());			
		}
		
		return localActiveDelta;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#setResource(java.lang.String, java.lang.Object)
	 */
	public boolean setResource(String key, Object resource)
	{
	    // Need to check if the object being passed in 
	    // is a translated id that we created. If it is we want to strip off our translation
		// because it wouldn't be stored that way.
		resource = resolveIDResource(resource);
		
	    Object oldObject = getResource(key);	    
	    if(oldObject != null && oldObject.equals(resource)){
	        return false;
	    }
	    
	    // special hack for -0.0 and 0.0 
	    // see the docs for Float.equals()
	    if(oldObject instanceof Float && 
	            resource instanceof Float) {
	        if(((Float)oldObject).floatValue() == 
	            ((Float)resource).floatValue()){
	            return false;
	        }
	    }
	    
	    if(!composite){
	    	return baseObject.setResource(key, resource);
	    }
	    
	    // get the existing active delta object or create a new one
	    OTDataObject localActiveDelta = getOrCreateActiveDeltaObject();        	    
		return localActiveDelta.setResource(key, resource);		
	}
	
    /**
     * This method is to handle the case where a resource is an
     * id.  Some of the passed in ids will be from our template
     * database.  So they are really just temporary ids.  These ids are
     * relative ids, where the root is the id of the template db and
     * the relative part is the id of the original object
     * 
     * We want to store the id of the original object.  So this method
     * should be called on any resource that could be an id.
     * 
     * If this isn't working properly then an id will be stored that 
     * doesn't exist. 
     * 
     * @param resource
     * @return
     */
    public Object resolveIDResource(Object resource)
    {
        if(resource instanceof OTTransientMapID) {
        	return database.resolveID((OTID) resource);
        }
        
        return resource;
    }
    
	@SuppressWarnings("unchecked")
    public <T extends OTDataCollection> T getResourceCollection(String key, 
		Class<T> collectionClass)
	{
		OTDataCollection collection = resourceCollections.get(key);
	    if(collection != null) {
	        return (T)collection;
	    }

	    // Get the base list that a delta will be built against
		Object resourceObj = getNonActiveDeltaResource(key);

		// Create a wrapper list so changes to the list will create a new list resource in
		// the active delta object.
		if(collectionClass.equals(OTDataList.class)) {
			collection =  
				new CompositeDataList(this, (OTDataList)resourceObj, key, composite);
		} else if(collectionClass.equals(OTDataMap.class)) {
			collection =  
				new CompositeDataMap(this, (OTDataMap)resourceObj, key, composite);
		}

		resourceCollections.put(key, collection);

	    return (T)collection;
	}

	/**
	 * We do not allow switching the type of the object on the fly, so the type of the object
	 * will always be the type of the base object
	 */
	public OTDataObjectType getType()
    {
		return baseObject.getType();
    }

	public URL getCodebase()
    {
		// FIXME For now return the codebase of the baseObject.
		//  this needs to be through through more.
		return baseObject.getCodebase();
    }

	public boolean containsKey(String key)
    {
		// If we are not a composite then do the basic thing
		if(!composite){
			return baseObject.containsKey(key);
		}
		
		// first look in the userObject
		// then look in the middle deltas
		// then look in the authoringObject

		OTDataObject localActiveDelta = getActiveDeltaObject();
		if (localActiveDelta != null && localActiveDelta.containsKey(key)) {
			return true;
		}

		if(middleDeltas != null){
			for(int i=0; i<middleDeltas.length; i++){
				OTDataObject delta = middleDeltas[i];
				if (delta != null && delta.containsKey(key)) {
					return true;
				}				
			}
		}
		
		return baseObject.containsKey(key);		
    }
	
	public boolean hasOverrideInTopOverlay(String key)
	{
		OTDataObject deltaObject = getActiveDeltaObject();
		if(deltaObject == null){
			return false;
		}
		
		return deltaObject.containsKey(key);
	}

	public void removeOverrideInTopOverlay(String name)
    {
		OTDataObject deltaObject = getActiveDeltaObject();
		if(deltaObject == null){
			logger.warning("Doesn't have a delta object");
			return;
		}
		
		if(!(deltaObject instanceof XMLDataObject)){
			logger.warning("Can only remove overrides on xml data objects");
			return;			
		}
		
		((XMLDataObject)deltaObject).setSaveNulls(false);
		deltaObject.setResource(name, null);
		((XMLDataObject)deltaObject).setSaveNulls(true);		
    }

	void setMiddleDeltas(OTDataObject [] middleDeltas)
    {
	    this.middleDeltas = middleDeltas;
	    
	    // throw away our cached collections
	    resourceCollections.clear();
    }

	OTDataObject [] getMiddleDeltas()
    {
	    return middleDeltas;
    }
	
	/**
	 * A helper method to determine if this object has a modification, or is a modification
	 * in the case of a newly created object.
	 * @return
	 */
	public boolean isModified() {
		if (composite) {
			if (getActiveDeltaObject() != null) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}
	
	public boolean isComposite() {
		return composite;
	}
}
