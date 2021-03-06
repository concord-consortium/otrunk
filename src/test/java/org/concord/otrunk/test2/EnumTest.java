package org.concord.otrunk.test2;

import org.concord.otrunk.test.OTEnumTestObject;
import org.concord.otrunk.test.OTListTestObject;
import org.concord.otrunk.test.RoundTrip;
import org.concord.otrunk.test.RoundTripHelperAuthoring;
import org.concord.otrunk.test.OTEnumTestObject.TestEnum;

public class EnumTest extends RoundTrip
{	
	public EnumTest()
    {
		setHelper(new RoundTripHelperAuthoring());
    }
	
	public void testBasicRoundTrip() throws Exception
	{
		initOTrunk(OTEnumTestObject.class);
		
        OTEnumTestObject root = (OTEnumTestObject) getRootObject();

        // test if the default value of an enum is null
        // not sure if that is the best way it should be but that is what I'd expect right now
        assertEquals(null, root.getEnumProp1());
        
        root.setEnumProp1(TestEnum.CONST3);

        // Test if the object preserves the enum value before it is written out
        assertEquals(TestEnum.CONST3, root.getEnumProp1());

        // Make sure the defaults are working correctly
        assertEquals(OTEnumTestObject.DEFAULT_enumProp2, root.getEnumProp2());
    
        reload();
        
		root = (OTEnumTestObject) getRootObject();
		
		assertEquals(TestEnum.CONST3, root.getEnumProp1());
	}

	public void testListRoundTrip() throws Exception
	{
		initOTrunk(OTListTestObject.class);
		
		OTListTestObject listTestObject = (OTListTestObject) getRootObject(); 
		
		listTestObject.getResourceList().add(TestEnum.CONST1);
		listTestObject.getResourceList().add(TestEnum.CONST2);
		listTestObject.getResourceList().add(TestEnum.CONST3);
		
		reload();
		
		listTestObject = (OTListTestObject) getRootObject();
		
		// This isn't implemented yet
		assertEquals(0, listTestObject.getResourceList().size());
	}
}
