package org.concord.otrunk.overlay;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;

public interface OTOverlay
    extends OTObjectInterface
{
	public OTResourceMap getDeltaObjectMap();
	
	public static String NON_DELTA_OBJECTS_ATTRIBUTE = "nonDeltaObjects";
	public OTResourceList getNonDeltaObjects();
}
