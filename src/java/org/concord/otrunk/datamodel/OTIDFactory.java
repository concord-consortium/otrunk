/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2004-12-17 20:09:18 $
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
		try {
			return new OTUUID(id);
		} catch (Exception e) {
			return null;
		}
	}
}
