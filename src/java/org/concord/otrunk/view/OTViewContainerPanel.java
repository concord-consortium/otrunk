/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2005-01-27 16:45:29 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.framework.otrunk.view.OTViewContainer;


/**
 * OTViewContainerPanel
 * Class name and description
 *
 * Date created: Jan 20, 2005
 *
 * @author scott<p>
 *
 */
public class OTViewContainerPanel extends JPanel
	implements OTViewContainer
{
	private static OTViewFactory otViewFactory;
	
	protected OTFrameManager frameManager;

	private JFrame myFrame;

	public static void setOTViewFactory(OTViewFactory factory)
	{
		otViewFactory = factory;
	}
		
	/**
	 * 
	 */
	public OTViewContainerPanel(OTFrameManager frameManager, JFrame frame)
	{	
		super(new BorderLayout());
		this.frameManager = frameManager;
		myFrame = frame;
	}

	public void showFrame()
	{
		myFrame.setVisible(true);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewContainer#setCurrentObject(org.concord.framework.otrunk.OTObject, org.concord.otrunk.view.OTFrame)
	 */
	public void setCurrentObject(OTObject pfObject, OTFrame otFrame)
	{
		if(otFrame != null) {
			frameManager.setFrameObject(pfObject, otFrame);
			return;
		}
		
		JComponent newComponent = null;
		if(pfObject != null) {
			newComponent = getComponent(pfObject, this, true);
			 
		} else {
			newComponent = new JLabel("Null object");
		}

		removeAll();
		add(newComponent, BorderLayout.CENTER);
		revalidate();
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewContainer#getComponent(org.concord.framework.otrunk.OTObject, org.concord.otrunk.view.OTViewContainer, boolean)
	 */
	public JComponent getComponent(OTObject pfObject,
			OTViewContainer container, boolean editable)
	{
		return otViewFactory.getComponent(pfObject, container, editable);
	}

}
