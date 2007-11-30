/**
 * 
 */
package org.concord.otrunk.view.document;

import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.view.OTViewEntry;

/**
 * @author scott
 *
 */
public interface OTDocumentViewConfig extends DocumentConfig, OTViewEntry 
{
	// Methods duplicated here because reflection won't understand that these are indeed
	// extended from DocumentConfig. These don't do anything, but must remain.
	public String getMode();
	public void setMode(String mode);
	
	public String getCss();
	public void setCss(String css);
	
	public OTObjectList getCssBlocks();
	
	public boolean getViewContainerIsUpdateable();
	public void setViewContainerIsUpdateable(boolean viewContainerIsUpdateable);
	public static boolean DEFAULT_viewContainerIsUpdateable = true;
}
