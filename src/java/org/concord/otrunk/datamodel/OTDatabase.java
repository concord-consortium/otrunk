/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2004-12-06 03:51:35 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;


/**
 * OTDatabase
 * Class name and description
 *
 * Date created: Nov 8, 2004
 *
 * @author scott<p>
 *
 */
public interface OTDatabase
{
	public abstract void setRoot(OTID rootId) 
		throws Exception;
	
	public abstract OTDataObject getRoot() 
		throws Exception;	
	
	/**
	 * Make a brand new data object and create an id for it
	 * @return
	 * @throws Exception
	 */
	public abstract OTDataObject createDataObject() 
		throws Exception;
	
	/**
	 * Make a brand new data object and use an existing id
	 * this is required so objects can be imported into this database
	 * @return
	 * @throws Exception
	 */
	public abstract OTDataObject createDataObject(OTID id) 
		throws Exception;

	public abstract OTResourceCollection createCollection(OTDataObject parent, Class collectionClass) 
		throws Exception;
	
	/**
	 * The dataParent must be set so the database can correctly look up the 
	 * child object.
	 *  
	 * @param dataParent
	 * @param childID
	 * @return
	 * @throws Exception
	 */
	public abstract OTDataObject getOTDataObject(OTDataObject dataParent, OTID childID)
		throws Exception;
	
	public abstract void close();

}
