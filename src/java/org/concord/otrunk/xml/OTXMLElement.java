/*
 * Created on Mar 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk.xml;

import java.util.List;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface OTXMLElement
{
    public String getName();
    
    /**
     * Should return an empty list if there are no children
     * @return
     */
    public List getChildren();
   
    public OTXMLElement getChild(String name);
    
    public String getAttributeValue(String attribute);
    
    /**
     * Should return an empty list if there are no attributes
     * @return
     */
    public List getAttributes();
    
    public String getTextTrim();
    
    /**
     * Return the content inside this element as a string including
     * xml tags and attributes.
     * 
     * So this should return things like:
     * Hello <bold>this is bold</bold>.  This is a paragraph element <para/>
     * 
     * 
     * @return
     */
    public String getContentAsXMLText();
    
    /**
     * Returns null if there is no parent element
     * @return
     */
    OTXMLElement getParentElement();
}
