/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2005-01-25 16:18:41 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import javax.swing.JComponent;

import org.concord.framework.otrunk.OTObject;


/**
 * PfViewContainer
 * Class name and description
 *
 * Date created: Sep 8, 2004
 *
 * @author scott<p>
 *
 */
public interface OTViewContainer
{
	public void setCurrentObject(OTObject pfObject, OTFrame otFrame);
	
	public JComponent getComponent(OTObject pfObject, 
			OTViewContainer container, boolean editable);
}
