/**
 * 
 */
package org.concord.otrunk.view;

import java.util.ArrayList;

import org.concord.framework.otrunk.view.OTFrameManager;
import org.concord.framework.otrunk.view.OTJComponentService;
import org.concord.framework.otrunk.view.OTViewContainer;

/**
 * @author scott
 *
 */
public class OTJComponentContainerHelper
{
	private ArrayList<OTViewContainerPanel> viewContainerPanels = 
		new ArrayList<OTViewContainerPanel>();
	private OTFrameManager frameManager;
	private String viewMode;
	private boolean useScrollPane = false;
	private boolean autoRequestFocus = false;
	private OTJComponentService jComponentService;
	private OTViewContainer parentContainer;
	
	public OTJComponentContainerHelper(OTFrameManager frameManager,
	                                   OTJComponentService jComponentService,
	                                   String viewMode)
	{
		this.frameManager = frameManager;
		this.viewMode = viewMode;
		this.jComponentService = jComponentService;		
	}
	                                   
	
	public void removeAllSubViews()
	{
		for(OTViewContainerPanel panel: viewContainerPanels){
			panel.setCurrentObject(null);
		}
		viewContainerPanels.clear();
	}
	
	public OTViewContainerPanel createViewContainerPanel()
	{
		OTViewContainerPanel viewContainerPanel =
		    createViewContainerPanel(frameManager, jComponentService, autoRequestFocus,
		        useScrollPane, viewMode, this.parentContainer);
		viewContainerPanels.add(viewContainerPanel);
		return viewContainerPanel;
	}

	public static OTViewContainerPanel createViewContainerPanel(OTFrameManager frameManager,
	    OTJComponentService jComponentService, boolean autoRequestFocus, boolean useScrollPane,
	    String viewMode, OTViewContainer parentContainer)
	{
		OTViewContainerPanel viewContainerPanel = new OTViewContainerPanel(frameManager);
		viewContainerPanel.setOTJComponentService(jComponentService);
		viewContainerPanel.setAutoRequestFocus(autoRequestFocus);
		viewContainerPanel.setUseScrollPane(useScrollPane);
		viewContainerPanel.setOpaque(false);
		viewContainerPanel.setViewMode(viewMode);
		viewContainerPanel.setParentContainer(parentContainer);
		return viewContainerPanel;
	}


	public boolean isAutoRequestFocus()
    {
    	return autoRequestFocus;
    }


	public void setAutoRequestFocus(boolean autoRequestFocus)
    {
    	this.autoRequestFocus = autoRequestFocus;
    }


	public boolean isUseScrollPane()
    {
    	return useScrollPane;
    }


	public void setUseScrollPane(boolean useScrollPane)
    {
    	this.useScrollPane = useScrollPane;
    }


	public String getViewMode()
    {
    	return viewMode;
    }


	public void setViewMode(String viewMode)
    {
    	this.viewMode = viewMode;
    }


	public void setParentContainer(OTViewContainer parentContainer)
    {
	    this.parentContainer = parentContainer;
    }


	public OTViewContainer getParentContainer()
    {
	    return parentContainer;
    }

	
}
