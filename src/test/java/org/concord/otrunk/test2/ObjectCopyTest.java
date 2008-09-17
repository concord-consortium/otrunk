/**
 * 
 */
package org.concord.otrunk.test2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.OTrunkUtil;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.view.OTFolder;
import org.concord.otrunk.view.OTViewerHelper;

/**
 * Example <br>
 * [description]
 * <p>
 * Date created: Sep 17, 2008
 * 
 * @author scytacki<p>
 *
 */

public class ObjectCopyTest extends TestCase
{

	public void testDocumentCopy() throws Exception
	{
		helper(getClass().getResource("/copy-test.otml"), 0);
	}
	
	public void testFolderCopy() throws Exception
	{
		helper(getClass().getResource("/copy-test.otml"), 1);
	}

	public void helper(URL input, int firstIndex) throws Exception
	{
		// load in the first argument as an otml file
		// assume the root object is a folder, and then 
		// get the first child of the folder and
		// copy it and store the copy as the second
		// object in the folder
		OTViewerHelper viewerHelper = new OTViewerHelper();
		
		OTDatabase mainDb = viewerHelper.loadOTDatabase(input);

		viewerHelper.loadOTrunk(mainDb, null);
		
		OTFolder root = (OTFolder)viewerHelper.getRootObject();
		OTObject first = (OTObject)root.getChild(firstIndex);
		
		OTrunkImpl otrunk = (OTrunkImpl)viewerHelper.getOtrunk();
		OTObjectService objService = otrunk.getRootObjectService();
		
		OTObject second = objService.copyObject(first, -1);
		
		root.addChild(second);
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		viewerHelper.saveOTDatabase(mainDb, output);
		output.close();

		
		viewerHelper = new OTViewerHelper();
		
		mainDb = viewerHelper.loadOTDatabase(new ByteArrayInputStream(output.toByteArray()), 
			null);
		
		viewerHelper.loadOTrunk(mainDb, null);

		root = (OTFolder)viewerHelper.getRootObject();
		first = (OTObject)root.getChild(firstIndex);
		second = (OTObject)root.getChild(root.getChildCount() - 1);
		
		// CHECK if first and second are equal????
		assertTrue(OTrunkUtil.compareObjects(first, second));
	}
}
