/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-11-12 02:02:51 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel;

import java.io.Serializable;
import java.util.Date;

import org.doomdark.uuid.EthernetAddress;
import org.doomdark.uuid.NativeInterfaces;
import org.doomdark.uuid.UUID;
import org.doomdark.uuid.UUIDGenerator;


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
	UUID revisionId = null;
	OTObjectRevision ancestor;
	Date modifiedTime;
	boolean synced;
	
	public OTObjectRevision(OTObjectRevision ancestor)
	{
		// silly change again and again and again and again
		// from eclipse
		this.ancestor = ancestor;

		UUIDGenerator generator = UUIDGenerator.getInstance();
    	EthernetAddress hwAddress = NativeInterfaces.getPrimaryInterface();
    	revisionId = generator.generateTimeBasedUUID(hwAddress);
    	
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
	protected UUID getRevisionId()
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

