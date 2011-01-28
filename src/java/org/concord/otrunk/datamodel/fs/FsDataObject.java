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
 * $Revision: 1.11 $
 * $Date: 2007-06-27 21:35:14 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel.fs;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import org.concord.framework.otrunk.OTID;
import org.concord.otrunk.datamodel.OTDataCollection;
import org.concord.otrunk.datamodel.OTDataList;
import org.concord.otrunk.datamodel.OTDataMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDataObjectType;
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
	private static final long serialVersionUID = 1L;
	
	private OTID globalId;
	HashMap<String, Object> resources = new HashMap<String, Object>();
	Date creationTime = null;
	Date modifiedTime = null;
	FsDatabase database = null;

	protected OTDataObjectType type;

	private FsDataObject container;
	private String containerResourceKey;
	
	public final static String CURRENT_REVISION = "currentRevision";
	
	FsDataObject(OTDataObjectType type, OTUUID id, FsDatabase db)
	{
		this.type = type;
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
	public boolean setResource(String key, Object resource)
	{
		Object oldObject = resources.get(key);		
		resources.put(key, resource);
		
		if(oldObject == null || !oldObject.equals(resource)) {
			updateModifiedTime();
		}		
		
		return true;
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
	@SuppressWarnings("unchecked")
    public <T extends OTDataCollection> T getResourceCollection(String key, Class<T> collectionClass)
	{
		Object listObj = resources.get(key);
		if(collectionClass.isInstance(listObj)) {
			return (T)listObj;
		}
		
		if(listObj != null) {
			// an non list object is stored in this resource slot
			// probably we should throw an exception
			return null;
		}
		
		// create a resource list object
		// add it as a resource with this name
		if(collectionClass == OTDataList.class) {
		    FsDataList list = new FsDataList(this);
		    resources.put(key, list);
		    return (T)list;
		} else if(collectionClass == OTDataMap.class) {
		    FsDataMap map = new FsDataMap(this);
		    resources.put(key, map);
		    return (T)map;
		}
		
		return null;
	}

	public OTDataObjectType getType()
    {
		return type;
    }

	public URL getCodebase()
    {
	    // TODO Auto-generated method stub
	    return null;
    }

	public boolean containsKey(String key)
    {
		return resources.containsKey(key);
    }

	public OTDataObject getContainer()
    {
	    return container;
    }

	public void setContainer(OTDataObject container)
    {
		if (container == null || container instanceof FsDataObject) {
			this.container = (FsDataObject) container;
		} else {
			throw new RuntimeException("FsDataObject can only have another FsDataObject as a container.");
		}
    }

	public String getContainerResourceKey()
    {
	    return containerResourceKey;
    }

	public void setContainerResourceKey(String containerResourceKey)
    {
	    this.containerResourceKey = containerResourceKey;
    }
}
