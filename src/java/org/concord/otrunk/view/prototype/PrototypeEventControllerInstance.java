package org.concord.otrunk.view.prototype;

import org.concord.framework.otrunk.OTObject;

public class PrototypeEventControllerInstance
    implements PrototypeControllerInstance
{
	OTPrototypeEventMappingHelper helper;

	OTPrototypeEventController.ResourceSchema resources;

	PrototypeEventControllerInstance(OTPrototypeEventController.ResourceSchema resources)
	{
		this.resources = resources;
	}
	
	public OTObject getViewObject(OTObject model, OTObject prototypeCopy,
	    String defaultModelProperty)
	{
		helper = 
			new OTPrototypeEventMappingHelper(model, defaultModelProperty, prototypeCopy, 
					resources);
				
		return prototypeCopy;
	}

	public void close()
	{
		helper.removeAllListeners();
	}

}
