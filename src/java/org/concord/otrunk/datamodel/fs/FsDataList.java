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
 * $Revision: 1.2 $
 * $Date: 2007-03-09 05:26:13 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel.fs;

import java.io.Serializable;
import java.util.Vector;

import org.concord.otrunk.datamodel.OTDataList;


/**
 * FsResourceList
 * Class name and description
 *
 * Date created: Aug 23, 2004
 *
 * @author scott<p>
 *
 */
public class FsDataList
	implements OTDataList, Serializable
{
	private static final long serialVersionUID = 1L;
	
	Vector list = new Vector();
	boolean readOnly;
	private FsDataObject dataObject = null;
		
	FsDataList(FsDataObject dataObject)
	{
		this.dataObject = dataObject;
	}
		
	private void updateModifiedTime()
	{
		dataObject.updateModifiedTime();
	}
		
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly)
	{
		// TODO Auto-generated method stub
		this.readOnly = readOnly;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#size()
	 */
	public int size() {
		return list.size();
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#get(int)
	 */
	public Object get(int index) 
	{
		return list.get(index);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#add(java.lang.Object)
	 */
	public void add(Object object) 
	{
		if(readOnly) {
			// TODO should throw an exception
			return;
		}

		updateModifiedTime();
		list.add(object);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#add(int, java.lang.Object)
	 */
	public void add(int index, Object object) 
	{
		if(readOnly) {
			// TODO should throw an exception
			return;
		}

		updateModifiedTime();
		list.add(index, object);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#set(int, java.lang.Object)
	 */
	public void set(int index, Object object) 
	{
		if(readOnly) {
			// TODO should throw an exception
			return;
		}

		updateModifiedTime();
		list.set(index, object);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#removeAll()
	 */
	public void removeAll()
	{
		updateModifiedTime();
		list.removeAllElements();
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#remove(int)
	 */
	public void remove(int index)
	{
		updateModifiedTime();
		list.remove(index);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#remove(java.lang.Object)
	 */
	public void remove(Object obj)
	{
		updateModifiedTime();
		list.remove(obj);
	}
}
