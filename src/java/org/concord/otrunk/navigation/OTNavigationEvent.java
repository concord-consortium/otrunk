package org.concord.otrunk.navigation;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;

public interface OTNavigationEvent extends OTObjectInterface {

	public long getTimestamp();
	public void setTimestamp(long ms);
	
	public OTObject getObject();
	public void setObject(OTObject object);
}
