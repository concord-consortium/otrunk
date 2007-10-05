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

import java.util.Hashtable;

import org.concord.framework.otrunk.OTID;
import org.concord.otrunk.datamodel.OTDataCollection;
import org.concord.otrunk.datamodel.OTDataList;
import org.concord.otrunk.datamodel.OTDataMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDataObjectType;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTObjectRevision;
import org.concord.otrunk.datamodel.OTTransientMapID;


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
	private OTDataObject baseObject;
	private OTDataObject activeDeltaObject = null;
	private OTDataObject [] middleDeltas;
	private CompositeDatabase database;
	
	
    // The object can represent two types of data objects
    // it is either a bridge between an authored object (template object)
    // and the user modifications to that object object
    // or it is a wrapper around a user created object
	private boolean composite;
	
	private Hashtable resourceCollections = new Hashtable();
	
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
			}
		}

		return getNonActiveDeltaResource(key);
	}

	protected Object getNonActiveDeltaResource(String key)
	{
		Object value = null;
		if(middleDeltas != null){
			for(int i=0; i<middleDeltas.length; i++){
				OTDataObject delta = middleDeltas[i];
				if (delta != null) {
					value = delta.getResource(key);
					if(value != null) {
						return value;
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

        Hashtable keyTable = new Hashtable();
        OTDataObject localActiveDelta = getActiveDeltaObject();
        if (localActiveDelta != null) {
            String [] userKeys = localActiveDelta.getResourceKeys();
            for(int i=0; i<userKeys.length; i++){
                // we can put any object, but lets use the userObject
                // so we might take advantage of that later
                keyTable.put(userKeys[i], localActiveDelta);
            }
        }
        
		if(middleDeltas != null){
			for(int i=0; i<middleDeltas.length; i++){
				OTDataObject delta = middleDeltas[i];
	            String [] userKeys = delta.getResourceKeys();
	            for(int j=0; j<userKeys.length; j++){
	                keyTable.put(userKeys[j], delta);
	            }				
			}
		}
        
        if(baseObject != null) {
            String [] authorKeys = baseObject.getResourceKeys();
            for(int i=0; i<authorKeys.length; i++){
                Object oldValue = keyTable.get(authorKeys[i]);
                if(oldValue == null){
                    keyTable.put(authorKeys[i], baseObject);
                }
            }
        }

        Object [] keys = keyTable.keySet().toArray();
        String [] strKeys = new String [keys.length];
        System.arraycopy(keys, 0, strKeys, 0, keys.length);
        return strKeys;
	}

	public OTDataObject getOrCreateActiveDeltaObject()
	{
	    OTDataObject localActiveDelta = getActiveDeltaObject();
		
		if(localActiveDelta == null) {
			localActiveDelta = database.createActiveDeltaObject(baseObject);
			System.err.println("created delta object: " + localActiveDelta.getGlobalId().toExternalForm());
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
    
	public OTDataCollection getResourceCollection(String key, 
			Class collectionClass)
	{
		OTDataCollection collection = (OTDataCollection)resourceCollections.get(key);
	    if(collection != null) {
	        return collection;
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

	    return collection;
	}

	/**
	 * We do not allow switching the type of the object on the fly, so the type of the object
	 * will always be the type of the base object
	 */
	public OTDataObjectType getType()
    {
		return baseObject.getType();
    }
}
