package org.concord.otrunk.view.document;

import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerAware;
import org.concord.otrunk.view.OTViewContainerPanel;

public class AbstractOTDocumentView extends OTTextObjectView 
	implements OTViewContainerAware
{
	private OTViewContainer viewContainer;

	private String viewMode = null;
	
	private Vector viewContainerPanels = new Vector();
	
	public void setViewContainer(OTViewContainer container) 
	{
		viewContainer = container;
	}

	public OTViewContainer getViewContainer()
	{
		return viewContainer;
	}
	
	public String getViewMode() 
	{
		return viewMode;
	}

	public void setViewMode(String viewMode) 
	{
		this.viewMode = viewMode;
	}

	public OTViewContainerPanel createtViewContainerPanel()
	{
		OTViewContainerPanel viewContainerPanel = 
        	new OTViewContainerPanel(getFrameManager());
        viewContainerPanel.setOTViewFactory(getViewFactory());
        viewContainerPanel.setAutoRequestFocus(false);
        viewContainerPanel.setUseScrollPane(false);
        viewContainerPanel.setOpaque(false);
        viewContainerPanel.setViewMode(getViewMode());
        
        viewContainerPanels.add(viewContainerPanel);
        return viewContainerPanel;
	}
	
	public void removeAllSubViews()
	{
		for(int i=0; i<viewContainerPanels.size(); i++){
			OTViewContainerPanel panel = 
				(OTViewContainerPanel) viewContainerPanels.get(i);
			panel.setCurrentObject(null);
		}
		viewContainerPanels.removeAllElements();
	}
	
	public void viewClosed()
	{
		super.viewClosed();
		
		removeAllSubViews();
	}
	
	public OTObject getReferencedObject(OTID id)
	{	
    	try {
            OTObjectService objService = pfObject.getOTObjectService();
    		return objService.getOTObject(id);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
	}
	
	public OTObject getReferencedObject(String id)
	{
        OTObjectService objService = pfObject.getOTObjectService();
	    OTID linkId = objService.getOTID(id);
	    if(linkId == null) {
	        return null;
	    }
	    return getReferencedObject(linkId);
	}
}
