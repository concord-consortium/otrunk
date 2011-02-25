package org.concord.otrunk.test2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTUser;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.OTrunkUtil;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTIDFactory;
import org.concord.otrunk.datamodel.OTTransientMapID;
import org.concord.otrunk.overlay.CompositeDatabase;
import org.concord.otrunk.overlay.OTOverlay;
import org.concord.otrunk.overlay.OverlayImpl;
import org.concord.otrunk.view.OTConfig;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

public class OverlayObjectCopyTest extends TestCase
{
	private static final Logger logger = Logger.getLogger(OverlayObjectCopyTest.class.getCanonicalName());
	private static URL authoredContent = OverlayObjectCopyTest.class.getResource("/overlay-copy-test-authored.otml");
	private static URL learnerContent = OverlayObjectCopyTest.class.getResource("/overlay-copy-test-learner.otml");
	private OTViewerHelper viewerHelper;
	private OTDatabase mainDb;
	private OTDatabase learnerDb;
	private OTrunkImpl otrunk;
	private OTObjectServiceImpl overlayObjService;
	private OTDatabase overlayDb;
	private String documentUUID = "9d4f759c-3166-4c54-a6ab-416e546d9f62";
	
	/*
	 * Ways to embed objects and tests for them
	 * (- test description                          [testMethodName] )
	 * 
	 *  - object with primitive attributes (int, float, String, ...)
	 *    - none changed                            [testSimpleCopyNoChange]
	 *    - one changed
	 *    - some changed                            [testSimpleCopySomeChange]
	 *    - all changed                             [testSimpleCopyAllChange]
	 *  - object with blob attribute
	 *    - none changed                            [testBlobNoChange]
	 *    - some changed                            [testBlobSomeChange]
	 *    - added                                   [testBlobAddChange]
	 *    - deleted                                 [testBlobDeleteChange]
	 *  - object with resource map
	 *    - no changes                              [testResourceMapNoChange]
	 *    - value changes 
	 *      - one value changed
	 *      - some values changed
	 *      - all values changed                    [testResourceMapValueChange]
	 *    - key/value removed
	 *      - one value removed                     [testResourceMapDeleteChange]
	 *      - some values removed
	 *      - all values removed
	 *    - key/value added
	 *      - one value added                       [testResourceMapAddChange]
	 *      - some values added
	 *  - object with resource list
	 *    - no change                               [testResourceListNoChange]
	 *    - value changes
	 *      - one value changed
	 *      - some values changed                   [testResourceListSomeChange]
	 *      - all values changed
	 *    - key/value removed
	 *      - one value removed                     [testResourceListDeleteChange]
	 *      - some values removed
	 *      - all values removed
	 *    - key/value added
	 *      - one value added                       [testResourceListAddChange]
	 *      - some values added
	 *  - object with object list
	 *    - no change                               [testObjectListNoChange]
	 *    - value changes
	 *      - one value changed
	 *      - some values changed                   [testObjectListSomeChange]
	 *      - all values changed
	 *    - value removed
	 *      - one value removed                     [testObjectListDeleteChange]
	 *      - some values removed
	 *      - all values removed
	 *    - value added
	 *      - one value added                       [testObjectListAddChange]
	 *      - some values added
	 *  - object with object map
	 *    - no change                               [testObjectMapNoChange]
	 *    - value changes
	 *      - one value changed                     [testObjectMapOneChange]
	 *      - some values changed
	 *      - all values changed
	 *    - key/value removed
	 *      - one value removed                     [testObjectMapDeleteChange]
	 *      - some values removed
	 *      - all values removed
	 *    - key/value added
	 *      - one value added                       [testObjectMapAddChange]
	 *      - some values added
	 *  - object with OTXMLString (ex, OTCompoundDoc)
	 *    - no change                               [testDocumentNoChange]
	 *    - text change                             [testDocumentTextChange]
	 *    - referenced object changed               [testDocumentReferenceChange]
	 *  - object with other object as attribute
	 *    - no change                               [testBasicReferenceNoChange]
	 *    - reference removed                       [testBasicReferenceDeleteChange]
	 *    - reference added                         [testBasicReferenceAddChange]
	 *    - referenced object changed               [testBasicReferenceAllChange]
	 *      - change can be all of the previously mentioned tests
	 *  - various levels of nested-ness
	 *    - change in grandparent                   [testNestedGrandchildChange]
	 *    - change in greatgrandparent              [testNestedGreatGrandchildChange]
	 *    - change in greatgreatgrandparent         [testNestedGreat2GrandchildChange]
	 *    - change in greatgreatgreatgrandparent    [testNestedGreat3GrandchildChange]
	 *  - various combinations of types of objects nesting each other
	 *    - object -> object -> resourcemap
	 *    - object -> object -> objectmap
	 *    - object -> object -> objectlist
	 *    - object -> object -> resourcelist
	 *    - object -> document -> object
	 *    - object -> document -> object -> resourcemap    [testObjectDocumentResourceMap]
	 *    - object -> document -> object -> objectmap      [testObjectDocumentObjectMap]
	 *    - object -> document -> object -> objectlist     [testObjectDocumentResourcelist]
	 *    - object -> document -> object -> resourcelist   [testObjectDocumentResourcelist]
	 *  - various levels of embedded objects with various types of ids
	 */

	public void testSimpleCopyNoChange() throws Exception {
		helper("primitive_no_changes");
	}
	
	public void testSimpleCopySomeChange() throws Exception {
		helper("primitive_some_changes");
	}
	
	public void testSimpleCopyAllChange() throws Exception {
		helper("primitive_all_changes");
	}
	
	public void testBlobNoChange() throws Exception {
		helper("blob_no_changes");
	}
	
	public void testBlobSomeChange() throws Exception {
		helper("blob_some_changes");
	}
	
	public void testBlobAddChange() throws Exception {
		helper("blob_add_changes");
	}
	
	public void testBlobDeleteChange() throws Exception {
		helper("blob_delete_changes");
	}
	
	public void testResourceMapNoChange() throws Exception {
		helper("resource_map_no_changes");
	}
	
	public void testResourceMapValueChange() throws Exception {
		helper("resource_map_change_value");
	}
	
	public void testResourceMapAddChange() throws Exception {
		helper("resource_map_add_entry");
	}
	
	public void testResourceMapDeleteChange() throws Exception {
		helper("resource_map_delete_entry");
	}
	
	public void testResourceListNoChange() throws Exception {
		helper("resource_list_no_change");
	}
	
	public void testResourceListSomeChange() throws Exception {
		helper("resource_list_some_change");
	}
	
	public void testResourceListAddChange() throws Exception {
		helper("resource_list_add_change");
	}
	
	public void testResourceListDeleteChange() throws Exception {
		helper("resource_list_delete_change");
	}
	
	public void testObjectMapNoChange() throws Exception {
		helper("object_map_no_change");
	}
	
	public void testObjectMapOneChange() throws Exception {
		helper("object_map_one_change");
	}
	
	public void testObjectMapAddChange() throws Exception {
		helper("object_map_add_change");
	}
	
	public void testObjectMapDeleteChange() throws Exception {
		helper("object_map_delete_change");
	}
	
	public void testObjectListNoChange() throws Exception {
		helper("object_list_no_change");
	}
	
	public void testObjectListSomeChange() throws Exception {
		helper("object_list_some_change");
	}
	
	public void testObjectListAddChange() throws Exception {
		helper("object_list_add_change");
	}
	
	public void testObjectListDeleteChange() throws Exception {
		helper("object_list_delete_change");
	}
	
	public void testDocumentNoChange() throws Exception {
		helper("document_no_change");
	}
	
	public void testDocumentTextChange() throws Exception {
		helper("document_text_change");
	}
	
	public void testDocumentReferenceChange() throws Exception {
		helper("document_reference_change");
	}
	
	public void testBasicReferenceNoChange() throws Exception {
		helper("basic_reference_no_change");
	}
	
	public void testBasicReferenceAllChange() throws Exception {
		helper("basic_reference_all_change");
	}
	
	public void testBasicReferenceAddChange() throws Exception {
		helper("basic_reference_add_change");
	}
	
	public void testBasicReferenceDeleteChange() throws Exception {
		helper("basic_reference_delete_change");
	}
	
	public void testNestedGrandchildChange() throws Exception {
		helper("nested_grandchild_change");
	}
	
	public void testNestedGreatGrandchildChange() throws Exception {
		helper("nested_greatgrandchild_change");
	}
	
	public void testNestedGreat2GrandchildChange() throws Exception {
		helper("nested_great2grandchild_change");
	}
	
	public void testNestedGreat3GrandchildChange() throws Exception {
		helper("nested_great3grandchild_change");
	}
	
	public void testObjectObjectResourceMapChange() throws Exception {
		helper("object_object_resourcemap");
	}
	
	public void testObjectObjectResourceListChange() throws Exception {
		helper("object_object_resourcelist");
	}
	
	public void testObjectObjectObjectMapChange() throws Exception {
		helper("object_object_objectmap");
	}
	
	public void testObjectObjectObjectListChange() throws Exception {
		helper("object_object_objectlist");
	}
	
	public void testObjectDocumentResourceMap() throws Exception {
		helper("object_document_resourcemap");
	}
	
	public void testObjectDocumentResourceList() throws Exception {
		helper("object_document_resourcelist");
	}
	
	public void testObjectDocumentObjectMap() throws Exception {
		helper("object_document_objectmap");
	}
	
	public void testObjectDocumentObjectList() throws Exception {
		helper("object_document_objectlist");
	}
	
	private void helper(String localId) throws Exception
	{
		// load in the first argument as an otml file
		// assume the root object is a folder, and then 
		// get the first child of the folder and
		// copy it and store the copy as the second
		// object in the folder
		
		initOtrunk();
		initOverlay();
		
		// GET THE OBJECT
		logger.fine("Getting object");
		// OTFolder root = (OTFolder)viewerHelper.getRootObject();
		// OTObject first = (OTObject)root.getChild(firstIndex);
		OTID objectId = OTIDFactory.createOTID(documentUUID + "!/" + localId);
		OTObject first = otrunk.getRootObjectService().getOTObject(objectId);
		OTUser user = otrunk.getUsers().get(0);
		first = otrunk.getUserRuntimeObject(first, user);
		
		assertTrue(first.getGlobalId() instanceof OTTransientMapID);
		
		// get the overlay version of the object
		OTObject second = overlayObjService.getOTObject(otrunk.getRuntimeAuthoredObject(first).getGlobalId());
		
		logger.fine("Copying objects:\n");
		logger.fine(first.getGlobalId().toString());
		logger.fine(second.getGlobalId().toString());
		// COPY THE OBJECT
		((OTObjectServiceImpl)first.getOTObjectService()).copyInto(first, second, -1, true, false);
		
		logger.fine("Saving overlay");
		// SAVE THE OVERLAY
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		viewerHelper.saveOTDatabase(overlayDb, output);
		output.close();

		logger.fine("Overlay saved as:\n" + new String(output.toByteArray()));
		
		logger.fine("Loading overlay");
		// LOAD THE SAVED COPY IN A NEW INSTANCE
		initOverlay(new ByteArrayInputStream(output.toByteArray()));
		
		logger.fine("Loading object from overlay");
		// second should get loaded from the overlay
		second = overlayObjService.getOTObject(otrunk.getRuntimeAuthoredObject(first).getGlobalId());
		
		logger.fine("Comparing objects");
		// CHECK if first and second are equal????
		assertTrue(OTrunkUtil.compareObjects(first, second, true));
	}
	
	private void initOtrunk()
        throws Exception
    {
		logger.fine("loading otrunk");
		System.setProperty(OTConfig.SINGLE_USER_PROP, "true");
	    viewerHelper = new OTViewerHelper();
		mainDb = viewerHelper.loadOTDatabase(authoredContent);
		learnerDb = viewerHelper.loadOTDatabase(learnerContent);
		viewerHelper.loadOTrunk(mainDb, null);
		viewerHelper.loadUserData(learnerDb, null);
		otrunk = (OTrunkImpl) viewerHelper.getOtrunk();
    }
	
	private void initOverlay() throws Exception {
		logger.fine("creating default overlay");
		overlayDb = new XMLDatabase();
		OTObjectService tempObjService = otrunk.createObjectService(overlayDb);
		OTOverlay overlay = tempObjService.createObject(OTOverlay.class);
		overlayDb.setRoot(overlay.getGlobalId());
		
		initOverlayObjService(overlay);
	}
	
	private void initOverlayObjService(OTOverlay overlay) throws Exception {
		logger.fine("setting up overlay object service");
		OverlayImpl myOverlay = new OverlayImpl(overlay);
		CompositeDatabase db = new CompositeDatabase(otrunk.getDataObjectFinder(), myOverlay);
	  	overlayObjService = otrunk.createObjectService(db);
	}
	
	private void initOverlay(InputStream stream) throws Exception {
		logger.fine("loading existing overlay");
		overlayDb = viewerHelper.loadOTDatabase(stream, null);
		OTObjectService tempObjService = otrunk.createObjectService(overlayDb);
		OTOverlay overlay = (OTOverlay) tempObjService.getOTObject(overlayDb.getRoot().getGlobalId());
		
		initOverlayObjService(overlay);
	}
}
