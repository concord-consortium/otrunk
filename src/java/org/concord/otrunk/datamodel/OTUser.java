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

import org.doomdark.uuid.UUID;


/**
 * OTUser
 * Class name and description
 *
 * Date created: Aug 24, 2004
 *
 * @author scott<p>
 *
 */
public interface OTUser
{
	public OTDataObject getUserStateObject(OTDataObject authoringObject);
	public void setUserStateObject(OTDataObject authoringObject, OTDataObject userStateObject);
	
	public UUID getUserId();
	public String getName();
	
	public OTUserDataObject getUserDataObject(OTDataObject authoringObject);
}
