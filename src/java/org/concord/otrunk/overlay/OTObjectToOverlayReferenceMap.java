package org.concord.otrunk.overlay;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTObjectMap;

public interface OTObjectToOverlayReferenceMap
    extends OTObjectInterface
{
	public OTObjectMap getObjectToOverlayMap();
}
