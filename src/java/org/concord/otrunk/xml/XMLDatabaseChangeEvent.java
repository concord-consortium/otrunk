package org.concord.otrunk.xml;

import java.util.EventObject;

public class XMLDatabaseChangeEvent extends EventObject
{

	public static String STATE_CLEAN = "clean";
	
	public static String STATE_DIRTY = "dirty";

	private String state;
	
	public XMLDatabaseChangeEvent(XMLDatabase source)
    {
	    super(source);
    }
	
	public String getValue()
    {
    	return state;
    }
    
    public void setValue(String value)
    {
    	this.state = value;
    }

}
