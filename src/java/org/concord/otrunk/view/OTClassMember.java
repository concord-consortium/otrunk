package org.concord.otrunk.view;

import java.net.URL;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.otrunk.user.OTUserObject;

public interface OTClassMember extends OTObjectInterface
{
	public URL getOverlayURL();
	public OTUserObject getUserObject();
	
	public static boolean DEFAULT_isCurrentUser = false;
	public boolean getIsCurrentUser();

}
