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
 * $Revision: 1.48 $
 * $Date: 2007-07-03 18:43:02 $
 * $Author: aunger $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTFrameManager;
import org.concord.framework.otrunk.view.OTJComponentService;
import org.concord.framework.otrunk.view.OTJComponentServiceFactory;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerListener;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.framework.otrunk.view.OTViewServiceProvider;
import org.concord.swing.util.ComponentScreenshot;


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
    OTJComponentView currentView = null;
    OTViewEntry currentViewEntry = null;
    
    private OTJComponentService jComponentService;	
	
	protected OTFrameManager frameManager;

	private boolean useScrollPane = true;
	private boolean autoRequestFocus = true;
	private boolean updateable = false;
	private OTViewContainer parentContainer;
	
	Vector containerListeners = new Vector();
	
	MyViewContainer viewContainer;

	/**
	 * This is used to ignore the scrollRectToVisible method
	 * both in the viewport and in the ourselves.  scrollRectToVisible
	 * is called when the content of a child component is initialized.  One place
	 * where it is called is when the caret position is changed during loading.
	 * If the scrolling is not disabled then this causes the view to scroll to
	 * the bottom.  If this view is embedded in another view then the scroll 
	 * "event" is propgated to the parent and it is scrolled so the bottom of this
	 * embedded view is visible.
	 * 
	 * This is a count, so that scrolling stays disabled until all of the 
	 * scroll causing operations have finished.  This happens because setCurrentObject
	 * is called multiple times not in the awt thread.  The first one disables
	 * scrolling and queues up an invokeLater to enable it again.  Then the second
	 * call to setCurrentObject happens before the queued up enableScrolling is
	 * run.  And then the enableScrolling is run before the gui operations happen
	 * from the second call. So without a counter, the scrolling would be enabled while some of theThis is a bit dangerous
	 * so it would be better have some kind of logging so we can track this better.
	 */
	private int unwantedScrollingCount = 0;

	private String viewMode = null;
	
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
	
	public OTJComponentService getOTJComponentService()
	{
		return jComponentService;
	}
	
	/**
	 * This method is for legacy the object really only needs 
	 * OTJComponentService.  But to make the migration easier this method is still
	 * available.  And each time it is called more a new jComponentService is
	 * created. 
	 * 
	 * @param factory
	 */
	public void setOTViewFactory(OTViewFactory factory)
	{
		// get the OTJComponentService so we can create the OTJComponentView
		OTViewServiceProvider viewServiceProvider = 
			factory.getViewServiceProvider();
		OTJComponentServiceFactory componentServiceFactory =
			(OTJComponentServiceFactory) viewServiceProvider.getViewService(OTJComponentServiceFactory.class);
		jComponentService = componentServiceFactory.createOTJComponentService();
	}
		
	/**
	 * This is the preferred method for giving this object the ability to create new views
	 * Using this method will allow this object to share the jComponentService with
	 * other viewContainerPanels or view creators.  This way those views can access
	 * each other though the OTViewHost interface.
	 * 
	 * @param jComponentService
	 */
	public void setOTJComponentService(OTJComponentService jComponentService)
	{
		this.jComponentService = jComponentService;
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
		    currentView = null;
		}
		
		// FIXME There might already be an event queued to setup this current object
		// We should compare the last queued event with this request and 
		// have the option to not queue this event.
		
		currentObject = otObject;
		currentObjectEditable = editable;
		currentViewEntry = viewEntry;
		
		removeAll();
		
		if(otObject == null){
			return;
		}
		
		disableScrolling();

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
		
		// By doing a double invokeLater we make sure this code doesn't get
		// run until much later in the process.  Unless something else is doing
		// an double invoke latter, this will execute after all of the currently
		// queued up code which calls invokeLater.
		// For some reason this allows popups to incrementally draw them selves
		// without it, the popup blocks until the inside content is rendered
		// before showing anything
		final Runnable createComponentTask = new Runnable(){
		    public void run()
		    {
		    	JComponent myComponent = createJComponent(); 

				if(isUseScrollPane()) {
					JScrollPane scrollPane = new JScrollPane();
					scrollPane.setViewport(new JViewport(){
						/**
						 * Not intended to be serialized, just added remove compile warning
						 */
						private static final long serialVersionUID = 1L;
						
						public void scrollRectToVisible(Rectangle contentRect) {
							// disabling this removes the flicker that occurs during the loading of the page.
							// if we could 
							if(!isScrollingAllowed()){
								return;
							}

							super.scrollRectToVisible(contentRect);							
						}
					});
					scrollPane.setViewportView(myComponent);
					
					scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					myComponent = scrollPane;
				}
				removeAll();
				add(myComponent, BorderLayout.CENTER);
				
				revalidate();
				notifyListeners();
				if(isAutoRequestFocus()){
					myComponent.requestFocus();
				}
				
				// We have to queue this up, because during the setup of this
				// component other things might be queued, that cause scrolling
				// to happen. 
				// this way the scrolling should remain diabled until all 
				// of them are complete.
				SwingUtilities.invokeLater(new Runnable(){
					/* (non-Javadoc)
					 * @see java.lang.Runnable#run()
					 */
					public void run() {
						// TODO Auto-generated method stub
						enableScrolling();
					}
				});
		    }
		};

		/**
		 * This is disabled for now but we might have to come back to it it
		 * fixed a loading problems with sidebars that were popup windows.
		 * 
		 * Only do a double invoke later if the item is a sidebar item
		 * 
		 *
		String otObjectClassName = otObject.getClass().getInterfaces()[0].getName();
		System.out.println(otObjectClassName);
		if(otObject.getClass().getInterfaces()[0].getName().endsWith("OTUDLSideBarItem")){
			System.out.println("delaying sidebar item update");
			SwingUtilities.invokeLater(new Runnable(){
				public void run()
				{

					SwingUtilities.invokeLater(createComponentTask);
				} 
			});			
		} else {
			SwingUtilities.invokeLater(createComponentTask);
		}
		*/
		
		SwingUtilities.invokeLater(createComponentTask);
	}

	/**
	 * This method trys to make a view of the current object and viewEntry if 
	 * there is one.
	 * 
	 * @return
	 */
	protected JComponent createJComponent()
	{
		if(currentObject == null) {
			return new JLabel("Null object");
		}
		
		// get the OTJComponentService so we can create the OTJComponentView
		OTJComponentService jComponentService = getOTJComponentService();
		
		currentView = jComponentService.getObjectView(currentObject, viewContainer,
				getViewMode(), currentViewEntry);		

		if(currentView == null) {
			return new JLabel("No view for object: " + currentObject);
		} 
		
		return jComponentService.getComponent(currentObject, 
				currentView, currentObjectEditable);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#scrollRectToVisible(java.awt.Rectangle)
	 */
	public void scrollRectToVisible(Rectangle aRect) 
	{
		// disabling this removes the flicker that occurs during the loading of the page.
		// if we could 
		if(!isScrollingAllowed()){
			return;
		}

		super.scrollRectToVisible(aRect);					
	}
	
	protected boolean isScrollingAllowed()
	{
		return unwantedScrollingCount <=0;
	}
	
	protected void disableScrolling()
	{
		// System.out.println("disable Scrolling: " + unwantedScrollingCount + " " 
		//		+ currentObject.getGlobalId());
		unwantedScrollingCount++;
	}
	
	protected void enableScrolling()
	{
		unwantedScrollingCount--;
		//System.out.println("enabling Scrolling: " + 
		//		unwantedScrollingCount + " " + 
		//		currentObject.getGlobalId().toString());

		if(unwantedScrollingCount < 0){
			System.err.println("unwantedScrollingCount dropped below 0");
		}
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
		
		public boolean isUpdateable() {
	        return OTViewContainerPanel.this.isUpdateable();
        }
		public void setUpdateable(boolean b) {
			OTViewContainerPanel.this.setUpdateable(b);
        }
		
		public void setParentContainer(OTViewContainer c) {
			OTViewContainerPanel.this.setParentContainer(c);
		}
		public OTViewContainer getParentContainer() {
			return OTViewContainerPanel.this.getParentContainer();
		}
		
		public OTViewContainer getUpdateableContainer() {
			return OTViewContainerPanel.this.getUpdateableContainer();
		}
	}

	public OTViewContainer getViewContainer() {
		return viewContainer.getUpdateableContainer();
	}
	
	public OTViewContainer getUpdateableContainer()
    {
	    if (this.isUpdateable()) {
	    	return this.viewContainer;
	    } else {
	    	return parentContainer.getUpdateableContainer();
	    }
    }

	public OTJComponentView getView() {
		return currentView;
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

	public boolean isUpdateable() {
		return updateable;
	}
	
	public void setUpdateable(boolean b) {
		this.updateable = b;
	}
	/**
	 * @param viewMode
	 */
	public void setViewMode(String viewMode) 
	{
		this.viewMode  = viewMode;
	}
	
	public String getViewMode()
	{
		return viewMode;
	}
	
	public void saveScreenshotAsByteArrayOutputStream(ByteArrayOutputStream out, String type)
	throws Throwable
	{
		ComponentScreenshot.saveScreenshotAsOutputStream(this, out, type);
	}
	
	/**
	 * 
	 * @param type "png" or "gif" are best bets
	 * @return ByteArrayOutputStream representing image
	 * @throws Throwable
	 */
	public ByteArrayOutputStream getScreenshotAsByteArrayOutputStream(String type)
	throws Throwable
	{
		return ComponentScreenshot.saveScreenshotAsByteArrayOutputStream(this, type);
		
	}
	
	public BufferedImage getScreenShot() throws Exception
	{
		BufferedImage image = ComponentScreenshot.getScreenshot(this);
		return image;
	}
	
	public boolean isCurrentObjectEditable() {
		return currentObjectEditable;
	}

	public void setParentContainer(OTViewContainer parentContainer)
    {
	    this.parentContainer = parentContainer;
    }

	public OTViewContainer getParentContainer()
    {
	    return parentContainer;
    }
}
