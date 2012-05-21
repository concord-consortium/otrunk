package org.concord.otrunk;

import org.concord.otrunk.user.OTReferenceMap;

public interface PeriodicUploadingEventListener
{
	public void uploadFailed(OTReferenceMap refMap, Exception e);
	public void uploadSucceeded(OTReferenceMap refMap);
}
