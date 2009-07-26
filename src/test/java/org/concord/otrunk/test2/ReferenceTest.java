package org.concord.otrunk.test2;

import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTUser;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDataPropertyReference;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTIDFactory;
import org.concord.otrunk.datamodel.OTTransientMapID;
import org.concord.otrunk.view.OTConfig;
import org.concord.otrunk.view.OTViewerHelper;

public class ReferenceTest extends TestCase
{
	private static final Logger logger = Logger.getLogger(OverlayObjectCopyTest.class.getCanonicalName());
	private static URL authoredContent = OverlayObjectCopyTest.class.getResource("/reference-test-authored.otml");
	private static URL learnerContent = OverlayObjectCopyTest.class.getResource("/reference-test-learner.otml");
	private OTViewerHelper viewerHelper;
	private OTDatabase mainDb;
	private OTDatabase learnerDb;
	private OTrunkImpl otrunk;
	private String documentUUID = "8868f212-41db-4925-a885-8794c8eaed35";
	
	public void testOTIDEquals() throws Exception {
		OTID otid1 = OTIDFactory.createOTID("8868f212-41db-4925-a885-8794c8eaed35!/foo");
		OTID otid2 = OTIDFactory.createOTID("8868f212-41db-4925-a885-8794c8eaed35!/foo");
		
		assertTrue(otid1.equals(otid2));
	}
	
	public void testOTIDContains() throws Exception {
		OTID otid1 = OTIDFactory.createOTID("8868f212-41db-4925-a885-8794c8eaed35!/foo");
		OTID otid2 = OTIDFactory.createOTID("8868f212-41db-4925-a885-8794c8eaed35!/foo");
		
		ArrayList<OTID> list = new ArrayList<OTID>();
		list.add(otid1);
		
		assertTrue(list.contains(otid2));
	}
	
	public void testNoLearnerDataSingleParentDirectOnly() throws Exception {
		initOtrunk(false);
		
		ArrayList<OTID> expectedReferences = new ArrayList<OTID>();
		expectedReferences.add(OTIDFactory.createOTID(documentUUID + "!" + "/nlr_p1"));
		
		OTObject obj = getObject("no_learner_references", false);	
		
		ArrayList<ArrayList<OTDataPropertyReference>> references = otrunk.getIncomingReferences(obj.getGlobalId(), false);
		makeSureArraysMatch(extractReferences(references), expectedReferences);
	}
	
	public void testNoLearnerDataSingleParentDirectAndSibling() throws Exception {
		initOtrunk(false);
		
		ArrayList<OTID> expectedReferences = new ArrayList<OTID>();
		expectedReferences.add(OTIDFactory.createOTID(documentUUID + "!" + "/nlrs_p1"));
		expectedReferences.add(OTIDFactory.createOTID(documentUUID + "/" + "/root"));
		
		OTObject obj = getObject("no_learner_references_sibling", false);
		ArrayList<ArrayList<OTDataPropertyReference>> references = otrunk.getIncomingReferences(obj.getGlobalId(), false);
		makeSureArraysMatch(extractReferences(references), expectedReferences);
	}
	
	public void testNoLearnerDataSingleParentDirectAndSiblingViaObjectMap() throws Exception {
		initOtrunk(false);
		
		ArrayList<OTID> expectedReferences = new ArrayList<OTID>();
		expectedReferences.add(OTIDFactory.createOTID(documentUUID + "!" + "/foo_bar"));
		expectedReferences.add(OTIDFactory.createOTID(documentUUID + "/" + "/root"));
		
		OTObject obj = getObject("nlr_p1", false);
		ArrayList<ArrayList<OTDataPropertyReference>> references = otrunk.getIncomingReferences(obj.getGlobalId(), false);
		makeSureArraysMatch(extractReferences(references), expectedReferences);
	}
	
	public void testNoLearnerDataSingleParentDirectAndSiblingViaObjectList() throws Exception {
		initOtrunk(false);
		
		ArrayList<OTID> expectedReferences = new ArrayList<OTID>();
		expectedReferences.add(OTIDFactory.createOTID(documentUUID + "!" + "/object_list"));
		expectedReferences.add(OTIDFactory.createOTID(documentUUID + "/" + "/root"));
		
		OTObject obj = getObject("object_list_sibling", false);
		ArrayList<ArrayList<OTDataPropertyReference>> references = otrunk.getIncomingReferences(obj.getGlobalId(), false);
		makeSureArraysMatch(extractReferences(references), expectedReferences);
		
		// check if the propertyName of the reference is what it should be
		assertEquals("objectList[0]", references.get(1).get(0).getProperty());
	}
	
	private void makeSureArraysMatch(ArrayList<OTID> results, ArrayList<OTID> expected) throws Exception {
		ArrayList<OTID> notExpected = new ArrayList<OTID>();
		notExpected.addAll(results);
		notExpected.removeAll(expected);
		
		ArrayList<OTID> notReturned = new ArrayList<OTID>();
		notReturned.addAll(expected);
		notReturned.removeAll(results);
		
		String msg = "Results mismatched. Found: ";
		for (OTID id : notExpected) {
			msg += id.toString() + ",";
		}
		msg += " Didn't Find: ";
		for (OTID id : notReturned) {
			msg += id.toString() + ",";
		}
		
		assertTrue(msg, (notExpected.size() + notReturned.size()) == 0);
	}
	
	private OTObject getObject(String localId, boolean userVersion) throws Exception
	{
		// load in the first argument as an otml file
		// assume the root object is a folder, and then 
		// get the first child of the folder and
		// copy it and store the copy as the second
		// object in the folder

		// GET THE OBJECT
		logger.fine("Getting object");
		// OTFolder root = (OTFolder)viewerHelper.getRootObject();
		// OTObject first = (OTObject)root.getChild(firstIndex);
		OTID objectId = OTIDFactory.createOTID(documentUUID + "!/" + localId);
		OTObject first = otrunk.getRootObjectService().getOTObject(objectId);
		if (userVersion) {
    		OTUser user = otrunk.getUsers().get(0);
    		first = otrunk.getUserRuntimeObject(first, user);
    		assertTrue(first.getGlobalId() instanceof OTTransientMapID);
		}

		return first;
	}
	
	private ArrayList<OTID> extractReferences(ArrayList<ArrayList<OTDataPropertyReference>> paths) {
		ArrayList<OTID> references = new ArrayList<OTID>();
		for (ArrayList<OTDataPropertyReference> path : paths) {
			references.add(path.get(0).getSource());
		}
		return references;
	}
	
	private void initOtrunk(boolean loadLearnerData)
    throws Exception
{
	logger.finer("loading otrunk");
	if (loadLearnerData) {
		System.setProperty(OTConfig.SINGLE_USER_PROP, "true");
	} else {
		System.setProperty(OTConfig.NO_USER_PROP, "true");
	}
    viewerHelper = new OTViewerHelper();
	mainDb = viewerHelper.loadOTDatabase(authoredContent);
	viewerHelper.loadOTrunk(mainDb, null);
	
	if (loadLearnerData) {
		learnerDb = viewerHelper.loadOTDatabase(learnerContent);
    	viewerHelper.loadUserData(learnerDb, null);
	}
	otrunk = (OTrunkImpl) viewerHelper.getOtrunk();
}
	
}
