/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2004-12-06 03:51:35 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;

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
	
	public OTID getUserId();
	public String getName();
	
	public OTUserDataObject getUserDataObject(OTDataObject authoringObject);
}
