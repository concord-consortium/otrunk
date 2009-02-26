package org.concord.otrunk.view;

import java.net.URL;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.otrunk.user.OTUserObject;

public interface OTGroupMember extends OTObjectInterface
{
	public URL getDataURL();
	public OTUserObject getUserObject();
	
	public static boolean DEFAULT_isCurrentUser = false;
	public boolean getIsCurrentUser();

	public String getUuid();
	
	/**
	 * This is an MD5 hex digest of the password
	 * @return
	 */
	public String getPasswordHash();
}
