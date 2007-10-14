/**
 * 
 */
package org.concord.otrunk.test;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;

import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.view.OTFolderObject;
import org.concord.otrunk.view.OTViewerHelper;

/**
 * @author scott
 *
 */
public class RemoveDeclarationTest {

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
		
		File input = new File("src/test/resources/remove-declaration-test.otml");
		OTDatabase mainDb = viewerHelper.loadOTDatabase(input.toURL());

		viewerHelper.loadOTrunk(mainDb, null);
		
		OTFolderObject root = (OTFolderObject)viewerHelper.getRootObject();
		root.getChildren().remove(0);
				
		FileOutputStream output = 
			new FileOutputStream("src/test/resources/output/remove-declartion-test-output.otml");
		viewerHelper.saveOTDatabase(mainDb, output);
		output.close();
		
		System.exit(0);
	}

}
