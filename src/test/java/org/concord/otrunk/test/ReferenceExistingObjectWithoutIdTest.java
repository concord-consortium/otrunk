/**
 * 
 */
package org.concord.otrunk.test;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;

import org.concord.framework.otrunk.OTObjectList;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.view.OTFolderObject;
import org.concord.otrunk.view.OTViewerHelper;

/**
 * @author scott
 *
 */
public class ReferenceExistingObjectWithoutIdTest {

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
		
		File input = new File("src/test/resources/reference-existing-object-without-id-test.otml");
		OTDatabase mainDb = viewerHelper.loadOTDatabase(input.toURL());

		viewerHelper.loadOTrunk(mainDb, null);
		
		OTFolderObject root = (OTFolderObject)viewerHelper.getRootObject();
		OTObjectList children = root.getChildren();
		for(int i=0; i<children.size(); i++){
			OTFolderObject child = (OTFolderObject) children.get(i);
			createReference(child);
		}
						
		FileOutputStream output = 
			new FileOutputStream("src/test/resources/output/reference-existing-object-without-id-test-output.otml");
		viewerHelper.saveOTDatabase(mainDb, output);
		output.close();
		
		System.exit(0);
	}

	public static void createReference(OTFolderObject parent)
	{
		OTFolderObject firstChild = (OTFolderObject) parent.getChildren().get(0);
		OTFolderObject secondChild = (OTFolderObject) parent.getChildren().get(1);
		secondChild.getChildren().add(firstChild);		
	}
	
}
