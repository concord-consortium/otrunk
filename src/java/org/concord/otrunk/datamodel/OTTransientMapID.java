package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;

public class OTTransientMapID
    implements OTID
{
	public static final String TRANSIENT_ID_PREFIX = "transient:";
	OTID mappedId;
	Object mapToken;
	int hashCode;
	
	public OTTransientMapID(OTID mapToken, OTID mappedId)
	{
		this.mappedId = mappedId;
		this.mapToken = mapToken;
		hashCode = mapToken.hashCode() + mappedId.hashCode();
	}
	
	protected String internalToString()
	{
        return TRANSIENT_ID_PREFIX + mapToken.toString().replaceFirst("%", "") + "!" + mappedId.toExternalForm();		
	}
	
    /**
     * This returns a unique string for this id.  This is not the actual id.<p>
     * 
     * The actual id is not returned because using the toString method on an OTID
     * cannot always return the correct thing.  The method OTObjectService.getExternalID 
     * should be used instead. 
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
    	return "%" + internalToString();
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
    	return hashCode;
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

	public String toExternalForm()
    {
		throw new RuntimeException("Transient IDs do not have a direct external form, " +
			"OTObjectService.getExternalID should used instead.\n" +
			" problem id: " + toInternalForm());
    }
	
	/*
	 * Transient IDs should never be persisted! This method is for temporary runtime use only!
	 */
	public String toInternalForm()
	{
		return internalToString().replaceFirst("%", "");
	}
}
