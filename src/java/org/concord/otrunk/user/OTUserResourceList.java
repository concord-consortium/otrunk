/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-04-24 15:44:55 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.user;

import java.util.Vector;

import org.concord.framework.otrunk.OTResourceList;
import org.concord.otrunk.datamodel.OTDataObject;


final class OTUserResourceList 
	implements OTResourceList {
	private OTUserDataObject parent;
	private OTResourceList authoredList;
	private String resourceName;
	
	
	OTUserResourceList(OTUserDataObject parent, OTResourceList authoredList,
	        String resourceName)
	{
	    this.parent = parent;
		this.authoredList = authoredList;
		this.resourceName = resourceName;
	}

	private OTResourceList getExistingUserList()
	{
	    OTDataObject userState = parent.getExistingUserObject();
	    if(userState == null) {
	        return null;
	    }
	    
	    Object oldList = userState.getResource(resourceName);
        return (OTResourceList)oldList;	    
	}
	
	private OTResourceList getUserList()
	{
	    OTDataObject userState = parent.getUserObject();
	    Object oldList = userState.getResource(resourceName);
	    if(oldList != null) {
	        return (OTResourceList)oldList;
	    }

	    OTResourceList userList = (OTResourceList)userState.getResourceCollection(resourceName, OTResourceList.class);
	    if(authoredList != null) {
	        for(int i=0; i<authoredList.size(); i++) {
	            userList.add(authoredList.get(i));
	        }
	    }
	    return userList;
	}
	
	public void set(int index, Object object)
	{
	    getUserList().set(index, object);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#add(int, java.lang.Object)
	 */
	public void add(int index, Object object)
	{
	    getUserList().add(index, object);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#add(java.lang.Object)
	 */
	public void add(Object object)
	{
	    getUserList().add(object);
	}

	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#removeAll()
	 */
	public void removeAll()
	{
	    getUserList().removeAll();
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#get(int)
	 */
	public Object get(int index)
	{
	    OTResourceList userList = getExistingUserList();
	    if(userList != null) {
	        return userList.get(index);
	    }
	    
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
	    OTResourceList userList = getExistingUserList();
	    if(userList != null) {
	        return userList.size();
	    }
	    
	    if(authoredList == null) return 0;
	    
		return authoredList.size();
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#remove(int)
	 */
	public void remove(int index)
	{
	    getUserList().remove(index);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#remove(java.lang.Object)
	 */
	public void remove(Object obj)
	{
	    getUserList().remove(obj);
	}				
}