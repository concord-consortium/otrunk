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
 * $Revision: 1.5 $
 * $Date: 2007-02-09 22:04:47 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.user;

import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.framework.otrunk.OTResourceList;


final class OTUserResourceList extends OTUserResourceCollection
	implements OTResourceList {
	
	
	OTUserResourceList(OTUserDataObject parent, OTResourceList authoredList,
	        String resourceName)
	{
		super(OTResourceList.class, parent, authoredList, resourceName);
	}

	private OTResourceList getUserList()
	{
		return (OTResourceList)getUserCollection();
	}
	
	private OTResourceList getListForRead()
	{
		return (OTResourceList)getCollectionForRead();
	}
	
	protected void copyInto(OTResourceCollection userCollection,
			OTResourceCollection authoredCollection)
	{
		OTResourceList authoredList = (OTResourceList)authoredCollection;
		OTResourceList userList = (OTResourceList) userCollection;
		
		for(int i=0; i<authoredList.size(); i++) {
			userList.add(authoredList.get(i));
		}
	}

	public void set(int index, Object object)
	{
        object = resolveIDResource(object);
	    getUserList().set(index, object);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#add(int, java.lang.Object)
	 */
	public void add(int index, Object object)
	{
        object = resolveIDResource(object);
	    getUserList().add(index, object);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#add(java.lang.Object)
	 */
	public void add(Object object)
	{
        object = resolveIDResource(object);
	    getUserList().add(object);
	}

	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#get(int)
	 */
	public Object get(int index)
	{
		OTResourceList listForRead = getListForRead();

		if(listForRead == null) return null;

		return listForRead.get(index);		
	}
			
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly)
	{
		// TODO Auto-generated method stub
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
        obj = resolveIDResource(obj);
	    getUserList().remove(obj);
	}				
}