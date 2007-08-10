package org.concord.otrunk.view.prototype;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;

public interface OTPropertyReference extends OTObjectInterface
{
	public String getProperty();
	public void setProperty(String property);
	
	public OTObject getReference();
	public void setReference(OTObject reference);
}
