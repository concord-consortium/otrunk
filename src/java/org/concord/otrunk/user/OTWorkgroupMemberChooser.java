package org.concord.otrunk.user;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.otrunk.view.OTClassMember;
import org.concord.otrunk.view.document.OTDocument;

public interface OTWorkgroupMemberChooser
    extends OTObjectInterface
{
	/**
	 * The current user
	 * @return
	 */
	public void setLeadMember(OTClassMember member);
	public OTClassMember getLeadMember();
	
	/**
	 * A List of OTClassMember objects, all of which are copied from the OTClassListManager
	 * @return
	 */
	public OTObjectList getOtherWorkgroupMembers();
	
	/**
	 * A List of OTClassMember objects, all of which were dynamically created on the fly by the current user
	 * @return
	 */
	public OTObjectList getCustomWorkgroupMembers();
	
	public static final boolean DEFAULT_usePopup = true;
	public boolean getUsePopup();
	
	/**
	 * This Document is a template for the header text in the view. The text will be run through a simple substitution engine prior to being rendered.
	 * Valid substitution variables are:
	 *   #{user_name}
	 * @return
	 */
	public OTDocument getTextTemplate();
}
