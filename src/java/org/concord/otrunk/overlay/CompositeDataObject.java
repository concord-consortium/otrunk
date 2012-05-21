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
	
	private OTDataObject container;
	private String containerResourceKey;
	
	private boolean alreadyPulledUp = true;
	
	public CompositeDataObject(OTDataObject baseObject, 
	        CompositeDatabase db, OTDataObject[] middleDeltas, boolean composite)
	{
		this.baseObject = baseObject;
		database = db;
		this.middleDeltas = middleDeltas;
		this.composite = composite;
		resetActiveDeltaObject();
	}

	void resetActiveDeltaObject()
    {
	    if(composite){
			activeDeltaObject = database.getActiveDeltaObject(baseObject);
			alreadyPulledUp = false;
		}
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
		return activeDeltaObject;
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
		try {
			database.readLock();
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
		} finally {
			database.readUnlock();
		}
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
		try {
			database.readLock();
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
		} finally {
			database.readUnlock();
		}
	}

	public OTDataObject getOrCreateActiveDeltaObject()
	{
		if(activeDeltaObject == null) {
			try {
				database.readLock();
    			activeDeltaObject = database.createActiveDeltaObject(baseObject);			
    			activeDeltaObject.setContainer(this.container);
    			activeDeltaObject.setContainerResourceKey(this.containerResourceKey);
    			if (database.shouldPullAllAttributesIntoCurrentLayer()) {
    				pullUpModifiedResources(activeDeltaObject);
    			}
			} finally {
				database.readUnlock();
			}
		}
		
		return activeDeltaObject;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#setResource(java.lang.String, java.lang.Object)
	 */
	public boolean setResource(String key, Object resource)
	{
		try {
			database.readLock();
			return realSetResource(key, resource);
		} finally {
			database.readUnlock();
		}
	}
	
	private boolean realSetResource(String key, Object resource)
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
	    	if (database.shouldPullAllAttributesIntoCurrentLayer()) {
	    		// TODO We need to find this object's closest composite parent, and
	    		// make sure that it gets the resourcekey which leads to this object
	    		// written in the current delta layer
	    		database.writeClosestCompositeParentKey(this);
	    	}
	    	if (resource instanceof OTID) {
	    		database.recordReference(getGlobalId(), (OTID) resource, key);
	    	}
	    	return baseObject.setResource(key, resource);
	    }
	    
	    // get the existing active delta object or create a new one
	    OTDataObject localActiveDelta = getOrCreateActiveDeltaObject();
    	if (resource instanceof OTID) {
    		database.recordReference(getGlobalId(), (OTID) resource, key);
    	}
		boolean ret = localActiveDelta.setResource(key, resource);
		if (database.shouldPullAllAttributesIntoCurrentLayer()) {
			pullUpModifiedResources(localActiveDelta);
		}
		return ret;
	}
	
    private void pullUpModifiedResources(OTDataObject localActiveDelta)
    {
		// Only set resources that have been set/modified in any of the overlay layers or the loaded student data
		if (!alreadyPulledUp && middleDeltas != null) {
			for (String key : getResourceKeys()) {
				for (OTDataObject middle : middleDeltas) {
					if (middle.containsKey(key)) {
						Object resource = getResource(key);
						if (resource != null) {
							localActiveDelta.setResource(key, resource);
							break; // go to the next key
						}
					}
				}
			}
		}
		alreadyPulledUp = true;
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
		try {
			database.readLock();
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
		} finally {
			database.readUnlock();
		}
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
		try {
			database.readLock();
    		OTDataObject deltaObject = getActiveDeltaObject();
    		if(deltaObject == null){
    			return false;
    		}
    		
    		return deltaObject.containsKey(key);
		} finally {
			database.readUnlock();
		}
	}

	public void removeOverrideInTopOverlay(String name)
    {
		try {
			database.readLock();
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
		} finally {
			database.readUnlock();
		}
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
			try {
				database.readLock();
    			if (getActiveDeltaObject() != null) {
    				return true;
    			}
			} finally {
				database.readUnlock();
			}
		} else {
			return true;
		}
		return false;
	}
	
	public boolean isComposite() {
		return composite;
	}
	
	public void resetBaseObject() {
		if (!composite) {
    		try {
    			OTID baseId = baseObject.getGlobalId();
    			if (! database.getActiveOverlayDb().contains(baseId)) {
        			if (baseId instanceof OTTransientMapID) {
        				baseId = baseId.getMappedId();
        			}
            		OTDataObject newBase = database.getActiveOverlayDb().createDataObject(baseObject.getType(), baseId);
            		for (String key : baseObject.getResourceKeys()) {
            			newBase.setResource(key, baseObject.getResource(key));
            		}
            		newBase.setContainer(baseObject.getContainer());
            		newBase.setContainerResourceKey(baseObject.getContainerResourceKey());
            		if (newBase instanceof XMLDataObject && baseObject instanceof XMLDataObject) {
            			((XMLDataObject)newBase).setPreserveUUID(((XMLDataObject) baseObject).isPreserveUUID());
            		}
            		baseObject = newBase;
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
		} else {
			
		}
	}
	
	public OTDataObject getContainer()
    {
		if (composite) {
			if (activeDeltaObject != null) {
				return activeDeltaObject.getContainer();
			}
			return this.container;
		}
		return baseObject.getContainer();
    }

	public void setContainer(OTDataObject container)
    {
		OTDataObject parentDO = getParentDO(container);
		if (composite) {
			if (activeDeltaObject != null) {
				activeDeltaObject.setContainer(parentDO);
			}
		} else {
			baseObject.setContainer(parentDO);
		}
		this.container = parentDO;
    }
	
	private OTDataObject getParentDO(OTDataObject container) {
		if (container instanceof CompositeDataObject) {
			CompositeDataObject compDO = (CompositeDataObject) container;
			if (compDO.isComposite()) {
				return compDO.getActiveDeltaObject();
			}
			return compDO.getBaseObject();
		}
		return container;
	}

	public String getContainerResourceKey()
    {
		if (composite) {
			if (activeDeltaObject != null) {
				return activeDeltaObject.getContainerResourceKey();
			}
			return this.containerResourceKey;
		}
		return baseObject.getContainerResourceKey();
    }

	public void setContainerResourceKey(String containerResourceKey)
    {
		if (composite) {
			if (activeDeltaObject != null) {
				activeDeltaObject.setContainerResourceKey(containerResourceKey);
			}
		} else {
			baseObject.setContainerResourceKey(containerResourceKey);
		}
		this.containerResourceKey = containerResourceKey;
    }
}
