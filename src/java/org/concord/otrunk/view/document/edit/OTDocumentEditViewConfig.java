package org.concord.otrunk.view.document.edit;

import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.otrunk.view.document.DocumentConfig;
import org.concord.otrunk.view.document.OTDocumentViewConfig;

public interface OTDocumentEditViewConfig extends DocumentConfig, OTViewEntry
{
	public OTObjectList getObjectsToInsert();
	
	public OTDocumentViewConfig getDocumentViewConfig();
	
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
