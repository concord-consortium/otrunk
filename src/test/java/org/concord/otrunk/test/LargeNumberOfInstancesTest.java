package org.concord.otrunk.test;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.concord.framework.otrunk.OTObjectService;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

public class LargeNumberOfInstancesTest
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

		ArrayList<OTBasicTestObject> objs = new ArrayList<OTBasicTestObject>();		
		for(int i=0; i<1000000; i++){
			try{
				OTBasicTestObject obj = 
					(OTBasicTestObject) objService.createObject(OTBasicTestObject.class);
				objs.add(obj);
			} catch (Throwable t){
				objs = null;
				System.err.println("got error creating objects on obj: " + i);
				t.printStackTrace();
				break;
			}			
		}
				
		System.exit(0);
	}


}
