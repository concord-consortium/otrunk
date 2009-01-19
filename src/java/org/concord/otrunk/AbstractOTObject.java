package org.concord.otrunk;

public abstract class AbstractOTObject extends OTObjectInternal
{
	@Override
	public boolean equals(Object obj)
	{
		return internalEquals(obj);
	}
	
	@Override
	public String toString()
	{
		return internalToString();
	}
	
	@Override
	public int hashCode()
	{
		return internalHashCode();
	}
}
