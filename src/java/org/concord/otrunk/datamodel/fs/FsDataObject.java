/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-11-12 02:02:51 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel.fs;

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;

import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTObjectRevision;
import org.concord.otrunk.datamodel.OTResourceCollection;
import org.concord.otrunk.datamodel.OTResourceList;
import org.doomdark.uuid.UUID;


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
	private UUID globalId;
	Hashtable resources = new Hashtable();
	Date creationTime = null;
	Date modifiedTime = null;
		
	public final static String CURRENT_REVISION = "currentRevision";
	
	public FsDataObject(UUID id)
	{
		globalId = id;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getGlobalId()
	 */
	public UUID getGlobalId()
	{
		return globalId;
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
	public OTResourceCollection getResourceCollection(String key)
	{
		Object listObj = resources.get(key);
		if(listObj instanceof OTResourceList) {
			return (OTResourceCollection)listObj;
		}
		
		if(listObj != null) {
			// an non list object is stored in this resource slot
			// probably we should throw an exception
			return null;
		}
		
		// create a resource list object
		// add it as a resource with this name
		FsResourceList list = new FsResourceList(this);
		resources.put(key, list);
		return list;
	}

}
