package org.concord.otrunk.view.prototype;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTView;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewEntryAware;
import org.concord.framework.otrunk.view.OTViewFactory;

public class OTPrototypeView extends AbstractOTView
	implements OTJComponentView, OTViewEntryAware
{
	OTPrototypeViewEntry viewEntry;
	
	/**
	 * We need to keep a reference to the controller
	 * so it can add listeners to things and not get
	 * garbage collected.
	 */
	OTPrototypeController controller;
	
	public JComponent getComponent(OTObject otObject, boolean editable) 
	{
		OTViewFactory otViewFactory = 
			   (OTViewFactory)getViewService(OTViewFactory.class);
		
		// for now just ignore the passed in object
		// and look up the view for our object, 
		// What should happen is that the OtObject should be proxied
		// so that certain properties from it can come from the passed
		// in object.
		
		OTObject prototype = viewEntry.getPrototype();

		controller = viewEntry.getController();		
		JComponent component = null;
		if(controller != null) {
			component = controller.getComponent(otObject, viewEntry, otViewFactory);
		} else {
			// there is no controller, so we can't create a live view but
			// we can display a view of the prototype
			OTJComponentView currentView =
				(OTJComponentView)otViewFactory.getView(prototype, viewEntry.getViewEntry());

			/**
			 * Should set these things here
			 * but this code needs to go in a common place
			 * so we stop doing it over and over
			if(currentView instanceof OTViewContainerAware){
				((OTViewContainerAware)currentView).
				setViewContainer(viewContainer);
			}			        
				
			if(currentView instanceof OTFrameManagerAware){
				((OTFrameManagerAware)currentView).setFrameManager(frameManager);
			}
			 */
			component = currentView.getComponent(prototype, false);
		}
		/*
		OTPrototypeViewInvocationHandler invokeHandler =
			new OTPrototypeViewInvocationHandler(templateObject, otObject, this);

		OTObject proxy = (OTObject)Proxy.newProxyInstance(getClass().getClassLoader(), 
				templateObject.getClass().getInterfaces(), invokeHandler);
		
		OTJComponentView currentView =
			(OTJComponentView)otViewFactory.getView(proxy, config.getViewEntry());
        *
        */
		
		/**
		 * Should set these things here
		 * but this code needs to go in a common place
		 * so we stop doing it over and over
		if(currentView instanceof OTViewContainerAware){
			((OTViewContainerAware)currentView).
			setViewContainer(viewContainer);
		}			        
			
		if(currentView instanceof OTFrameManagerAware){
			((OTFrameManagerAware)currentView).setFrameManager(frameManager);
		}
		 */

		
		if(component == null) {
			return new JLabel("No view for object: " + prototype);
		} else {
			return component;
		}		
	}

	public void viewClosed() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTViewConfigAware#setViewConfig(org.concord.framework.otrunk.OTObject)
	 */
	public void setViewEntry(OTViewEntry viewEntry) 
	{
		this.viewEntry = (OTPrototypeViewEntry)viewEntry;		
	}
}
