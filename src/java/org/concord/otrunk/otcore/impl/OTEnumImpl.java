package org.concord.otrunk.otcore.impl;

import org.concord.framework.otrunk.otcore.OTEnum;
import org.concord.framework.util.EnumHelper;

public class OTEnumImpl extends OTTypeImpl
    implements OTEnum
{

	public OTEnumImpl(Class<?> instanceClass)
    {
	    super(instanceClass);
	    if(!instanceClass.isEnum()){
	    	throw new IllegalStateException("The instance class of an OTEnumImpl must be an Enum");
	    }
    }
	
	public Object getValue(int valueOrdinal)
	{
		Class<Enum> instanceClass2 = (Class<Enum>) getInstanceClass();
		
		return EnumHelper.getValueByOrdinal(instanceClass2, valueOrdinal);		
	}
	
	public Object getValue(String valueName)
    {
		Class<Enum> instanceClass2 = (Class<Enum>) getInstanceClass();
	
		return EnumHelper.getValueByName(instanceClass2, valueName);
    }
	
	
}
