/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-11-12 02:02:51 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import org.doomdark.uuid.UUID;


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
	public UUID getGlobalId();
	
	public String getName();
	public void setName(String name);
}
