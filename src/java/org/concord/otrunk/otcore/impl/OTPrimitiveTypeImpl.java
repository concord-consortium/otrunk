package org.concord.otrunk.otcore.impl;

import org.concord.framework.otrunk.otcore.OTPrimitiveType;

public class OTPrimitiveTypeImpl implements OTPrimitiveType
{
	String primitiveName;
	Class instanceClass;
	
	public OTPrimitiveTypeImpl(String primitiveName, Class instanceClass)
	{
		this.primitiveName = primitiveName;
		this.instanceClass = instanceClass;
	}
	
	public String getPrimitiveName()
	{
		return primitiveName;
	}
	
	public Class getInstanceClass()
    {
		return instanceClass;
    }

}
