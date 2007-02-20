/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2007-02-20 01:59:20 $
 * $Author: imoncada $
 *
 * Licence Information
 * Copyright 2007 The Concord Consortium 
*/
package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTObjectInterface;
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
	extends OTObjectInterface
{
	//This property is to specify the list of objects that can be inserted
	//into the document using its edit view
	public OTObjectList getObjectsToInsert();
	public void setObjectsToInsert(OTObjectList list);
}
