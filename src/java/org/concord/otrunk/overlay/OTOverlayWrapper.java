package org.concord.otrunk.overlay;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;

public interface OTOverlayWrapper
    extends OTObjectInterface
{
	/**
	 * If this is specified, the the wrapped object is rendered from that overlay. Otherwise the wrapper
	 * will check for an existing OTClassListManager, and will load the current user's overlay. If that
	 * is not specified, or the OTClassListManager doesn't exist, it will load the object in the current
	 * users session overlay, like normal.
	 * @return OTOverlay The specific overlay from which to load objects
	 */
	public OTOverlay getOverlay();
	public OTObject getWrappedObject();
	
	public static boolean DEFAULT_showButton = true;
	/**
	 * Whether or not to show the submit button
	 * @return
	 */
	public boolean getShowButton();
	
	public static String DEFAULT_buttonText = "Submit";
	/**
	 * The text used for the "submit" button.
	 * @return
	 */
	public String getButtonText();
	
	public OTObject getResultsObject();
	
	public static boolean DEFAULT_autoSubmit = false;
	public boolean getAutoSubmit();
}
