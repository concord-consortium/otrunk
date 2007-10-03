package org.concord.otrunk.otcore.impl;

import org.concord.framework.otrunk.otcore.OTPrimitiveType;

public class OTPrimitiveTypeImpl extends OTTypeImpl 
	implements OTPrimitiveType
{
	String primitiveName;
	
	public OTPrimitiveTypeImpl(String primitiveName, Class instanceClass)
	{
		super(instanceClass);
		this.primitiveName = primitiveName;
	}
	
	public String getPrimitiveName()
	{
		return primitiveName;
	}		
}
