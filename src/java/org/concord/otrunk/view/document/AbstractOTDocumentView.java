package org.concord.otrunk.view.document;

import java.awt.event.ContainerListener;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerAware;
import org.concord.framework.otrunk.view.OTViewContainerChangeEvent;
import org.concord.framework.otrunk.view.OTViewContainerListener;
import org.concord.otrunk.view.OTJComponentContainerHelper;
import org.concord.otrunk.view.OTViewContainerPanel;

public class AbstractOTDocumentView extends OTTextObjectView
    implements OTViewContainerAware, OTViewContainerListener
{
	private OTViewContainer viewContainer;

	private String viewMode = null;

	private OTJComponentContainerHelper containerHelper;

	public void setViewContainer(OTViewContainer container)
	{
		super.setViewContainer(container);
		viewContainer = container;
		if (viewContainer != null) {
			viewContainer.setUpdateable(true);
		}
	}

	public OTViewContainer getViewContainer()
	{
		return viewContainer;
	}

	public String getViewMode()
	{
		if (viewMode == null){
			return myMode;
		}
		return viewMode;
	}

	public void setViewMode(String viewMode)
	{
		this.viewMode = viewMode;
	}

	public void removeAllSubViews()
	{
		if (containerHelper != null) {
			containerHelper.removeAllSubViews();
		}
	}

	public OTViewContainerPanel createViewContainerPanel()
	{
		if (containerHelper == null) {
			containerHelper = new OTJComponentContainerHelper(
			        getFrameManager(), getJComponentService(), getViewMode());
			containerHelper.setParentContainer(viewContainer);
		}
		
		OTViewContainerPanel viewContainerPanel = containerHelper.createViewContainerPanel();
		viewContainerPanel.addViewContainerListener(this);
		
		return viewContainerPanel;
	}

	public void viewClosed()
	{
		super.viewClosed();

		if (containerHelper != null) {
			containerHelper.removeAllSubViews();
		}
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
		if (linkId == null) {
			return null;
		}
		return getReferencedObject(linkId);
	}

	public void currentObjectChanged(OTViewContainerChangeEvent evt)
    {
		// to be handled by OTDocumentView
    }
	
	public OTrunk getOTrunk() {
		return (OTrunk) getViewService(OTrunk.class);
	}
}
