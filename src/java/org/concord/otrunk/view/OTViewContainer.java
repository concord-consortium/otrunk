/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-15 22:52:15 $
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
	public void setCurrentObject(OTObject pfObject);
	
	public JComponent getComponent(OTObject pfObject, 
			OTViewContainer container, boolean editable);
}
