package org.concord.otrunk.xml;

public class XMLResourceInfo
{
	public final static int ATTRIBUTE = 0;
	public final static int ELEMENT = 1;
	
	/**
	 * This indicates if this resource was stored as a element or an attribute
	 */
	public int type = ELEMENT;
	
	public boolean containment = false;
}
