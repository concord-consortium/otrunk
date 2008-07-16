package org.concord.otrunk.util;

public interface OTSharingListener
{
	/**
	 * Called when a new object is shared.
	 * @param event The OTSharingEvent describing this change.
	 */
	public void objectShared(OTSharingEvent event);
	
	/**
	 * Called when an object is no longer being shared.
	 * @param event The OTSharingEvent describing this change.
	 */
	public void objectRemoved(OTSharingEvent event);

}
