package org.concord.otrunk.test2;

import java.net.URL;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.wrapper.OTInt;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.view.OTConfig;
import org.concord.otrunk.view.OTViewerHelper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AnnotationsTest
{
	private static final Logger logger = Logger.getLogger(AnnotationsTest.class.getCanonicalName());
	private static final boolean loadLearnerData = false;
	private static URL authoredContent = OverlayObjectCopyTest.class.getResource("/reference-test-authored.otml");
	private static URL learnerContent = OverlayObjectCopyTest.class.getResource("/reference-test-learner.otml");
	private static OTViewerHelper viewerHelper;
	private static OTDatabase mainDb;
	private static OTDatabase learnerDb;
	private static OTrunkImpl otrunk;
	private static OTObject rootObject;
	
	@Test
	public void annotationsIsAnAttribute() throws Exception {
		OTObjectMap annotations = rootObject.getAnnotations();
		Assert.assertTrue("Annotations is an OTObjectMap", annotations instanceof OTObjectMap);
	}
	
	@Test
	public void canSetAnAnnotation() throws Exception {
		OTObjectMap annotations = rootObject.getAnnotations();
		OTInt intObj = otrunk.createObject(OTInt.class);
		int intVal = 5;
		intObj.setValue(intVal);
		annotations.putObject("test", intObj);
		OTObject obj = annotations.getObject("test");
		Assert.assertTrue("Retrieved object should be an OTInt", obj instanceof OTInt);
		Assert.assertTrue("OTInt value should be " + intVal, ((OTInt)obj).getValue() == intVal);
	}
	
	@BeforeClass
	public static void initOtrunk()
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
	rootObject = otrunk.getRoot();
}

}
