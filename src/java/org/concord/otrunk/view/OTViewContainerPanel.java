/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2005-04-01 19:15:42 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerListener;


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
    OTObject currentObject = null;
    
	private static OTViewFactory otViewFactory;
	
	protected OTFrameManager frameManager;

	private JFrame myFrame;

	Vector containerListeners = new Vector();
	
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
		
		currentObject = pfObject;
		
		JComponent newComponent = null;
		if(pfObject != null) {
			newComponent = getComponent(pfObject, this, true);
			 
		} else {
			newComponent = new JLabel("Null object");
		}

		removeAll();
		add(newComponent, BorderLayout.CENTER);
		revalidate();
		notifyListeners();
	}

	public OTObject getCurrentObject()
	{
	    return currentObject;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewContainer#getComponent(org.concord.framework.otrunk.OTObject, org.concord.otrunk.view.OTViewContainer, boolean)
	 */
	public JComponent getComponent(OTObject pfObject,
			OTViewContainer container, boolean editable)
	{
		return otViewFactory.getComponent(pfObject, container, editable);
	}

	public void addViewContainerListener(OTViewContainerListener listener)
	{
	    containerListeners.add(listener);
	}
	
	public void removeViewContainerListener(OTViewContainerListener listener)
	{
	    containerListeners.remove(listener);
	}
	
	public void notifyListeners()
	{
	    for(int i=0; i<containerListeners.size(); i++) {
	        ((OTViewContainerListener)containerListeners.get(i)).
	        	currentObjectChanged(this);
	        	
	    }
	}
}
