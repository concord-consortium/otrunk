package org.concord.otrunk.overlay;

import java.awt.event.ActionListener;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.view.OTAction;
import org.concord.framework.otrunk.view.OTActionContext;

public class OTOverlaySaveAction extends DefaultOTObject
	implements OTAction {
	
	private ResourceSchema resources;

	public static interface ResourceSchema extends OTResourceSchema
	{
		
	}
	
	public OTOverlaySaveAction(ResourceSchema resources) {
		super(resources);
		this.resources = resources;
	}

	ActionListener clickAction;

	public void doAction(OTActionContext context) {
		
		OTUserOverlayManager userOverlayManager = context.getViewContext().getViewService(OTUserOverlayManager.class);
		try {
	        userOverlayManager.remoteSaveAll(null);
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}

	public String getActionText() {
		return "Save Overlays";
	}	
}
