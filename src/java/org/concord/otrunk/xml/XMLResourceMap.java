/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2005-01-25 16:19:41 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Hashtable;

import org.concord.framework.otrunk.OTResourceMap;


/**
 * XMLResourceMap
 * Class name and description
 *
 * Date created: Oct 13, 2004
 *
 * @author scott<p>
 *
 */
public class XMLResourceMap implements OTResourceMap
{
	Hashtable hTable = new Hashtable();
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceMap#put(java.lang.String, java.lang.Object)
	 */
	public void put(String key, Object resource)
	{
		hTable.put(key, resource);		
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceMap#get(java.lang.String)
	 */
	public Object get(String key)
	{
		return hTable.get(key);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceMap#getKeys()
	 */
	public String[] getKeys()
	{
		Object [] keys = hTable.keySet().toArray();
		String [] strKeys = new String [keys.length];
		System.arraycopy(keys, 0, strKeys, 0, keys.length);
		return strKeys;
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceCollection#size()
	 */
	public int size()
	{
		return hTable.size();
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceCollection#removeAll()
	 */
	public void removeAll()
	{
		hTable.clear();		
	}
	
	void remove(String key)
	{
		hTable.remove(key);
	}
}
