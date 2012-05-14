package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObject;

public interface OTMultiReferenceTestObject
    extends OTPrimitivesTestObject
{
	public void setReference(OTObject reference);
	public OTObject getReference();
	

	public void setObject(OTObject object);
	public OTObject getObject();
	

	public void setData(OTObject data);
	public OTObject getData();
	

	public void setStuff(OTObject stuff);
	public OTObject getStuff();
}
