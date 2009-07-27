package org.concord.otrunk.otcore.impl;

import org.concord.framework.otrunk.otcore.OTEnum;

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

	public boolean validInt(int testInt)
    {
		Class<Enum> instanceClass2 = (Class<Enum>) getInstanceClass();
	
		for(Enum constant: instanceClass2.getEnumConstants()){
			if(constant.ordinal() == testInt){
				return true;
			}
		}
		
		return false;
    }

	public boolean validName(int testName)
    {
		Class<Enum> instanceClass2 = (Class<Enum>) getInstanceClass();
	
		for(Enum constant: instanceClass2.getEnumConstants()){
			if(constant.name().equals(testName)){
				return true;
			}
		}
		
		return false;
    }
	
	public Object getValue(int valueOrdinal)
	{
		Class<Enum> instanceClass2 = (Class<Enum>) getInstanceClass();
		
		for(Enum constant: instanceClass2.getEnumConstants()){
			if(constant.ordinal() == valueOrdinal){
				return constant;
			}
		}
		
		return null;		
	}
	
	public Object getValue(String valueName)
    {
		Class<Enum> instanceClass2 = (Class<Enum>) getInstanceClass();
	
		for(Enum constant: instanceClass2.getEnumConstants()){
			if(constant.name().equals(valueName)){
				return constant;
			}
		}
		
		return null;
    }
	
	
}
