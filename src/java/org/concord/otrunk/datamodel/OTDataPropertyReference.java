package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;

/**
 * A class to encapsulate one object referring to another object.
 * @author aunger
 *
 */
public class OTDataPropertyReference
{
	private OTID source;
	private OTID dest;
	private String property;
	
	public OTDataPropertyReference(OTID source, OTID destination, String property) {
		this.setSource(source);
		this.setDest(destination);
		this.setProperty(property);
	}
	
	public boolean equals(Object obj) {
		if (! (obj instanceof OTDataPropertyReference)) {
			return false;
		}
		OTDataPropertyReference ref = (OTDataPropertyReference) obj;
		
		if (this.source.equals(ref.getSource())) {
			if (this.dest.equals(ref.getDest())) {
				if (this.property.equals(ref.getProperty())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
     * @param source the source to set
     */
    public void setSource(OTID source)
    {
	    this.source = source;
    }

	/**
     * @return the source
     */
    public OTID getSource()
    {
	    return source;
    }

	/**
     * @param dest the dest to set
     */
    public void setDest(OTID dest)
    {
	    this.dest = dest;
    }

	/**
     * @return the dest
     */
    public OTID getDest()
    {
	    return dest;
    }

	/**
     * @param property the property to set
     */
    public void setProperty(String property)
    {
	    this.property = property;
    }

	/**
     * @return the property
     */
    public String getProperty()
    {
	    return property;
    }

}
