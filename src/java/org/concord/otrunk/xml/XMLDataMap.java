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
 * $Date: 2007-07-25 20:25:34 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.HashMap;
import java.util.Hashtable;

import org.concord.otrunk.datamodel.OTDataMap;


/**
 * XMLResourceMap
 * Class name and description
 *
 * Date created: Oct 13, 2004
 *
 * @author scott<p>
 *
 */
public class XMLDataMap 
	implements OTDataMap
{
	Hashtable hTable = new Hashtable();
	XMLDataObject dataObject;
	
	HashMap referenceInfoMap = new HashMap();
	
	public XMLDataMap(XMLDataObject parent)
	{
	    dataObject = parent;
	    if(dataObject == null) {
	        throw new UnsupportedOperationException("passing null parent not allowed");
	    }
	}
	
	private void updateModifiedTime()
	{
		dataObject.updateModifiedTime();
	}
				
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceMap#put(java.lang.String, java.lang.Object)
	 */
	public void put(String key, Object resource)
	{
		hTable.put(key, resource);		
		updateModifiedTime();
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
		updateModifiedTime();
	}
	
	void remove(String key)
	{
		hTable.remove(key);
		updateModifiedTime();
	}
}
