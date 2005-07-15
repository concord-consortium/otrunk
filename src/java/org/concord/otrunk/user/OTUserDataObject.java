
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
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2005-07-15 20:26:19 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.user;

import java.util.Hashtable;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTIDFactory;
import org.concord.otrunk.datamodel.OTObjectRevision;
import org.concord.otrunk.datamodel.OTRelativeID;
import org.concord.otrunk.xml.XMLResourceList;
import org.concord.otrunk.xml.XMLResourceMap;


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
	private OTID userId;
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
	        return database.getStateObject(authoringObject);
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
		(new Exception("user data object get resource keys not implemented")).printStackTrace();
		return null;
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
    
	public OTResourceCollection getResourceCollection(String key, Class collectionClass)
	{
	    OTResourceCollection collection = 
	        (OTResourceCollection)resourceCollections.get(key);
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
		if(collectionClass.equals(OTResourceList.class)) {
			collection =  new OTUserResourceList(this, (OTResourceList)resourceObj, key);
		} else if(collectionClass.equals(OTResourceMap.class)) {
			collection =  new OTUserResourceMap(this, (OTResourceMap)resourceObj);
		}

		resourceCollections.put(key, collection);

	    return collection;
	}

}
