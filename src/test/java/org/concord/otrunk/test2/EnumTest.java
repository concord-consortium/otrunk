package org.concord.otrunk.test2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTrunk;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.OTrunkUtil;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.test.OTBasicTestObject;
import org.concord.otrunk.test.OTEnumTestObject;
import org.concord.otrunk.test.OTListTestObject;
import org.concord.otrunk.test.OTEnumTestObject.TestEnum;
import org.concord.otrunk.view.OTFolder;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

import junit.framework.TestCase;

public class EnumTest extends TestCase
{
	OTViewerHelper viewerHelper;
	XMLDatabase db;
	OTrunk otrunk;
	
	public void initOTrunk() throws Exception
	{
		viewerHelper = new OTViewerHelper();

		// create  an empty database
		db = new XMLDatabase();

        viewerHelper.loadOTrunk2(null, db);

        otrunk = viewerHelper.getOtrunk();		
	}

	public void reload() throws Exception
	{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		viewerHelper.saveOTDatabase(db, output);
		output.close();
		
		InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(output.toByteArray()));
		BufferedReader bufReader = new BufferedReader(reader);
		String line;
		while((line = bufReader.readLine()) != null){
			System.out.println(line);
		}
		
		viewerHelper = new OTViewerHelper();
		
		db = (XMLDatabase) viewerHelper.loadOTDatabase(new ByteArrayInputStream(output.toByteArray()), 
			null);
		
		viewerHelper.loadOTrunk2(null, db);		
	}
	
	public void testBasicRoundTrip() throws Exception
	{
		initOTrunk();
		
        OTEnumTestObject root = otrunk.createObject(OTEnumTestObject.class);
        otrunk.setRoot(root);	        

        // test if the default value of an enum is null
        // not sure if that is the best way it should be but that is what I'd expect right now
        assertEquals(null, root.getEnumProp1());
        
        root.setEnumProp1(TestEnum.CONST3);

        // Test if the object preserves the enum value before it is written out
        assertEquals(TestEnum.CONST3, root.getEnumProp1());

        // Make sure the defaults are working correctly
        assertEquals(OTEnumTestObject.DEFAULT_enumProp2, root.getEnumProp2());
    
        reload();
        
		root = (OTEnumTestObject) viewerHelper.getRootObject();
		
		assertEquals(TestEnum.CONST3, root.getEnumProp1());
	}

	public void testListRoundTrip() throws Exception
	{
		initOTrunk();
		
		OTListTestObject listTestObject = otrunk.createObject(OTListTestObject.class);
		otrunk.setRoot(listTestObject);
		
		listTestObject.getResourceList().add(TestEnum.CONST1);
		listTestObject.getResourceList().add(TestEnum.CONST2);
		listTestObject.getResourceList().add(TestEnum.CONST3);
		
		reload();
		
		listTestObject = (OTListTestObject) viewerHelper.getRootObject();
		
		// This isn't implemented yet
		assertEquals(0, listTestObject.getResourceList().size());
	}
}
