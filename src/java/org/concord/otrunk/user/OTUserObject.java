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
 * $Date: 2007-03-09 05:29:25 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.user;

import java.util.Hashtable;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.OTUser;

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
	implements OTUser
{
	public static interface ResourceSchema extends OTResourceSchema {
		public OTResourceMap getUserDataMap();
	}
	
	private ResourceSchema resources;

	/**
	 * Cache of user data objects.  These are virtual data objects
	 * that have an authoring object and create a user state object
	 * if the user makes any change to the authoring object.
	 * 
	 * We keep this cache so we don't generate one of these objects more 
	 * than once
	 */
	Hashtable userDataObjects = new Hashtable();
	

	public OTUserObject(ResourceSchema resources) 
	{
		super(resources);
		this.resources = resources;		
	}
				
	/* (non-Javadoc)
	 * @see org.concord.portfolio.PfUser#getUserId()
	 */
	public OTID getUserId()
	{
		return resources.getGlobalId();
	}
	
	
}
