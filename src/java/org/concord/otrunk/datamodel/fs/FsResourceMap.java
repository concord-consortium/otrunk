/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2005-01-12 04:19:55 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel.fs;

import java.io.Serializable;
import java.util.Hashtable;

import org.concord.framework.otrunk.OTResourceMap;


/**
 * FsResourceMap
 * Class name and description
 *
 * Date created: Sep 29, 2004
 *
 * @author scott<p>
 *
 */
public class FsResourceMap
	implements OTResourceMap, Serializable
{
	Hashtable map = new Hashtable();
	boolean readOnly;
	private FsDataObject dataObject = null;
	
	FsResourceMap(FsDataObject dataObject)
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
		this.readOnly = readOnly;
	}
		
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceMap#put(java.lang.String, java.lang.Object)
	 */
	public void put(String key, Object resource)
	{
		if(readOnly) {
			// TODO should throw an exception
			return;
		}

		updateModifiedTime();

		map.put(key, resource);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceMap#get(java.lang.String)
	 */
	public Object get(String key)
	{
		return map.get(key);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceMap#getKeys()
	 */
	public String[] getKeys()
	{
		Object [] keys = map.keySet().toArray();
		String [] strKeys = new String [keys.length];
		System.arraycopy(keys, 0, strKeys, 0, keys.length);
		return strKeys;
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceCollection#size()
	 */
	public int size()
	{
		return map.size();
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceCollection#removeAll()
	 */
	public void removeAll()
	{
		map.clear();
	}

}
