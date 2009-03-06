package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObjectInterface;

/**
 * An OTObject with an attribute per primitive attribute type
 * these are: all of the Java primitive types, plus String
 * @author aunger
 *
 */
public interface OTPrimitivesTestObject
    extends OTObjectInterface
{
	public static String DEFAULT_string = "";
	public void setString(String str);
	public String getString();
	
	public static float DEFAULT_float = 9493.123f;
	public void setFloat(float f);
	public float getFloat();
	
	public static int DEFAULT_int = 0;
	public void setInt(int i);
	public int getInt();
	
	public static long DEFAULT_long = 0;
	public void setLong(long l);
	public long getLong();
	
	public static double DEFAULT_double = 1.2003d;
	public void setDouble(double d);
	public double getDouble();
	
	// byte, char and short aren't supported right now
//	public static byte DEFAULT_byte = 0;
//	public void setByte(byte b);
//	public byte getByte();
//	
//	public static char DEFAULT_char = 'a';
//	public void setChar(char c);
//	public char getChar();
//
//	public static short DEFAULT_short = 0;
//	public void setShort(short s);
//	public short getShort();
	
	public static boolean DEFAULT_boolean = false;
	public void setBoolean(boolean b);
	public boolean getBoolean();
}
