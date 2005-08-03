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
 * $Revision: 1.7 $
 * $Date: 2005-08-03 20:52:23 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel.fs;

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTObjectRevision;
import org.concord.otrunk.datamodel.OTUUID;


/**
 * FsDataObject
 * Class name and description
 *
 * Date created: Aug 22, 2004
 *
 * @author scott<p>
 *
 */
public class FsDataObject
	implements OTDataObject, Serializable
{
	private OTID globalId;
	Hashtable resources = new Hashtable();
	Date creationTime = null;
	Date modifiedTime = null;
	FsDatabase database = null;
	
	public final static String CURRENT_REVISION = "currentRevision";
	
	FsDataObject(OTUUID id, FsDatabase db)
	{
		globalId = id;
		database = db;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getGlobalId()
	 */
	public OTID getGlobalId()
	{
		return globalId;
	}

	/* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDataObject#getDatabase()
     */
    public OTDatabase getDatabase()
    {
        return database;
    }
	
	void creationInit()
	{
		resources.put(CURRENT_REVISION, new OTObjectRevision(null));
	}
	
	public OTObjectRevision getCurrentRevision()
	{
		// TODO go through resource lists and check their modified times
		return (OTObjectRevision)getResource(CURRENT_REVISION);
	}
	
	void updateModifiedTime()
	{
		OTObjectRevision revision = getCurrentRevision();

		// Check if the current revision has been synced anywhere
		// if it has already been synced then we need to make a new revision
		// version.
		if(revision.getSynced()) {
			resources.put(CURRENT_REVISION, new OTObjectRevision(revision));
		}
		
		// If the current revision has not been synced then we don't
		// need to make a new revision.
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#setResource(java.lang.String, java.lang.Object)
	 */
	public void setResource(String key, Object resource)
	{
		Object oldObject = resources.get(key);		
		resources.put(key, resource);
		
		if(oldObject == null || !oldObject.equals(resource)) {
			updateModifiedTime();
		}		
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getResource(java.lang.String)
	 */
	public Object getResource(String key)
	{
		return resources.get(key);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getResourceKeys()
	 */
	public String[] getResourceKeys()
	{
		Object [] keys = resources.keySet().toArray();
		String [] strKeys = new String [keys.length];
		System.arraycopy(keys, 0, strKeys, 0, keys.length);
		return strKeys;
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getResourceList(java.lang.String)
	 */
	public OTResourceCollection getResourceCollection(String key, Class collectionClass)
	{
		Object listObj = resources.get(key);
		if(collectionClass.isInstance(listObj)) {
			return (OTResourceCollection)listObj;
		}
		
		if(listObj != null) {
			// an non list object is stored in this resource slot
			// probably we should throw an exception
			return null;
		}
		
		// create a resource list object
		// add it as a resource with this name
		if(collectionClass == OTResourceList.class) {
		    FsResourceList list = new FsResourceList(this);
		    resources.put(key, list);
		    return list;
		} else if(collectionClass == OTResourceMap.class) {
		    FsResourceMap map = new FsResourceMap(this);
		    resources.put(key, map);
		    return map;
		}
		
		return null;
	}

	
}
