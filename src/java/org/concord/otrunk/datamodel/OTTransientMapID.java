package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;

public class OTTransientMapID
    implements OTID
{
	OTID mappedId;
	Object mapToken;
	
	public OTTransientMapID(Object mapToken, OTID mappedId)
	{
		this.mappedId = mappedId;
		this.mapToken = mapToken;
	}
	
	protected String internalToString()
	{
        return mapToken.toString() + "!" + mappedId.toString();		
	}
	
	/**
	 * This method is used by the hashCode implementation of OTObjects, so it is called a lot.
	 * It would be good to warn people about using this, but then there needs to be a way to ignore
	 * the hashCode calls.
	 */
    public String toString()
    {
    	String idStr = internalToString();
        return idStr;
    }

    public OTID getMappedId()
    {
    	return mappedId;
    }
    
    public Object getMapToken()
    {
    	return mapToken;
    }
    
    public int hashCode()
    {
    	return internalToString().hashCode();
    }
    
    public boolean equals(Object obj)
    {
    	if(!(obj instanceof OTTransientMapID)){
    		return false;
    	}

    	OTTransientMapID other = (OTTransientMapID) obj;
    	
    	if(mapToken != other.getMapToken()){
    		return false;
    	}
    	
    	return mappedId.equals(other.getMappedId());
    }
}
