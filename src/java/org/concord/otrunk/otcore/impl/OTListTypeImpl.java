package org.concord.otrunk.otcore.impl;

import org.concord.framework.otrunk.otcore.OTListType;

public class OTListTypeImpl implements OTListType
{
	private Class instanceClass;

	public OTListTypeImpl(Class instanceClass)
	{
		this.instanceClass = instanceClass;
	}
	
	public Class getInstanceClass()
    {
		return instanceClass;
    }

}
