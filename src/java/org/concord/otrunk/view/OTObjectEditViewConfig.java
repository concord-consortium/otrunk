/*
 * Last modification information:
 * $Revision: 1.6 $
 * $Date: 2007-09-26 18:49:59 $
 * $Author: sfentress $
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
	
	public boolean getUsePopupEditWindows();
	public static boolean DEFAULT_usePopupEditWindows = false;
	
	public String getMode();
	public void setMode(String mode);
	
	public boolean getCopyNewObjectsByDefault();
	public static boolean DEFAULT_copyNewObjectsByDefault = false;
}
