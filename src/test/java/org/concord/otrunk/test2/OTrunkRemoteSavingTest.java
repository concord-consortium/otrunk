package org.concord.otrunk.test2;

import java.net.URL;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTObjectService;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.net.HTTPRequestException;
import org.concord.otrunk.overlay.OTOverlay;
import org.concord.otrunk.view.OTConfig;
import org.concord.otrunk.view.OTViewer;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;
import org.concord.testing.gui.TestHelper;
import org.junit.BeforeClass;
import org.junit.Test;

public class OTrunkRemoteSavingTest {
	private static final Logger logger = Logger.getLogger(OverlayDatabasePruningTest.class.getCanonicalName());
	private static OTViewerHelper viewerHelper;
	private static OTDatabase mainDb;
	private static OTrunkImpl otrunk;
	
	private static final String overlayBaseUrl = "http://webdav.diy.concord.org/test/remote-saving/";
	
	@BeforeClass
	public static void setup() throws Exception {
		TestHelper.deleteOverlay(overlayBaseUrl);
		TestHelper.makeDirectory(overlayBaseUrl);
		initOtrunk();
		// initOverlay();
	}
	
	@Test
	public void remoteSavingNonExistent() throws Exception {
		XMLDatabase db = initOverlay();
		otrunk.remoteSaveData(db, new URL(overlayBaseUrl + "nonexistent.otml"), OTViewer.HTTP_PUT, TestHelper.defaultAuthenticator, true);
	}
	
	@Test
	public void remoteSavingExistingOK() throws Exception {
		URL remoteURL = new URL(overlayBaseUrl + "existent.otml");
		TestHelper.writeOverlay(remoteURL.toExternalForm(), "<xml>Some dummy content</xml>");
		XMLDatabase db = initOverlay();
		otrunk.remoteSaveData(db, remoteURL, OTViewer.HTTP_PUT, TestHelper.defaultAuthenticator, false);
	}
	
	@Test
	public void remoteSavingExistingNotOK() throws Exception {
		try {
			URL remoteURL = new URL(overlayBaseUrl + "existent2.otml");
			TestHelper.writeOverlay(remoteURL.toExternalForm(), "<xml>Some dummy content</xml>");
			XMLDatabase db = initOverlay();
			otrunk.remoteSaveData(db, remoteURL, OTViewer.HTTP_PUT, TestHelper.defaultAuthenticator, true);
		} catch (HTTPRequestException e) {
			// good
			return;
		}
		throw new Exception("Should have received an Exception when saving a database on top of an existing database.");
	}
	
	private static void initOtrunk() throws Exception {
		logger.fine("loading otrunk");
		System.setProperty(OTConfig.SINGLE_USER_PROP, "true");
	    viewerHelper = new OTViewerHelper();
		mainDb = new XMLDatabase();
		viewerHelper.loadOTrunk(mainDb, null);
		otrunk = (OTrunkImpl) viewerHelper.getOtrunk();
    }
	
	private static XMLDatabase initOverlay() throws Exception {
		logger.fine("creating default overlay");
		XMLDatabase overlayDb = new XMLDatabase();
		OTObjectService tempObjService = otrunk.createObjectService(overlayDb);
		OTOverlay overlay = tempObjService.createObject(OTOverlay.class);
		overlayDb.setRoot(overlay.getGlobalId());
		
		return overlayDb;
	}
}
