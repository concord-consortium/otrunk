package org.concord.otrunk.otcore.impl;

import org.concord.framework.otrunk.otcore.OTType;

public abstract class OTTypeImpl implements OTType
{
	protected Class instanceClass;
	
	public Class getInstanceClass()
	{
		if(instanceClass != null){
			return instanceClass;
		}
		
		instanceClass = createInstanceClass();
		return instanceClass;
	}

	protected abstract Class createInstanceClass();
}
