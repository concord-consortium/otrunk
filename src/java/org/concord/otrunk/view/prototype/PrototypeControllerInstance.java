package org.concord.otrunk.view.prototype;

import org.concord.framework.otrunk.OTObject;

public interface PrototypeControllerInstance
{
	OTObject getViewObject(OTObject model, OTObject prototypeCopy, String defaultModelProperty);
	
	void close();
}
