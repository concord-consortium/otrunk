/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2007-09-26 18:39:32 $
 * $Author: imoncada $
 *
 * Licence Information
 * Copyright 2007 The Concord Consortium 
*/
package org.concord.otrunk.view;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.view.OTAction;
import org.concord.framework.otrunk.view.OTActionContext;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.framework.otrunk.view.OTFrameManager;

/**
 * OTFrameContainerAction
 * Class name and description
 *
 * Date created: Sep 26, 2007
 *
 * @author Ingrid Moncada<p>
 *
 */
public class OTFrameDisplayAction extends DefaultOTObject
    implements OTAction
{
	public static interface MyResourceSchema extends OTResourceSchema
	{
		public OTFrame getFrame();
		public OTObject getObjectToDisplay();
	}
	protected MyResourceSchema resources;

	public OTFrameDisplayAction(MyResourceSchema resources)
	{
		super(resources);
		this.resources = resources;
	}
	
	/**
	 * @see org.concord.framework.otrunk.view.OTAction#doAction(org.concord.framework.otrunk.view.OTActionContext)
	 */
	public void doAction(OTActionContext context)
	{
		OTFrameManager frameManager = (OTFrameManager)context.getViewContext().getViewService(OTFrameManager.class);
		OTFrame frame = resources.getFrame();
		OTObject otObject = resources.getObjectToDisplay();
		
		frameManager.putObjectInFrame(otObject, frame);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTAction#getActionText()
	 */
	public String getActionText()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
