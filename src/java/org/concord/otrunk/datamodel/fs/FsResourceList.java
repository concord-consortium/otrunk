/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2005-03-10 03:52:25 $
 * $Author: imoncada $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel.fs;

import java.io.Serializable;
import java.util.Vector;

import org.concord.framework.otrunk.OTResourceList;


/**
 * FsResourceList
 * Class name and description
 *
 * Date created: Aug 23, 2004
 *
 * @author scott<p>
 *
 */
public class FsResourceList
	implements OTResourceList, Serializable
{
	Vector list = new Vector();
	boolean readOnly;
	private FsDataObject dataObject = null;
		
	FsResourceList(FsDataObject dataObject)
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
}
