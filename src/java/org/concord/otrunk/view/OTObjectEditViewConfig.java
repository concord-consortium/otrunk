/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2007-03-13 11:55:41 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2007 The Concord Consortium 
*/
package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTObjectList;


/**
 * OTObjectEditViewConfig
 * Class name and description
 *
 * Date created: Feb 19, 2007
 *
 * @author Ingrid Moncada<p>
 *
 */
public interface OTObjectEditViewConfig
	extends org.concord.framework.otrunk.view.OTViewEntry
{
	//This property is to specify the list of objects that can be inserted
	//into the document using its edit view
	public OTObjectList getObjectsToInsert();
	public void setObjectsToInsert(OTObjectList list);
}
