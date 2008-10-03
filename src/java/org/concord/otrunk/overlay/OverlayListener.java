package org.concord.otrunk.overlay;

import org.concord.otrunk.datamodel.OTDataObject;

public interface OverlayListener
{
	public void newDeltaObject(Overlay overlay, OTDataObject baseObject);
}
