package org.concord.otrunk.test;

import java.net.MalformedURLException;

import org.concord.framework.otrunk.OTObjectService;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

public class GetterSetterPerformanceTest
{
	/**
	 * @param args
	 * @throws Exception 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) 
		throws MalformedURLException, Exception 
	{
		// load in the first argument as an otml file
		// assume the root object is a folder, and then 
		// get the first child of the folder and
		// copy it and store the copy as the second
		// object in the folder
		OTViewerHelper viewerHelper = new OTViewerHelper();

		OTDatabase mainDb = new XMLDatabase();

		viewerHelper.loadOTrunk(mainDb, null);
		
		OTrunkImpl otrunk = (OTrunkImpl)viewerHelper.getOtrunk();
		OTObjectService objService = otrunk.getRootObjectService();

		OTBasicTestObject obj = 
			(OTBasicTestObject) objService.createObject(OTBasicTestObject.class);

		long startTime = System.currentTimeMillis();
		int count = 1000000;
		float float1;
		for(int i=0; i<count; i++){
			float1 = obj.getFloat();			
		}
		
		long endTime = System.currentTimeMillis();
		System.err.println("time for millon gets: " + (endTime - startTime) + "ms");

		startTime = System.currentTimeMillis();
		count = 1000000;
		float1 = 20f;
		for(int i=0; i<count; i++){
			obj.setFloat((float)i);			
		}
		
		endTime = System.currentTimeMillis();

		System.err.println("time for million sets: " + (endTime - startTime) + "ms");
		
		System.exit(0);
	}


}
