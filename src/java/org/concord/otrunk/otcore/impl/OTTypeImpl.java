package org.concord.otrunk.otcore.impl;

import org.concord.framework.otrunk.otcore.OTType;

public abstract class OTTypeImpl implements OTType
{
	protected Class instanceClass;
	
	public OTTypeImpl(Class instanceClass)
	{
		this.instanceClass = instanceClass;
	}
	
	public Class getInstanceClass()
	{
		return instanceClass;
	}
	
	public String getName()
	{
		return getInstanceClass().getName();
	}
}
