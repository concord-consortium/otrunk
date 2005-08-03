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
 * $Revision: 1.4 $
 * $Date: 2005-08-03 20:52:23 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel;

import java.io.Serializable;
import java.util.Date;

import org.concord.framework.otrunk.OTID;


/**
 * OTObjectRevision
 * Class name and description
 *
 * Date created: Sep 14, 2004
 *
 * @author scott<p>
 *
 */
public class OTObjectRevision
	implements Serializable
{
	OTID revisionId = null;
	OTObjectRevision ancestor;
	Date modifiedTime;
	boolean synced;
	
	public OTObjectRevision(OTObjectRevision ancestor)
	{
		// silly change again and again and again and again
		// from eclipse
		this.ancestor = ancestor;

		revisionId = OTUUID.createOTUUID();
    	
    	modifiedTime = new Date();
    	
    	synced = false;
	}
	
	public void setSynced(boolean flag)
	{
		synced = flag;
	}
	
	public boolean getSynced()
	{
		return synced;
	}
	
	
	/**
	 * @return Returns the ancestor.
	 */
	public OTObjectRevision getAncestor()
	{
		return ancestor;
	}
	
	/**
	 * @return Returns the modifiedTime.
	 */
	public Date getModifiedTime()
	{
		return modifiedTime;
	}

	
	/**
	 * @return Returns the revisionId.
	 */
	protected OTID getRevisionId()
	{
		return revisionId;
	}
	
	public boolean equals(Object obj)
	{
		if(obj instanceof OTObjectRevision) {
			OTObjectRevision objectRevision = (OTObjectRevision)obj;
			return objectRevision.getRevisionId().equals(getRevisionId());
		}
		
		return false;
	}
	
	/**
	 * @param revision
	 * @return
	 */
	public boolean isAncestorOf(OTObjectRevision revision)
	{
		OTObjectRevision ancestor = revision.getAncestor();

		while (ancestor != null) {
			if(ancestor.equals(this)) {
				return true;
			}
			ancestor = ancestor.getAncestor();
		} 
		
		return false;
	}
	
	public String toString()
	{
		String outString = "";
		OTObjectRevision revision = this;
		while (revision != null) {
			outString += revision.getModifiedTime() + "  " + revision.getRevisionId() + "\n";
			revision = revision.getAncestor();
		}
		
		return outString;
	}
}

