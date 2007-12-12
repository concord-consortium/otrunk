package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTObjectInterface;

/**
 * This interface if we want an object to contain child views of a different mode than
 * itself. The view of this object ought to call setSubViewMode(mode).
 * 
 * This is currently used by the OTModeSwitcher if it doesn't want to switch the mode
 * of the top-level object.
 * 
 * @author sfentress
 *
 */
public interface OTChildModeSettable extends OTObjectInterface
{
	public String getChildMode();
	public void setChildMode(String mode);
	public static String DEFAULT_childMode = "_no_view_mode";
}
