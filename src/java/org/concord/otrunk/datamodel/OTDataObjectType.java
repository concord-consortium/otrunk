package org.concord.otrunk.datamodel;

public class OTDataObjectType
{
	protected String className;

	public OTDataObjectType(String className)
	{
		this.className = className;
	}
	
	public String getClassName()
    {
    	return className;
    }
    
    public String toString()
    {
		return super.toString() + " className=" + className;
    }
}
