package org.concord.otrunk.user;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.otrunk.view.OTGroupMember;

public interface OTWorkgroupMemberChooser
    extends OTObjectInterface
{
	/**
	 * The current user
	 * @return
	 */
	public void setLeadMember(OTGroupMember member);
	public OTGroupMember getLeadMember();
	
	/**
	 * A List of OTGroupMember objects, all of which are copied from the OTGroupListManager
	 * @return
	 */
	public OTObjectList getOtherWorkgroupMembers();
	
	/**
	 * A List of OTGroupMember objects, all of which were dynamically created on the fly by the current user
	 * @return
	 */
	public OTObjectList getCustomWorkgroupMembers();
	
	public static final boolean DEFAULT_usePopup = true;
	public boolean getUsePopup();
	
	/**
	 * This Document is a template for the header text in the view. The text will be run through a simple substitution engine prior to being rendered.
	 * Valid substitution variables are:
	 *   #{user_name}
	 * The type of this object is set to OTObject to remove its dependency on OTDocument which 
	 * is no longer in the OTrunk project.  The view now casts this to OTDocument.
	 * This class should probably be moved to another package/project.
	 * @return
	 */
	public OTObject getTextTemplate();
}
