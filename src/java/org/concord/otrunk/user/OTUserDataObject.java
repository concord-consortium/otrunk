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
 * $Revision: 1.9 $
 * $Date: 2007-02-20 00:16:39 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.user;

import java.util.Hashtable;

import org.concord.framework.otrunk.OTID;
import org.concord.otrunk.datamodel.OTDataCollection;
import org.concord.otrunk.datamodel.OTDataList;
import org.concord.otrunk.datamodel.OTDataMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTObjectRevision;
import org.concord.otrunk.datamodel.OTRelativeID;


/**
 * OTUserDataObject
 * Class name and description
 *
 * Date created: Aug 24, 2004
 *
 * @author scott<p>
 *
 */
public class OTUserDataObject
	implements OTDataObject, OTID
{
	private OTDataObject authoringObject;
	private OTDataObject stateObject = null;
	private OTTemplateDatabase database;
	
	private Hashtable resourceCollections = new Hashtable();
	
	public OTUserDataObject(OTDataObject authoringObject, 
	        OTTemplateDatabase db)
	{
		this.authoringObject = authoringObject;
		database = db;
	}
	
	public OTDatabase getDatabase()
	{
	    return database;
	}
	
	public OTDataObject getAuthoringObject()
	{
		return authoringObject;
	}

	public void setStateObject(OTDataObject stateObject)
	{
	    this.stateObject = stateObject;
	}
	
	public OTDataObject getExistingUserObject()
	{
	    if(stateObject != null) {
	        return stateObject;
	    } else {
            // I don't know if I should do this but it should speed things
            // up
            stateObject = database.getStateObject(authoringObject);
	        return stateObject;
	    }
	}
			
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getGlobalId()
	 */
	public OTID getGlobalId()
	{
	    OTID dbId = database.getDatabaseId();
	    
	    // The object can represent two types of data objects
	    // it is either a bridge between an authored object (template object)
	    // and the user modifications to that object object
	    // or it is a wrapper around a user created object
	    OTID otherId = null;
	    if(authoringObject != null) {
	        otherId = authoringObject.getGlobalId();
	    } else {
	        otherId = stateObject.getGlobalId();
	    }
	    
	    return new OTRelativeID(dbId, otherId);
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
		// first look in the userObject
		// then look in the authoringObject
		Object value = null;
		OTDataObject userObject = getExistingUserObject();
		if (userObject != null) {
			value = userObject.getResource(key);
			if(value != null) {
				return value;
			}
		}
		
		if(authoringObject == null) {
		    return null;
		}
		
		return authoringObject.getResource(key);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getResourceKeys()
	 */
	public String[] getResourceKeys()
	{
		// if this is called then it isn't clear what to do, I guess
		// some combination of the two objects.
        // it is needed to implement generic object copying
        // if we had access to the objects data description we could use
        // that in this case, but assuming we don't have that
        // we need to go through all the keys of the userobject and 
        // all the keys of the author object and combine them, an easy
        // way is to use a hashtable
        Hashtable keyTable = new Hashtable();
        OTDataObject userObject = getExistingUserObject();
        if (userObject != null) {
            String [] userKeys = userObject.getResourceKeys();
            for(int i=0; i<userKeys.length; i++){
                // we can put any object, but lets use the userObject
                // so we might take advantage of that later
                keyTable.put(userKeys[i], userObject);
            }
        }
        
        if(authoringObject != null) {
            String [] authorKeys = authoringObject.getResourceKeys();
            for(int i=0; i<authorKeys.length; i++){
                Object oldValue = keyTable.get(authorKeys[i]);
                if(oldValue == null){
                    keyTable.put(authorKeys[i], authoringObject);
                }
            }
        }

        Object [] keys = keyTable.keySet().toArray();
        String [] strKeys = new String [keys.length];
        System.arraycopy(keys, 0, strKeys, 0, keys.length);
        return strKeys;
	}

	public OTDataObject getUserObject()
	{
	    OTDataObject userObject = getExistingUserObject();
		
		if(userObject == null) {
			userObject = database.createStateObject(authoringObject);
			System.err.println("created userStateObject: " + userObject.getGlobalId());
		}
		
		return userObject;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#setResource(java.lang.String, java.lang.Object)
	 */
	public void setResource(String key, Object resource)
	{
	    Object oldObject = getResource(key);
	    if(oldObject != null &&
	            oldObject.equals(resource)){
	        return;
	    }
	    
	    // special hack for -0.0 and 0.0 
	    // see the docs for Float.equals()
	    if(oldObject instanceof Float && 
	            resource instanceof Float) {
	        if(((Float)oldObject).floatValue() == 
	            ((Float)resource).floatValue()){
	            return;
	        }
	    }
	    
	    // get the existing user object or create a new one
	    OTDataObject userObject = getUserObject();

        resource = resolveIDResource(resource);
        	    
		userObject.setResource(key, resource);
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
        if(resource instanceof OTRelativeID) {
            OTRelativeID relID = (OTRelativeID)resource;
            if(database.getDatabaseId().equals(relID.getRootId())) {
                // This is an id that came from our database
                // so get the relative part of the id which is 
                // either an id in the
                // authored database or an id of a brand new object in
                // the user database
                return relID.getRelativeId();
            }
        }
        
        return resource;
    }
    
	public OTDataCollection getResourceCollection(String key, 
			Class collectionClass)
	{
		OTDataCollection collection = 
	        (OTDataCollection)resourceCollections.get(key);
	    if(collection != null) {
	        return collection;
	    }
	    
	    // This might need to be getResourceCollection instead of 
	    // getResource.  But I wanted to know if the list has been
	    // set yet.
	    
		Object resourceObj = null;
		if(authoringObject != null) {
		    resourceObj = authoringObject.getResource(key);
		}

		// Here is the tricky part.  We want to make a pseudo
		// list so that the real list isn't created unless it is really
		// used.
		if(collectionClass.equals(OTDataList.class)) {
			collection =  
				new OTUserDataList(this, (OTDataList)resourceObj, key);
		} else if(collectionClass.equals(OTDataMap.class)) {
			collection =  
				new OTUserDataMap(this, (OTDataMap)resourceObj, key);
		}

		resourceCollections.put(key, collection);

	    return collection;
	}
}
