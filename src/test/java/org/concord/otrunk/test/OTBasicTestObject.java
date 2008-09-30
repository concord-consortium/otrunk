package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;

public interface OTBasicTestObject
    extends OTObjectInterface
{
	public static String DEFAULT_string = "";
	public void setString(String str);
	public String getString();
	
	public static float DEFAULT_float = 0;
	public void setFloat(float str);
	public float getFloat();
	
	public static int DEFAULT_int = 0;
	public void setInt(int i);
	public int getInt();	
	
	public void setReference(OTObject reference);
	public OTObject getReference();
}
