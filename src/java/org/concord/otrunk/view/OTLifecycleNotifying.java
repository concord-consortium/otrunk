package org.concord.otrunk.view;

public interface OTLifecycleNotifying
{
	public void addLifecycleListener(OTLifecycleListener listener);
	public void removeLifecycleListener(OTLifecycleListener listener);
}
