/**
 * 
 */
package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.view.OTFrame;

/**
 * @author scott
 *
 */
public interface OTAuthorEmbedDefaultViewConfig extends OTObjectInterface 
{
	public OTFrame getFrame();
	public void setFrame(OTFrame frame);
	
	public String getPopupViewMode();
	public void setPopupViewMode(String mode);
}
