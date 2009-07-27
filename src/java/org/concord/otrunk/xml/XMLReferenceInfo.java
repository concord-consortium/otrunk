package org.concord.otrunk.xml;

public class XMLReferenceInfo
{
	public final static int ATTRIBUTE = 0;
	public final static int ELEMENT = 1;
	
	/**
	 * This indicates if this resource was stored as a element or an attribute
	 */
	public int type = ELEMENT;	
	
	/**
	 * This is a comment that can be associated with this reference.  When the reference
	 * is serialized it will be saved as a comment above the reference.
	 * 
	 * If the reference is a attribute it will not be saved.
	 */
	public String comment;
}
