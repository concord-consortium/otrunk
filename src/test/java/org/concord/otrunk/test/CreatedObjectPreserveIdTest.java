package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTrunk;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

public class CreatedObjectPreserveIdTest
{
	public static void main(String[] args)
    {
		// load in the first argument as an otml file
		// assume the root object is a folder, and then 
		// get the first child of the folder and
		// copy it and store the copy as the second
		// object in the folder
		OTViewerHelper viewerHelper = new OTViewerHelper();

		// create  an empty database
		XMLDatabase db = new XMLDatabase();
		
		try {
	        viewerHelper.loadOTrunk2(null, db);
	        
	        OTrunk otrunk = viewerHelper.getOtrunk();
	        OTObject root = otrunk.createObject(OTBasicTestObject.class);
	        
	        otrunk.setRoot(root);	        

	        root.getOTObjectService().preserveUUID(root);
	        
			String result = viewerHelper.saveOTDatabase(db);
			System.out.println(result);

	        // FIXME we should automatically check if the id is actually saved in the string.
			
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
    }
}
