package org.concord.otrunk.view;

import org.concord.framework.otrunk.view.OTViewEntry;

public interface OTChooserViewEntry extends OTViewEntry {
	
	public String getPropertyName();
	public void setPropertyName(String attributeName);
    
    public String getFinalViewMode();
    public void setFinalViewMode(String finalViewMode);
}
