package org.concord.otrunk.overlay;

import java.net.URL;

import org.concord.framework.otrunk.OTObjectInterface;

public interface OTOverlayReference
    extends OTObjectInterface
{
	public URL getOverlayURL();
	public void setOverlayURL(URL url);
	
	public static int DEFAULT_number = 1;
	public int getNumber();
	public void setNumber(int num);
	
	public static boolean DEFAULT_canReload = false;
	public boolean getCanReload();
	public void setCanReload(boolean canReload);
}
