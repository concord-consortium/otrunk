package org.concord.otrunk.overlay;

import java.awt.event.ActionListener;
import java.net.Authenticator;
import java.util.Iterator;
import java.util.Set;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.view.OTAction;
import org.concord.framework.otrunk.view.OTActionContext;
import org.concord.otrunk.util.StandardPasswordAuthenticator;

public class OTOverlaySaveAction extends DefaultOTObject
	implements OTAction {

	public static interface ResourceSchema extends OTResourceSchema
	{
		
	}
	
	public OTOverlaySaveAction(ResourceSchema resources) {
		super(resources);

		authenticator = new StandardPasswordAuthenticator();
	}

	ActionListener clickAction;
	Authenticator authenticator;

	public void doAction(OTActionContext context) {
		
		OTUserOverlayManager userOverlayManager = 
			context.getViewContext().getViewService(OTUserOverlayManager.class);
		
			// ArrayList<OTDatabase> allDatabases = userOverlayManager.getOverlayDatabases();
			Set<OTOverlay> overlaySet = userOverlayManager.getOverlays();

			for (Iterator<OTOverlay> overlays = overlaySet.iterator(); overlays.hasNext();) {
				try {
					userOverlayManager.remoteSave(overlays.next());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		
	}

	public String getActionText() {
		return "Save Overlays";
	}	
}
