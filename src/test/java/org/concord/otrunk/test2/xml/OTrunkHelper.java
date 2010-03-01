package org.concord.otrunk.test2.xml;

import java.net.URL;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTUser;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTIDFactory;
import org.concord.otrunk.view.OTConfig;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

public class OTrunkHelper
{
	private static final Logger logger = Logger.getLogger(OTrunkHelper.class.getCanonicalName());
	private OTViewerHelper viewerHelper;
	private XMLDatabase mainDb;
	
	private XMLDatabase learnerDb;
	private OTrunkImpl otrunk;
	
	public OTObject getObject(String docId, String localId, boolean useLearnerDb) throws Exception {
		OTID objectId = OTIDFactory.createOTID(docId + "!/" + localId);
		OTObject first = otrunk.getRootObjectService().getOTObject(objectId);
		if (useLearnerDb) {
    		OTUser user = otrunk.getUsers().get(0);
    		first = otrunk.getUserRuntimeObject(first, user);
		}
		return first;
	}
	
	public OTDataObject getDataObject(String docId, String localId, boolean useLearnerDb) throws Exception {
		OTObject obj = getObject(docId, localId, useLearnerDb);
//		OTDataObject dataObj = mainDb.getOTDataObject(null, obj.getGlobalId());
//		if (useLearnerDb) {
//			dataObj = learnerDb.getActiveDeltaObject(dataObj);
//		}
		OTDataObject dataObj = otrunk.getDataObjectFinder().findDataObject(obj.getGlobalId());
		return dataObj;
	}
	
	public void initOtrunk(URL authoredContent) throws Exception {
		initOtrunk(authoredContent, null, false);
	}
	
	public void initOtrunk(URL authoredContent, URL learnerContent) throws Exception {
		initOtrunk(authoredContent, learnerContent, true);
	}
	
	public void initOtrunk(URL authoredContent, URL learnerContent, boolean withLearner)
        throws Exception
    {
		logger.fine("loading otrunk");
		System.setProperty(OTConfig.SINGLE_USER_PROP, "true");
	    viewerHelper = new OTViewerHelper();
	    
		mainDb = (XMLDatabase) viewerHelper.loadOTDatabase(authoredContent);
		viewerHelper.loadOTrunk(mainDb, null);
		
		if (withLearner) {
			learnerDb = (XMLDatabase) viewerHelper.loadOTDatabase(learnerContent);
			viewerHelper.loadUserData(learnerDb, null);
		}
		
		otrunk = (OTrunkImpl) viewerHelper.getOtrunk();
    }

	public OTDatabase getMainDb()
    {
	    return mainDb;
    }
	
	public OTDatabase getLearnerDb()
    {
	    return learnerDb;
    }
}
