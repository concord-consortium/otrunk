/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-10-25 05:33:57 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;


/**
 * OTResourceMap
 * Class name and description
 *
 * Date created: Sep 29, 2004
 *
 * @author scott<p>
 *
 */
public interface OTResourceMap extends OTResourceCollection
{
	public void put(String key, Object resource);
	public Object get(String key);
	
	public String [] getKeys();
}
