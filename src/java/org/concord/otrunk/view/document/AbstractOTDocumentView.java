package org.concord.otrunk.view.document;

import org.concord.framework.otrunk.view.OTFrameManager;
import org.concord.framework.otrunk.view.OTFrameManagerAware;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerAware;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.framework.otrunk.view.OTViewFactoryAware;

public class AbstractOTDocumentView extends OTTextObjectView 
	implements OTFrameManagerAware, OTViewContainerAware, 
		OTViewFactoryAware 
{
	private OTFrameManager frameManager;
    private OTViewFactory viewFactory = null;
	private OTViewContainer viewContainer;

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
	
}
