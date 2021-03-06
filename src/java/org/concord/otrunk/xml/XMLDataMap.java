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
 * $Date: 2007-10-16 13:34:08 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.concord.framework.otrunk.OTID;
import org.concord.otrunk.datamodel.OTDataMap;
import org.concord.otrunk.datamodel.OTDataObject;


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
	HashMap<String, Object> hTable = new LinkedHashMap<String, Object>();
	XMLDataObject dataObject;
	XMLDatabase db;
	
	public XMLDataMap(XMLDataObject parent)
	{
	    dataObject = parent;
	    if(dataObject == null) {
	        throw new UnsupportedOperationException("passing null parent not allowed");
	    }
	    db = (XMLDatabase)dataObject.getDatabase();
	}
	
	private void updateModifiedTime()
	{
		dataObject.updateModifiedTime();
	}
				
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceMap#put(java.lang.String, java.lang.Object)
	 */
	public Object put(String key, Object resource)
	{
		Object previousValue = hTable.put(key, resource);		
		updateModifiedTime();
		if (previousValue != null) {
			removeReference(previousValue);
		}
		recordReference(resource);
		return previousValue;
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
		for (String key : hTable.keySet()) {
			Object obj = hTable.get(key);
			removeReference(obj);
		}
		hTable.clear();		
		updateModifiedTime();
	}
	
	public void remove(String key)
	{
		Object obj = hTable.get(key);
		removeReference(obj);
		hTable.remove(key);
		updateModifiedTime();
	}
	
	private void recordReference(Object resource)
    {
	    if (resource instanceof OTDataObject) {
			db.recordReference(dataObject, (OTDataObject) resource, "unknown");
		} else if (resource instanceof OTID) {
			db.recordReference(dataObject.getGlobalId(), (OTID) resource, "unknown");
		}
    }

	private void removeReference(Object previousValue)
    {
	    if (previousValue instanceof OTDataObject) {
	    	db.removeReference(dataObject, (OTDataObject) previousValue);
	    } else if (previousValue instanceof OTID) {
	    	db.removeReference(dataObject.getGlobalId(), (OTID) previousValue);
	    }
    }
}
