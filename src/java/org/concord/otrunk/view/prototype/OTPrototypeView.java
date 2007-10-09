package org.concord.otrunk.view.prototype;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewEntryAware;

public class OTPrototypeView extends AbstractOTJComponentView
	implements OTJComponentView, OTViewEntryAware
{
	OTPrototypeViewEntry viewEntry;
		
	/**
	 * We need to keep a reference to the controller
	 * so it can add listeners to things and not get
	 * garbage collected.
	 */
	OTPrototypeController controller;
	PrototypeControllerInstance controllerInstance;
	
	OTJComponentView viewOfPrototype;
	
	public JComponent getComponent(OTObject otObject) 
	{
		OTObject prototype = viewEntry.getPrototype();

		controller = viewEntry.getController();
		OTObject objectToView = null;
		JComponent component = null;
		if(controller != null) {
			controllerInstance = controller.createControllerInstance();			
			OTObject prototypeCopy = getPrototypeCopy(otObject);

			objectToView = getControllerViewObject(otObject, prototypeCopy);
			
		} else {
			objectToView = prototype;
		}

		String viewMode = viewEntry.getViewMode();
		
		// FIXME For now we need to set the default view mode of the factory otherwise children views of this
		// viewOfPrototype will not use this viewMode, if possible this should happen automatically when 
		// a view is requested with a particular mode.
		getViewFactory().setDefaultViewMode(viewMode);
		
		// return the component for the view 
		viewOfPrototype = getJComponentService().getObjectView(objectToView, null, null, 
				viewEntry.getViewEntry());
				
		component = viewOfPrototype.getComponent(objectToView);			

		if(component == null) {
			return new JLabel("No view for object: " + prototype);
		} else {
			return component;
		}		
	}

	protected OTObject getPrototypeCopy(OTObject model)
	{
		String prototypeCopyKey = model.otExternalId();
		
		if(viewEntry.getCopyPrototype()){
			OTObject prototypeCopy = viewEntry.getPrototypeCopies().getObject(prototypeCopyKey);

			// make one if there isn't
			if(prototypeCopy == null) {			
				try {
					prototypeCopy =
						model.getOTObjectService().copyObject(viewEntry.getPrototype(), -1);
					
					if(viewEntry.getSavePrototypeCopies()){
						viewEntry.getPrototypeCopies().putObject(prototypeCopyKey, prototypeCopy);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				initPrototypeCopy(model, prototypeCopy);
			}
			return prototypeCopy;
		} else {
			return viewEntry.getPrototype();
		}

	}
	
	protected void initPrototypeCopy(OTObject model, OTObject prototypeCopy)
	{
		controllerInstance.initPrototypeCopy(model, prototypeCopy);
	}
	
	protected OTObject getControllerViewObject(OTObject model, OTObject prototypeCopy)
	{
		return controllerInstance.getViewObject(model, prototypeCopy, null);
	}
	
	
	public void viewClosed() 
	{
		// first pass the close on to the view of our prototype copy
		viewOfPrototype.viewClosed();
		
		// Now call the close method on the controllerInstance so it can do any cleanup it has to do
		if(controllerInstance == null){
			System.err.println("null controllerInstance in prototypeView");
		} else {
			controllerInstance.close();
		}
		
		
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTViewConfigAware#setViewConfig(org.concord.framework.otrunk.OTObject)
	 */
	public void setViewEntry(OTViewEntry viewEntry) 
	{
		this.viewEntry = (OTPrototypeViewEntry)viewEntry;		
	}
}
