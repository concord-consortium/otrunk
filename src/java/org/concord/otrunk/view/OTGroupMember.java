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
	 * The uuid for the SDS workgroup.
	 * This will be the same as the uuid for the learner session OTUser.
	 * @return
	 */
	public String getSailUuid();
	
	/**
	 * This is an MD5 hex digest of the password
	 * @return
	 */
	public String getPasswordHash();
}
