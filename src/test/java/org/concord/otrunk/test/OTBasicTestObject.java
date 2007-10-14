package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObjectInterface;

public interface OTBasicTestObject
    extends OTObjectInterface
{
	public static String DEFAULT_string = "";
	public void setString(String str);
	public String getString();
	
	public static float DEFAULT_value = 0;
	public void setValue(float str);
	public float getValue();
}
