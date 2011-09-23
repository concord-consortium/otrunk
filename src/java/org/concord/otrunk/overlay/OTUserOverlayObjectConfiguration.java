package org.concord.otrunk.overlay;

import org.concord.framework.otrunk.OTObjectInterface;

public interface OTUserOverlayObjectConfiguration extends OTObjectInterface {
	public static int DEFAULT_copyDepth = -1;
	public void setCopyDepth(int depth);
	public int getCopyDepth();

	public static boolean DEFAULT_copyOnlyChanges = true;
	public boolean isCopyOnlyChanges();
	public void setCopyOnlyChanges(boolean copyOnlyChanges);
}
