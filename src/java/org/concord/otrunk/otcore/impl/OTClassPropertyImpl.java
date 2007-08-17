package org.concord.otrunk.otcore.impl;

import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.concord.framework.otrunk.otcore.OTListType;
import org.concord.framework.otrunk.otcore.OTMapType;
import org.concord.framework.otrunk.otcore.OTPrimitiveType;
import org.concord.framework.otrunk.otcore.OTType;

public class OTClassPropertyImpl
    implements OTClassProperty
{
	Object defaultValue;
	OTType type;
	String name;
	
	public OTClassPropertyImpl(String name, OTType type, Object defaultValue)
	{
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	public Object getDefault()	
	{
		return defaultValue;
	}

	public OTType getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public boolean isList()
    {
		return type instanceof OTListType;
    }

	public boolean isMap()
    {
		return type instanceof OTMapType;
    }

	public boolean isPrimitive()
    {
		return type instanceof OTPrimitiveType;
    }
}
