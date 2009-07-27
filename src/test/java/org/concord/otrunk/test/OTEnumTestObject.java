package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObjectInterface;

public interface OTEnumTestObject
    extends OTObjectInterface
{
	public enum TestEnum { CONST1, CONST2, CONST3};
	
	public TestEnum getEnumProp1();
	public void setEnumProp1(TestEnum value);
	
	public static TestEnum DEFAULT_enumProp2 = TestEnum.CONST2;
	public TestEnum getEnumProp2();
	public void setEnumProp2(TestEnum value);
}
