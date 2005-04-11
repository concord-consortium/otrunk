
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
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
 */

/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2005-04-11 15:01:08 $
 * $Author: maven $
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
