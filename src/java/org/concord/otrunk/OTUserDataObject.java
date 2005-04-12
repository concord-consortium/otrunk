
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
 * $Revision: 1.9 $
 * $Date: 2005-04-12 05:26:25 $
 * $Author: imoncada $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.util.Hashtable;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTObjectRevision;
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
	private OTUserStateMap user;
	private OTrunkImpl otDatabase;
	private Hashtable resourceCollections = new Hashtable();
	
	private final class OTUserResourceList 
		implements OTResourceList {
		private OTUserDataObject parent;
		private OTResourceList authoredList;
		
		OTUserResourceList(OTUserDataObject parent, OTResourceList authoredList)
		{
		    this.parent = parent;
			this.authoredList = authoredList;
		}
				
		public void set(int index, Object object)
		{
			// this is read only
			return;
		}

		/* (non-Javadoc)
		 * @see org.concord.otrunk.OTResourceList#add(int, java.lang.Object)
		 */
		public void add(int index, Object object)
		{
			// this is read only
			return;
		}
	
		/* (non-Javadoc)
		 * @see org.concord.otrunk.OTResourceList#add(java.lang.Object)
		 */
		public void add(Object object)
		{
			return;
		}

		
		/* (non-Javadoc)
		 * @see org.concord.otrunk.OTResourceList#removeAll()
		 */
		public void removeAll()
		{
			
		}
		
		/* (non-Javadoc)
		 * @see org.concord.otrunk.OTResourceList#get(int)
		 */
		public Object get(int index)
		{
		    // FIXME this should check the user object
		    if(authoredList == null) return null;
		    
			return authoredList.get(index);
		}
				
		/* (non-Javadoc)
		 * @see org.concord.otrunk.OTResourceList#setReadOnly(boolean)
		 */
		public void setReadOnly(boolean readOnly)
		{
			// TODO Auto-generated method stub
		}
				
		/* (non-Javadoc)
		 * @see org.concord.otrunk.OTResourceList#size()
		 */
		public int size()
		{
		    if(authoredList == null) return 0;
		    
			return authoredList.size();
		}

		/* (non-Javadoc)
		 * @see org.concord.framework.otrunk.OTResourceList#remove(int)
		 */
		public void remove(int index)
		{
			// this is read only
			return;
		}

		/* (non-Javadoc)
		 * @see org.concord.framework.otrunk.OTResourceList#remove(java.lang.Object)
		 */
		public void remove(Object obj)
		{
			// this is read only
			return;
		}				
	}
	
	private final class OTUserResourceMap
		implements OTResourceMap
	{
	    OTUserDataObject parent;
	    OTResourceMap authoredMap;
	    
	    public OTUserResourceMap(OTUserDataObject parent, OTResourceMap authoredMap)
	    {
	        this.parent = parent;
	        this.authoredMap = authoredMap;
	    }
	    
	    /* (non-Javadoc)
         * @see org.concord.framework.otrunk.OTResourceMap#get(java.lang.String)
         */
        public Object get(String key)
        {
            // TODO Auto-generated method stub
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.concord.framework.otrunk.OTResourceMap#getKeys()
         */
        public String[] getKeys()
        {
            // TODO Auto-generated method stub
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.concord.framework.otrunk.OTResourceMap#put(java.lang.String, java.lang.Object)
         */
        public void put(String key, Object resource)
        {
            // TODO Auto-generated method stub

        }
        
        /* (non-Javadoc)
         * @see org.concord.framework.otrunk.OTResourceCollection#removeAll()
         */
        public void removeAll()
        {
            // TODO Auto-generated method stub

        }
        
        /* (non-Javadoc)
         * @see org.concord.framework.otrunk.OTResourceCollection#size()
         */
        public int size()
        {
            // TODO Auto-generated method stub
            return 0;
        }
	}
	
		
	public OTUserDataObject(OTDataObject authoringObject, OTUserStateMap user, OTrunkImpl db)
	{
		this.authoringObject = authoringObject;
		this.user = user;
		otDatabase = db;
	}
	
	public OTDataObject getAuthoringObject()
	{
		return authoringObject;
	}

	public OTDataObject getUserObject()
	{
		return user.getUserStateObject(authoringObject);
	}
			
	public OTUserStateMap getUser()
	{
		return user;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getGlobalId()
	 */
	public OTID getGlobalId()
	{
	    // FIXME we need to verify this will work.  Instead of returning
	    // a reall globalId we'll just return ourselves. 
	    
	    // one important note is that this means when an object is
	    // requested the returned object might not have id as the requested
	    // object
	    return this;	    
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
		OTDataObject userObject = getUserObject();
		if (userObject != null) {
			value = userObject.getResource(key);
			if(value != null) {
				return value;
			}
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
		
	protected OTDataObject createUserObject()
	{
		OTDataObject userObject = null;
		
		try{
			
			userObject = otDatabase.createDataObject();
			// store some special resources in this object
			// so we know where it came from:
			userObject.setResource("user-id", user.getUserId());
			userObject.setResource("authoring-id", authoringObject.getGlobalId());

			user.setUserStateObject(authoringObject, userObject);
			
		}
		catch (Exception e){
			e.printStackTrace();
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
	    
	    OTDataObject userObject = getUserObject();
		
		if(userObject == null) {
			userObject = createUserObject();
		}
		// add the resource to the user object not the author object
		userObject.setResource(key, resource);
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
		Object resourceObj = authoringObject.getResource(key);

		// Here is the tricky part.  We want to make a pseudo
		// list so that the real list isn't created unless it is really
		// used.
		if(collectionClass.equals(OTResourceList.class)) {
			collection =  new OTUserResourceList(this, (OTResourceList)resourceObj);
		} else if(collectionClass.equals(OTResourceMap.class)) {
			collection =  new OTUserResourceMap(this, (OTResourceMap)resourceObj);
		}

		resourceCollections.put(key, collection);

	    return collection;
	}

}
