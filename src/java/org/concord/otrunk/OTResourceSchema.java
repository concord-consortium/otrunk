/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2004-12-06 03:51:34 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import org.concord.framework.otrunk.OTID;



/**
 * OTResourceSchema
 * Class name and description
 *
 * Date created: Nov 11, 2004
 *
 * @author scott<p>
 *
 */
public interface OTResourceSchema
{
	public OTID getGlobalId();
	
	public String getName();
	public void setName(String name);
}
