package org.concord.otrunk.overlay;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.view.OTActionContext;
import org.concord.framework.otrunk.view.OTSelectableAction;

public class OTAddOverlayAction extends DefaultOTObject
    implements OTSelectableAction
{

	public static interface MyResourceSchema extends OTResourceSchema
	{
		public OTOverlayGroup getOverlayGroup();
		public void setOverlayGroup(OTOverlayGroup overlayGroup);
		
		public OTOverlay getOverlay();
		public void setOverlay(OTOverlay overlay);
		
		public String getActionText();
		public void setActionText(String actionText);
	}
	protected MyResourceSchema resources;
	
	public OTAddOverlayAction(MyResourceSchema resources)
    {
		super(resources);
		this.resources = (MyResourceSchema) resources;
    }

	public void doAction(OTActionContext context)
	{
		resources.getOverlayGroup().getOverlays().clear();
		resources.getOverlayGroup().getOverlays().add(resources.getOverlay());
	}

	public String getActionText()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSelected(OTActionContext context)
    {
	    return resources.getOverlayGroup().getOverlays().getVector().contains(resources.getOverlay());
    }
}
