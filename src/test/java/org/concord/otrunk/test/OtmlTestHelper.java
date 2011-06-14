package org.concord.otrunk.test;

import java.net.URL;

import org.concord.framework.otrunk.OTControllerService;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTUser;
import org.concord.framework.otrunk.view.OTView;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTTransientMapID;
import org.concord.otrunk.view.OTConfig;
import org.concord.otrunk.view.OTViewContainerPanel;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

public class OtmlTestHelper
{
	private OTViewerHelper viewerHelper;
	private OTDatabase mainDb;
	private OTrunkImpl otrunk;
	private OTControllerService controllerService;

	public void initOtrunk(URL authoredContent) throws Exception {
		System.setProperty(OTConfig.NO_USER_PROP, "true");
	    viewerHelper = new OTViewerHelper();
		mainDb = viewerHelper.loadOTDatabase(authoredContent);
		viewerHelper.loadOTrunk(mainDb, null);
		otrunk = (OTrunkImpl) viewerHelper.getOtrunk();
	}

	public OTObject getObject(String localId) throws Exception {
		OTID objectId = ((XMLDatabase)viewerHelper.getOtDB()).getOTIDFromLocalID(localId);
		return otrunk.getRootObjectService().getOTObject(objectId);
	}	
	
	public OTObject getObject(String localId, boolean userVersion) throws Exception {
		OTObject otObject = getObject(localId);
		if (userVersion) {
    		OTUser user = otrunk.getUsers().get(0);
    		otObject = otrunk.getUserRuntimeObject(otObject, user);
    		if(!(otObject.getGlobalId() instanceof OTTransientMapID)){
    			throw new RuntimeException("user object not valid");
    		}
		}

		return otObject;
	}
	
	/*
	 * FIXME the sleep 2000 is pretty annoying here, and it might not always work
	 */
	public OTView getView(OTObject object) throws Exception {
		OTViewContainerPanel panel = viewerHelper.createViewContainerPanel();
		panel.setCurrentObject(object);
		Thread.sleep(2000);
		return panel.getView();
	}
	
	public OTControllerService getControllerService() {
		if(controllerService != null){
			return controllerService;
		}
		controllerService = otrunk.getRootObjectService().createControllerService();
		return controllerService;
	}
	
}
