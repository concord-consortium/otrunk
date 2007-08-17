package org.concord.otrunk.otcore.impl;

import org.concord.framework.otrunk.otcore.OTMapType;

public class OTMapTypeImpl implements OTMapType
{

	private Class instanceClass;

	public OTMapTypeImpl(Class instanceClass)
	{
		this.instanceClass = instanceClass;
	}
	
	public Class getInstanceClass()
    {
		return instanceClass;
    }

}
