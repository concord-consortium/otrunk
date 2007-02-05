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
 * $Revision: 1.22 $
 * $Date: 2007-02-05 18:57:47 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTFrameManager;
import org.concord.framework.otrunk.view.OTFrameManagerAware;
import org.concord.framework.otrunk.view.OTObjectView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerAware;
import org.concord.framework.otrunk.view.OTViewContainerListener;
import org.concord.framework.otrunk.view.OTViewEntry;
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
    boolean currentObjectEditable = false;
    OTObjectView currentView = null;
    OTViewEntry currentViewEntry = null;
    
	private OTViewFactory otViewFactory;
	
	protected OTFrameManager frameManager;

	private boolean useScrollPane = true;
	private boolean autoRequestFocus = true;
	
	Vector containerListeners = new Vector();
	
	MyViewContainer viewContainer;
	
	/**
	 * 
	 */
	public OTViewContainerPanel(OTFrameManager frameManager)
	{
		super(new BorderLayout());

		viewContainer = new MyViewContainer();
		
		this.frameManager = frameManager;
		JLabel loadingLabel = new JLabel("Loading...");
		add(loadingLabel);		
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
	
	public OTObject getCurrentObject()
	{
	    return currentObject;
	}
	
	public void setCurrentObject(OTObject otObject)
	{
		// I'm not sure why editable is true by default here
		// but that is how it was before
		setCurrentObject(otObject, null, true);
	}
		
	public void setCurrentObject(OTObject otObject, boolean editable)
	{
		setCurrentObject(otObject, null, editable);
	}
	
	public void setCurrentObject(OTObject otObject, OTViewEntry viewEntry, 
			boolean editable)
	{
		if(currentView != null) {
		    currentView.viewClosed();
		}
			
		currentObject = otObject;
		currentObjectEditable = editable;
		currentViewEntry = viewEntry;
		
		removeAll();
		// Unfortunately the size of this label matters, when these objects
		// are embedded in tables inside of the htmleditorkit.  I think the
		// editorkit gets messed up when the width of a component changes.
		// It seems to be a problem only when it shrinks, not when it grows.	
		// so instead of this:
        // JLabel loading = new JLabel("Loading...");
		// we'll use this which is really short.
		JLabel loading = new JLabel("...");
		loading.setBorder(BorderFactory.createLineBorder(Color.black));
		add(loading);
		revalidate();		
		
		SwingUtilities.invokeLater(new Runnable(){
		    public void run()
		    {
				JComponent newComponent = null;
				if(currentObject != null) {
					if(currentViewEntry != null) {
						currentView = 
							(OTObjectView)otViewFactory.getView(currentObject, currentViewEntry);

					} else {
						currentView = 
							(OTObjectView)otViewFactory.getView(currentObject, OTObjectView.class);
					}

					if(currentView instanceof OTViewContainerAware){
			        	((OTViewContainerAware)currentView).
			        		setViewContainer(viewContainer);
			        }			        
					
				    if(currentView instanceof OTFrameManagerAware){
				    	((OTFrameManagerAware)currentView).setFrameManager(frameManager);
				    }

				    if(currentView == null) {
				    	newComponent = new JLabel("No view for object: " + currentObject);
				    } else {
				    	newComponent = currentView.getComponent(currentObject, currentObjectEditable);
					}
				} else {
					newComponent = new JLabel("Null object");
				}

				JComponent myComponent = newComponent;
				
				if(isUseScrollPane()) {
					JScrollPane scrollPane = new JScrollPane(newComponent);
					scrollPane.getViewport().setViewPosition(new Point(0,0));
					scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					myComponent = scrollPane;
				}
				removeAll();
				add(myComponent, BorderLayout.CENTER);
				
				revalidate();
				notifyListeners();
				if(isAutoRequestFocus()){
					newComponent.requestFocus();
				}
		    }
		});
	}

    public Component getCurrentComponent()
    {   
    	Component currentComp = getComponent(0);
    	if(currentComp instanceof JScrollPane) {
    		currentComp = ((JScrollPane)currentComp).getViewport().getView();
    	}

        return currentComp;
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
		public void setCurrentObject(OTObject pfObject) {
			OTViewContainerPanel.this.setCurrentObject(pfObject);
		}
	}

	public OTViewContainer getViewContainer() {
		return viewContainer;
	}

	public boolean isUseScrollPane() {
		return useScrollPane;
	}

	public void setUseScrollPane(boolean useScrollPane) {
		this.useScrollPane = useScrollPane;
	}

	public boolean isAutoRequestFocus() {
		return autoRequestFocus;
	}

	public void setAutoRequestFocus(boolean autoRequestFocus) {
		this.autoRequestFocus = autoRequestFocus;
	}
}
