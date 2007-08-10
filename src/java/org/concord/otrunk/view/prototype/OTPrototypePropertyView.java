package org.concord.otrunk.view.prototype;

import javax.swing.JComponent;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTViewFactory;

public class OTPrototypePropertyView extends OTPrototypeView
{
	protected JComponent getControllerComponent(OTObject otObject,
	    OTViewFactory otViewFactory)
	{
		// Assume the otObject is a OTPropertyReference
		// get the object from that and the model property.
		OTPropertyReference propRef = (OTPropertyReference) otObject;
				
		String copyKey = otObject.getGlobalId().toString();

		return controller.getComponent(propRef.getReference(), copyKey, propRef.getProperty(), 
				viewEntry, otViewFactory);
	}
}
