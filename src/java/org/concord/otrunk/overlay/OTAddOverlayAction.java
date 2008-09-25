package org.concord.otrunk.overlay;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.concord.framework.otrunk.view.OTAction;
import org.concord.framework.otrunk.view.OTActionContext;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.framework.otrunk.view.OTSelectableAction;
import org.concord.otrunk.OTSystem;
import org.concord.otrunk.view.OTFrameDisplayAction.MyResourceSchema;

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
