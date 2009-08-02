package org.concord.otrunk.test2;

import org.concord.otrunk.test.OTBasicTestObject;
import org.concord.otrunk.test.OTListTestObject;
import org.concord.otrunk.test.RoundTrip;
import org.concord.otrunk.test.RoundTripHelperAuthoring;

public class ObjectListTest extends RoundTrip
{
	public ObjectListTest()
    {
		setHelper(new RoundTripHelperAuthoring());
    }
	
	/**
	 * This verifies that the first object can be removed from the list
	 * and can be saved and loaded after doing so.
	 * 
	 * @throws Exception
	 */
	public void testRemoveFirstObject() throws Exception
	{
		initOTrunk(OTListTestObject.class);
		
		OTListTestObject root = (OTListTestObject) getRootObject();

		root.getObjectList().add(createObject(OTBasicTestObject.class));
		root.getObjectList().add(createObject(OTBasicTestObject.class));
		root.getObjectList().add(createObject(OTBasicTestObject.class));
		
		reload();
		
		root = (OTListTestObject) getRootObject();
		root.getObjectList().remove(0);
		
		reload();
		
		root = (OTListTestObject) getRootObject();
		assertEquals(2, root.getObjectList().size());
	}
}
