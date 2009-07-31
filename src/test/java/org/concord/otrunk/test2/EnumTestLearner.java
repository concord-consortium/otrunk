package org.concord.otrunk.test2;

import org.concord.otrunk.test.RoundTripHelperLearner;

public class EnumTestLearner extends EnumTest
{
	public EnumTestLearner()
    {
		setHelper(new RoundTripHelperLearner());
    }
}
