package org.concord.otrunk.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.overlay.CompositeDatabase;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.view.OTMLUserSession;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

/**
 * This is intended to be extended so test round tripping various OTrunk
 * properties
 * 
 * @author scytacki
 *
 */
public class RoundTripHelperLearner implements RoundTripHelper
{
	private OTViewerHelper viewerHelper;
	private XMLDatabase authorDb;
	private XMLDatabase learnerDb;
	private OTrunkImpl otrunk;
	private OTMLUserSession userSession;
	private OTObjectService otObjectService;
	
	public void initOTrunk(Class<? extends OTObject> otClass) throws Exception
	{
		viewerHelper = new OTViewerHelper();

		// create  an empty database
		authorDb = new XMLDatabase();

        viewerHelper.loadOTrunk2(null, authorDb);

        otrunk = (OTrunkImpl) viewerHelper.getOtrunk();
        
        OTObject root = otrunk.createObject(otClass);
        otrunk.setRoot(root);
        
        userSession = new OTMLUserSession();
        viewerHelper.loadUserSession(userSession);
        viewerHelper.newUserData("roundtrip test");
        
        learnerDb = (XMLDatabase) userSession.getUserDataDb();
        
        otObjectService = viewerHelper.getRootObject().getOTObjectService();
	}

	public void reload() throws Exception
	{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
				
		viewerHelper.saveOTDatabase(learnerDb, output);
		output.close();
		
		InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(output.toByteArray()));
		BufferedReader bufReader = new BufferedReader(reader);
		String line;
		while((line = bufReader.readLine()) != null){
			System.out.println(line);
		}
		
		viewerHelper = new OTViewerHelper();
				
		viewerHelper.loadOTrunk2(null, authorDb);		
        otrunk = (OTrunkImpl) viewerHelper.getOtrunk();
        
        learnerDb = (XMLDatabase) viewerHelper.loadOTDatabase(new ByteArrayInputStream(output.toByteArray()), 
			null);
        
        viewerHelper.loadUserData(learnerDb, null);
        
        otObjectService = viewerHelper.getRootObject().getOTObjectService();
	}	
	
	public OTObject getRootObject() throws Exception
	{
		return viewerHelper.getRootObject();
	}
	
	public <T extends OTObject> T createObject(Class<T> klass) throws Exception
	{
        return otObjectService.createObject(klass);
	}
	
	public OTUserObject getLearnerUser() {
		return userSession.getUserObject();
	}

	public CompositeDatabase getReferenceMapDb() {
		return otrunk.getCompositeDatabases().get(getLearnerUser().getGlobalId());
	}
	
	public OTrunkImpl getOTrunk()
    {
	    return otrunk;
    }
	
	public String getExportedReferenceMapDb() throws Exception {
		CompositeDatabase db = getReferenceMapDb();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		viewerHelper.saveOTDatabase(db.getActiveOverlay().getOverlayDatabase(), baos);
		return baos.toString();
	}
}
