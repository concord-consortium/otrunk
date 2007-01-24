/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*
 * Last modification information:
 * $Revision: 1.21 $
 * $Date: 2007-01-24 22:11:24 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.framework.otrunk.view.OTObjectView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerListener;
import org.concord.framework.otrunk.view.OTViewFactory;


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
{
    /**
     * First version of this class
     */    
    private static final long serialVersionUID = 1L;
    
    OTObject currentObject = null;
    OTObjectView currentView = null;
    
	private OTViewFactory otViewFactory;
	
	protected OTFrameManager frameManager;

	private JFrame myFrame;
	private JScrollPane scrollPane = null;

	Vector containerListeners = new Vector();
	
	MyViewContainer viewContainer;
	
	/**
	 * 
	 */
	public OTViewContainerPanel(OTFrameManager frameManager, JFrame frame)
	{
		super(new BorderLayout());

		viewContainer = new MyViewContainer();
		
		this.frameManager = frameManager;
		myFrame = frame;
		scrollPane = new JScrollPane(new JLabel("Loading..."));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane);		
	}
	
	public void setOTViewFactory(OTViewFactory factory)
	{
		otViewFactory = factory;
	}
		
	public void setMessage(String message)
	{
	    removeAll();
	    add(new JLabel(message));
	}
	
	public void showFrame()
	{
		myFrame.setVisible(true);
	}
	
	public OTObject getCurrentObject()
	{
	    return currentObject;
	}
	
	public void setCurrentObject(OTObject otObject, OTFrame otFrame)
	{

		if(otFrame != null) {
			frameManager.setFrameObject(otObject, otFrame);
			return;
		}
		
		if(currentView != null) {
		    currentView.viewClosed();
		}
			
		currentObject = otObject;

		removeAll();
		add(new JLabel("Loading..."));
		revalidate();		
		
		SwingUtilities.invokeLater(new Runnable(){
		    public void run()
		    {
				JComponent newComponent = null;
				if(currentObject != null) {
				    currentView = otViewFactory.getObjectView(currentObject, viewContainer);
				    if(currentView == null) {
				    	newComponent = new JLabel("No view for object: " + currentObject);
				    } else {
				    	newComponent = currentView.getComponent(currentObject, true);
					}
				} else {
					newComponent = new JLabel("Null object");
				}
				
		    	scrollPane = new JScrollPane(newComponent);
		    	scrollPane.getViewport().setViewPosition(new Point(0,0));
				scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

				removeAll();
				add(scrollPane, BorderLayout.CENTER);
				
				revalidate();
				notifyListeners();
				newComponent.requestFocus();		        
		    }
		});
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewContainer#setCurrentObject(org.concord.framework.otrunk.OTObject, org.concord.otrunk.view.OTFrame)
	 */
	/*public void setCurrentObject(Vector users, OTFrame otFrame)
	{
		if(users == null || users.size() == 0) return;
		
		if(currentView != null) {
		    currentView.viewClosed();
		}

		currentObject = pfObject;

		removeAll();
		add(new JLabel("Loading..."));
		revalidate();		
		
		SwingUtilities.invokeLater(new Runnable(){
		    public void run()
		    {
				JComponent newComponent = null;
				if(currentObject != null) {
				    currentView = otViewFactory.getObjectView(currentObject, OTViewContainerPanel.this);
				    if(currentView == null) {
				        newComponent = new JLabel("No view for object: " + currentObject);
						

				    } else {
				        newComponent = currentView.getComponent(true);
				    }
				} else {
					newComponent = new JLabel("Null object");
				}

				removeAll();
				add(newComponent, BorderLayout.CENTER);
				revalidate();
				notifyListeners();
				newComponent.requestFocus();		        
		    }
		});
	}*/

    public Component getCurrentComponent()
    {
        return getComponent(0);
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
	        	currentObjectChanged(viewContainer);
	        	
	    }
	}
	
	/**
	 * Internal class so views which get passed a view container do not
	 * have direct access to the viewcontainer panel.  This also makes it
	 * easier to see who is using view containers and who is using 
	 * the viewcontainerpanel directly
	 * 
	 * @author scott
	 *
	 */
	class MyViewContainer implements OTViewContainer {
		public OTObject getCurrentObject() {
		    return OTViewContainerPanel.this.getCurrentObject();
		}
		public void setCurrentObject(OTObject pfObject, OTFrame otFrame) {
			OTViewContainerPanel.this.setCurrentObject(pfObject, otFrame);
		}
	}

	public MyViewContainer getViewContainer() {
		return viewContainer;
	}
}
