package org.concord.otrunk.test2;


import java.net.URL;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.test.OTListTestObject;
import org.concord.otrunk.view.OTViewerHelper;


public class CollectionCastingTest extends TestCase
{
	public OTObjectList initObjectList() throws Exception
	{
        URL url = getClass().getResource("/collection-test.otml");
        OTViewerHelper viewerHelper = new OTViewerHelper();
        OTDatabase mainDb = viewerHelper.loadOTDatabase(url);
        viewerHelper.loadOTrunk(mainDb, null);

        OTListTestObject list = (OTListTestObject) viewerHelper.getRootObject();
        return list.getObjectList();
        
	}
	
    /**
     * Test creating an ArrayList from an OTObjectList.
     * Used to throw an IllegalArgumentException from the constructor of ArrayList.
     */
    public void testArrayListFromOtObjectList() throws Exception {
        OTObjectList objectList = initObjectList();

        new ArrayList<OTObject>(objectList);
    }

    public void testToValidArray() throws Exception {
        OTObjectList objectList = initObjectList();

        objectList.toArray(new Object[0]);
    }

    
    public void testToInvalidArray() throws Exception {
        OTObjectList objectList = initObjectList();

        try{
        	objectList.toArray(new Integer[0]);
        } catch (ArrayStoreException e){
        	return;
        }
        
        assertTrue("storing a OTObjectList in a invalid array did not throw an exception", false);
    }
    
}
