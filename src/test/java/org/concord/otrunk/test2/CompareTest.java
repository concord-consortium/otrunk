package org.concord.otrunk.test2;

import junit.framework.TestCase;

import org.concord.framework.otrunk.OTObject;
import org.concord.otrunk.OTrunkUtil;
import org.concord.otrunk.test.OtmlTestHelper;

public class CompareTest extends TestCase
{
	public void testReferenceLoop() throws Exception
	{
		OtmlTestHelper helper = new OtmlTestHelper();
		helper.initOtrunk(getClass().getResource("/compare-self-referencing-test.otml"));
		OTObject first = helper.getObject("first");
		OTObject second = helper.getObject("second");
		assertTrue(OTrunkUtil.compareObjects(first, second));
		
		OTObject third = helper.getObject("third");
		assertTrue(!OTrunkUtil.compareObjects(first, third));
	}
}
