package org.concord.otrunk.view.document;

import java.util.Vector;

import org.concord.framework.otrunk.view.OTFrameManager;
import org.concord.framework.otrunk.view.OTFrameManagerAware;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerAware;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.framework.otrunk.view.OTViewFactoryAware;
import org.concord.otrunk.view.OTViewContainerPanel;

public class AbstractOTDocumentView extends OTTextObjectView 
	implements OTFrameManagerAware, OTViewContainerAware, 
		OTViewFactoryAware 
{
	private OTFrameManager frameManager;
    private OTViewFactory viewFactory = null;
	private OTViewContainer viewContainer;

	private Vector viewContainerPanels = new Vector();
	
	public void setFrameManager(OTFrameManager frameManager) 
	{
		this.frameManager = frameManager;
	}
	
	public OTFrameManager getFrameManager() 
	{
		return frameManager;
	}

	public void setViewContainer(OTViewContainer container) 
	{
		viewContainer = container;
	}

	public OTViewContainer getViewContainer()
	{
		return viewContainer;
	}
	
	public void setViewFactory(OTViewFactory factory) 
	{
		viewFactory = factory;
	}

	public OTViewFactory getViewFactory()
	{
		return viewFactory;
	}

	public OTViewContainerPanel createtViewContainerPanel()
	{
		OTViewContainerPanel viewContainerPanel = 
        	new OTViewContainerPanel(getFrameManager());
        viewContainerPanel.setOTViewFactory(getViewFactory());
        viewContainerPanel.setAutoRequestFocus(false);
        viewContainerPanel.setUseScrollPane(false);
        viewContainerPanel.setOpaque(false);
        
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
}
