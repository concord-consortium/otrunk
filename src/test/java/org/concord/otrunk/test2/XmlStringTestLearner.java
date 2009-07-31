package org.concord.otrunk.test2;

import org.concord.otrunk.test.RoundTripHelperLearner;

public class XmlStringTestLearner extends XmlStringTest
{
	public XmlStringTestLearner()
    {
		setHelper(new RoundTripHelperLearner());
    }
}
