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
	
	public JComponent getComponent(OTObject otObject, boolean editable) 
	{
		// for now just ignore the passed in object
		// and look up the view for our object, 
		// What should happen is that the OtObject should be proxied
 		// so that certain properties from it can come from the passed
		// in object.
		
		OTObject prototype = viewEntry.getPrototype();

		controller = viewEntry.getController();
		OTObject objectToView = null;
		JComponent component = null;
		if(controller != null) {
			controllerInstance = controller.getControllerInstance();			
			OTObject prototypeCopy = getPrototypeCopy(otObject);

			objectToView = getControllerViewObject(otObject, prototypeCopy);
			
		} else {
			objectToView = prototype;
		}

		// return the component for the view 
		OTJComponentView view = getJComponentService().getObjectView(objectToView, null, null, 
				viewEntry.getViewEntry());
				
		component = view.getComponent(objectToView, false);			

		if(component == null) {
			return new JLabel("No view for object: " + prototype);
		} else {
			return component;
		}		
	}

	protected OTObject getPrototypeCopy(OTObject model)
	{
		String prototypeCopyKey = model.getGlobalId().toString();
		
		if(viewEntry.getCopyPrototype()){
			OTObject prototypeCopy = viewEntry.getPrototypeCopies().getObject(prototypeCopyKey);

			// make one if there isn't
			if(prototypeCopy == null) {			
				try {
					prototypeCopy =
						model.getOTObjectService().copyObject(viewEntry.getPrototype(), -1);
					viewEntry.getPrototypeCopies().putObject(prototypeCopyKey, prototypeCopy);
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
	
	
	public void viewClosed() {
		// TODO Auto-generated method stub
		controllerInstance.close();
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTViewConfigAware#setViewConfig(org.concord.framework.otrunk.OTObject)
	 */
	public void setViewEntry(OTViewEntry viewEntry) 
	{
		this.viewEntry = (OTPrototypeViewEntry)viewEntry;		
	}
}
