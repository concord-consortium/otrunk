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
public interface OTDocumentViewConfig 
	extends OTViewEntry 
{
	/**
	 * The mode the document view uses to render embedded components
	 * 
	 * @return
	 */
	public String getMode();
	public void setMode(String mode);
	
	public String getCss();
	public void setCss(String css);
	
	public OTObjectList getCssBlocks();
	
	public boolean getViewContainerIsUpdateable();
	public void setViewContainerIsUpdateable(boolean viewContainerIsUpdateable);
	public static boolean DEFAULT_viewContainerIsUpdateable = true;
}
