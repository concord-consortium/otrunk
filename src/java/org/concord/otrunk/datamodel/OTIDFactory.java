/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-06 03:51:35 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;


/**
 * OTIDFactory
 * Class name and description
 *
 * Date created: Dec 5, 2004
 *
 * @author scott<p>
 *
 */
public class OTIDFactory
{
	public static OTID createOTID(String id)
	{
		return new OTUUID(id);
	}
}
