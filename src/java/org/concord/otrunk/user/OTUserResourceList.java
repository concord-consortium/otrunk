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
 * $Revision: 1.4 $
 * $Date: 2005-08-03 20:52:23 $
 * $Author: maven $
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
        object = parent.resolveIDResource(object);
	    getUserList().set(index, object);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#add(int, java.lang.Object)
	 */
	public void add(int index, Object object)
	{
        object = parent.resolveIDResource(object);
	    getUserList().add(index, object);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#add(java.lang.Object)
	 */
	public void add(Object object)
	{
        object = parent.resolveIDResource(object);
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
        obj = parent.resolveIDResource(obj);
	    getUserList().remove(obj);
	}				
}