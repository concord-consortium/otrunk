package org.concord.otrunk.overlay;

import java.awt.event.ActionListener;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.Iterator;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.OTAction;
import org.concord.framework.otrunk.view.OTActionContext;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.util.StandardPasswordAuthenticator;
import org.concord.otrunk.view.OTViewer;
import org.concord.otrunk.xml.XMLDatabase;

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
		
		OTObjectServiceImpl objService = (OTObjectServiceImpl) getOTObjectService();
		OTrunkImpl otrunk = (OTrunkImpl) objService.getOTrunkService(OTrunk.class);
		
		OTUserOverlayManager userOverlayManager = 
			context.getViewContext().getViewService(OTUserOverlayManager.class);
		try {
			ArrayList<OTDatabase> allDatabases = userOverlayManager.getOverlayDatabases();

			for (Iterator<OTDatabase> databases = allDatabases.iterator(); databases.hasNext();) {
				XMLDatabase db = (XMLDatabase) ((CompositeDatabase) databases.next()).getActiveOverlayDb();
				otrunk.remoteSaveData(db, OTViewer.HTTP_PUT, authenticator);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public String getActionText() {
		return "Save Overlays";
	}	
}
