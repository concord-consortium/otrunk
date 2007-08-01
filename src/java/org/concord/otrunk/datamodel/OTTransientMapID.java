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
	
    public String toString()
    {
        return mapToken.toString() + "!" + mappedId.toString();
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
    	return toString().hashCode();
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
