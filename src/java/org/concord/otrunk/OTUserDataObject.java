/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2005-01-11 07:51:05 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import org.concord.framework.otrunk.OTID;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTObjectRevision;
import org.concord.otrunk.datamodel.OTResourceList;


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
	implements OTDataObject
{
	private OTID userId;
	private OTDataObject authoringObject;
	private OTUserStateMap user;
	private OTrunkImpl otDatabase;
	
	private final class OtUserResourceList 
		implements OTResourceList {
		private OTResourceList authoredList;
		
		OtUserResourceList(OTResourceList authoredList)
		{
			this.authoredList = authoredList;
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
			return authoredList.size();
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
		// FIXME I'm not sure if this is the right thing to do here
		// in most cases returning the authoring id is the best thing
		// but I imagine that there can be problems with this
		return authoringObject.getGlobalId();		
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
		OTDataObject userObject = getUserObject();
		
		if(userObject == null) {
			userObject = createUserObject();
		}
		// add the resource to the user object not the author object
		userObject.setResource(key, resource);
	}
}
