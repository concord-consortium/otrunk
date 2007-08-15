package org.concord.otrunk.view.prototype;

import org.concord.framework.otrunk.OTObject;

public class OTPrototypePropertyView extends OTPrototypeView
{
	protected OTObject getControllerViewObject(OTObject model,
		OTObject prototypeCopy)
	{
		// Assume the otObject is a OTPropertyReference
		// get the object from that and the model property.
		OTPropertyReference propRef = (OTPropertyReference) model;

		return controllerInstance.getViewObject(propRef.getReference(), prototypeCopy, 
				propRef.getProperty());
	}	
}
