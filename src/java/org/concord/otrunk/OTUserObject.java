/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2005-01-12 04:19:54 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.util.Hashtable;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.otrunk.datamodel.OTDataObject;

/**
 * PfUserObject
 * Class name and description
 *
 * Date created: Aug 25, 2004
 *
 * @author scott<p>
 *
 */
public class OTUserObject extends DefaultOTObject
	implements OTUserStateMap
{
	public static interface ResourceSchema extends OTResourceSchema {
		public OTResourceMap getUserDataMap();
	}
	
	private ResourceSchema resources;
	public OTUserObject(ResourceSchema resources) 
	{
		super(resources);
		this.resources = resources;		
	}
				
	/**
	 * Cache of user data objects.  These are virtual data objects
	 * that have an authoring object and create a user state object
	 * if the user makes any change to the authoring object.
	 * 
	 * We keep this cache so we don't generate one of these objects more 
	 * than once
	 */
	Hashtable userDataObjects = new Hashtable();
	

	/* (non-Javadoc)
	 * @see org.concord.portfolio.PfUser#getUserId()
	 */
	public OTID getUserId()
	{
		return getGlobalId();
	}
	

	/* (non-Javadoc)
	 * @see org.concord.portfolio.PfUser#getUserStateObject(org.concord.portfolio.PfDataObject)
	 */
	public OTDataObject getUserStateObject(OTDataObject authoringObject)
	{
		OTID authoringId = authoringObject.getGlobalId();
		OTResourceMap userDataMap = resources.getUserDataMap();
		OTID userStateId = (OTID)userDataMap.get(authoringId.toString());

		if(userStateId == null) {
			return null;
		}
		
		OTrunkImpl db = (OTrunkImpl)getOTDatabase();
		
		try{
			return db.getOTDataObject(authoringObject, userStateId);			
		}
		catch (Exception e){
			e.printStackTrace();
		}

		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.PfUser#setUserStateObject(org.concord.portfolio.PfDataObject, org.concord.portfolio.PfDataObject)
	 */
	public void setUserStateObject(OTDataObject authoringObject,
			OTDataObject userStateObject)
	{
		OTID authoringId = authoringObject.getGlobalId();
		OTID userStateId = userStateObject.getGlobalId();

		OTResourceMap userDataMap = resources.getUserDataMap();
		userDataMap.put(authoringId.toString(), userStateId);
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.PfUser#getUserDataObject(org.concord.otrunk.PfDataObject)
	 */
	public OTUserDataObject getUserDataObject(OTDataObject authoringObject)
	{
		
		OTUserDataObject userDataObject = (OTUserDataObject)userDataObjects.get(authoringObject);
		
		if(userDataObject == null) {
			userDataObject = new OTUserDataObject(authoringObject, this, (OTrunkImpl)getOTDatabase());
			userDataObjects.put(authoringObject, userDataObject);
		}
		
		// TODO should add a listener to this object so we know when a user state
		// has been created.  Then we can remove the getUserState from the user object
		// or we can just traverse the user data object when we are saved, and 
		// record which ones have state objects.
		// unfortunately there is no "when we are saved"
		return userDataObject;
	}
}
