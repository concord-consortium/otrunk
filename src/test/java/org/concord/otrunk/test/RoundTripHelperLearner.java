package org.concord.otrunk.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTrunk;
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
	private OTrunk otrunk;
	private OTMLUserSession userSession;
	
	public void initOTrunk(Class<? extends OTObject> otClass) throws Exception
	{
		viewerHelper = new OTViewerHelper();

		// create  an empty database
		authorDb = new XMLDatabase();

        viewerHelper.loadOTrunk2(null, authorDb);

        otrunk = viewerHelper.getOtrunk();
        
        OTObject root = otrunk.createObject(otClass);
        otrunk.setRoot(root);
        
        userSession = new OTMLUserSession();
        viewerHelper.loadUserSession(userSession);
        viewerHelper.newUserData("roundtrip test");
        
        learnerDb = (XMLDatabase) userSession.getUserDataDb();
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
        otrunk = viewerHelper.getOtrunk();
        
        learnerDb = (XMLDatabase) viewerHelper.loadOTDatabase(new ByteArrayInputStream(output.toByteArray()), 
			null);
        
        viewerHelper.loadUserData(learnerDb, null);
	}	
	
	public OTObject getRootObject() throws Exception
	{
		return viewerHelper.getRootObject();
	}
}
