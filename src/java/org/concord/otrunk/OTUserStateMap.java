/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-01-11 07:51:05 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import org.concord.framework.otrunk.OTUser;
import org.concord.otrunk.datamodel.OTDataObject;

/**
 * OTUser
 * Class name and description
 *
 * Date created: Aug 24, 2004
 *
 * @author scott<p>
 *
 */
public interface OTUserStateMap extends OTUser
{
	public OTDataObject getUserStateObject(OTDataObject authoringObject);
	public void setUserStateObject(OTDataObject authoringObject, OTDataObject userStateObject);
	
	public OTUserDataObject getUserDataObject(OTDataObject authoringObject);
}
