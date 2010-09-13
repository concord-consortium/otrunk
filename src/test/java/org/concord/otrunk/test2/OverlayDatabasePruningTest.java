package org.concord.otrunk.test2;

import java.net.URL;
import java.util.Random;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTUser;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTTransientMapID;
import org.concord.otrunk.overlay.CompositeDatabase;
import org.concord.otrunk.overlay.OTOverlay;
import org.concord.otrunk.overlay.OverlayImpl;
import org.concord.otrunk.test.OTBasicTestObject;
import org.concord.otrunk.test.OTPrimitivesTestObject;
import org.concord.otrunk.test.OTResourceListTestObject;
import org.concord.otrunk.view.OTConfig;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class OverlayDatabasePruningTest
{
		private static final Logger logger = Logger.getLogger(OverlayDatabasePruningTest.class.getCanonicalName());
		private static URL authoredContent = OverlayObjectCopyTest.class.getResource("/overlay-pruning-authored.otml");
		private static URL learnerContent = OverlayObjectCopyTest.class.getResource("/overlay-pruning-learner.otml");
		private static OTViewerHelper viewerHelper;
		private static OTDatabase mainDb;
		private static OTDatabase learnerDb;
		private static OTrunkImpl otrunk;
		private static OTObjectServiceImpl overlayObjService;
		private static OTDatabase overlayDb;
		private static OverlayImpl overlayImpl;
		private static OTOverlay overlay;
		
		@BeforeClass
		public static void setup() throws Exception {
			initOtrunk();
			initOverlay();
		}
		
		@Test
		public void databasePruning() throws Exception {
			// load in the first argument as an otml file
			// assume the root object is a folder, and then 
			// get the first child of the folder and
			// copy it and store the copy as the second
			// object in the folder
			
			// GET THE OBJECT
			logger.fine("Getting object");
			OTBasicTestObject root = (OTBasicTestObject) viewerHelper.getRootObject();
			OTUser user = otrunk.getUsers().get(0);
			root = (OTBasicTestObject) otrunk.getUserRuntimeObject(root, user);
			
			Assert.assertTrue(root.getGlobalId() instanceof OTTransientMapID);
			
			// edit the child objects
			OTResourceListTestObject resourceListObject = root.getOTObjectService().createObject(OTResourceListTestObject.class);
			root.setReference(resourceListObject);
			
			OTResourceList resourceList = resourceListObject.getResourceList();
			for (int i = 0; i < 4; i++) {
				OTPrimitivesTestObject testObj = root.getOTObjectService().createObject(OTPrimitivesTestObject.class);
				testObj.setString("primary: " + i);
				resourceList.add(testObj);
			}
			
			copyIntoOverlay(root);
			
			// VERIFY THAT NON-DELTA-OBJECTS HAS 5 OBJECTS
			// there should be one OTResourceListTestObject and 4 OTPrimitivesTestObject
			OTResourceList nonDeltas = overlay.getNonDeltaObjects();
			Assert.assertTrue("There should be 1 non-delta object. There were: " + nonDeltas.size(), nonDeltas.size() == 1);

			checkCounts(nonDeltas, 1, 0, 0);
			
			// edit the child objects
			resourceList.remove(new Random().nextInt(resourceList.size()));
			resourceList.remove(new Random().nextInt(resourceList.size()));
			for (int i = 0; i < 4; i++) {
				OTPrimitivesTestObject testObj = root.getOTObjectService().createObject(OTPrimitivesTestObject.class);
				testObj.setString("secondary: " + i);
				resourceList.add(testObj);
			}
			// copy the objects
			copyIntoOverlay(root);
			
			// PRUNE THE OVERLAY
			logger.fine("Pruning overlay");
			overlayImpl.pruneNonDeltaObjects();
			
			// TODO VERIFY THAT NON-DELTA-OBJECTS HAS P OBJECTS
			nonDeltas = overlay.getNonDeltaObjects();
			Assert.assertTrue("There should be 1 non-delta object. There were: " + nonDeltas.size(), nonDeltas.size() == 1);

			checkCounts(nonDeltas, 1, 0, 0);
		}

		private void copyIntoOverlay(OTBasicTestObject root)
            throws Exception
        {
	        // get the overlay version of the object
			OTID id = root.getGlobalId().getMappedId();
			OTBasicTestObject overlayVersion = (OTBasicTestObject) overlayObjService.getOTObject(id);
			
	        // COPY THE OBJECT
			logger.fine("Copying objects:\n");
			logger.fine("Student root: " + root.getGlobalId().getMappedId().toString());
			logger.fine("Overlay root: " + overlayVersion.getGlobalId().getMappedId().toString());
			((OTObjectServiceImpl)root.getOTObjectService()).copyInto(root, overlayVersion, -1, true);
			
			OTResourceListTestObject overlayResourceList = (OTResourceListTestObject) overlayVersion.getReference();
			logger.fine("Overlay resource list id: " + overlayResourceList.getGlobalId().getMappedId());
			
			for (Object obj : overlayResourceList.getResourceList()) {
				logger.fine("Resource child: " + ((OTObject)obj).getGlobalId().getMappedId());
			}
        }
		
		private void checkCounts(OTResourceList nonDeltas, int expectedResourceLists, int expectedPrimitives, int expectedOther) throws Exception {
			int resourceLists = 0;
			int primitives = 0;
			int other = 0;
			for (Object obj : nonDeltas) {
				if (obj instanceof OTID) {
					obj = overlay.getOTObjectService().getOTObject((OTID) obj);
				}
				if (obj instanceof OTResourceListTestObject) {
					resourceLists++;
				} else if (obj instanceof OTPrimitivesTestObject) {
					primitives++;
				} else {
					other++;
				}
			}
			
			Assert.assertTrue("Should be " + expectedResourceLists + " resource list non-delta. Were: " + resourceLists, expectedResourceLists == resourceLists);
			Assert.assertTrue("Should be " + expectedPrimitives + " primitive non-delta. Were: " + primitives, expectedPrimitives == primitives);
			Assert.assertTrue("Should be " + expectedOther + " other non-delta. Were: " + other, expectedOther == other);
		}
		
		private static void initOtrunk() throws Exception {
			logger.fine("loading otrunk");
			System.setProperty(OTConfig.SINGLE_USER_PROP, "true");
		    viewerHelper = new OTViewerHelper();
			mainDb = viewerHelper.loadOTDatabase(authoredContent);
			learnerDb = viewerHelper.loadOTDatabase(learnerContent);
			viewerHelper.loadOTrunk(mainDb, null);
			viewerHelper.loadUserData(learnerDb, null);
			otrunk = (OTrunkImpl) viewerHelper.getOtrunk();
	    }
		
		private static void initOverlay() throws Exception {
			logger.fine("creating default overlay");
			overlayDb = new XMLDatabase();
			OTObjectService tempObjService = otrunk.createObjectService(overlayDb);
			overlay = tempObjService.createObject(OTOverlay.class);
			overlayDb.setRoot(overlay.getGlobalId());
			
			initOverlayObjService(overlay);
		}
		
		private static void initOverlayObjService(OTOverlay overlay) throws Exception {
			logger.fine("setting up overlay object service");
			overlayImpl = new OverlayImpl(overlay);
			CompositeDatabase db = new CompositeDatabase(otrunk.getDataObjectFinder(), overlayImpl);
		  	overlayObjService = otrunk.createObjectService(db);
		}
}
