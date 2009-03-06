package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObject;

public interface OTBasicTestObject
    extends OTPrimitivesTestObject
{
	public void setReference(OTObject reference);
	public OTObject getReference();
}
