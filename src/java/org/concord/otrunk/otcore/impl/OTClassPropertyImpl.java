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
	private boolean onlyInOverlayProperty;
	private boolean overridenProperty;
	
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

	public void setDefault(Object defaultValue)
	{
		this.defaultValue = defaultValue;
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
	
	public boolean isOnlyInOverlayProperty()
	{
		return onlyInOverlayProperty;
	}
	
	public boolean isOverriddenProperty()
	{
		return overridenProperty;
	}
	
	public OTClassProperty getOnlyInOverlayProperty()
	{
		OTClassPropertyImpl copy = copy();
		copy.onlyInOverlayProperty = true;
		return copy;
	}
	
	public OTClassProperty getOverriddenProperty()
	{
		OTClassPropertyImpl copy = copy();
		copy.overridenProperty = true;
		return copy;		
	}

	private OTClassPropertyImpl copy()
    {
		return new OTClassPropertyImpl(name, type, defaultValue);
    }

}
