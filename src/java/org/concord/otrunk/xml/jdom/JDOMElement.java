/*
 * Created on Mar 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk.xml.jdom;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.concord.otrunk.xml.OTXMLElement;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JDOMElement
    implements OTXMLElement
{
    Element element;
    
    JDOMElement(Element element)
    {
        this.element = element;
    }
    
    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLElement#getName()
     */
    public String getName()
    {
        return element.getName();
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLElement#getChildren()
     */
    public List getChildren()
    {
        Vector wrappedChildren = new Vector();
        List children = element.getChildren();
		for(Iterator childIter = children.iterator(); childIter.hasNext(); ) {			
			Element child = (Element)childIter.next();
			JDOMElement wrappedChild = new JDOMElement(child);
			wrappedChildren.add(wrappedChild);
		}
       
        return wrappedChildren; 
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLElement#getChild(java.lang.String)
     */
    public OTXMLElement getChild(String name)
    {
        Element child = element.getChild(name);
        
        return new JDOMElement(child);
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLElement#getAttributeValue(java.lang.String)
     */
    public String getAttributeValue(String attribute)
    {
        return element.getAttributeValue(attribute);
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLElement#getAttributes()
     */
    public List getAttributes()
    {
        Vector wrappedAttributes = new Vector();
        List attributes = element.getAttributes();
		for(Iterator attribIter = attributes.iterator(); attribIter.hasNext(); ) {			
			Attribute attrib = (Attribute)attribIter.next();
			JDOMAttribute wrappedAttrib = new JDOMAttribute(this, attrib);
			wrappedAttributes.add(wrappedAttrib);
		}
       
        return wrappedAttributes; 
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLElement#getTextTrim()
     */
    public String getTextTrim()
    {
        return element.getTextTrim();
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLElement#getContentAsXMLText()
     */
    public String getContentAsXMLText()
    {
		// get string from inside of this element and return it
		XMLOutputter outputer = new XMLOutputter();
		// need a buffer output stream to make a string out of the content
		StringWriter stringWriter = new StringWriter();
		try {
			outputer.outputElementContent(element, stringWriter);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		String contentStr = stringWriter.toString().trim();
		
		return contentStr;
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLElement#getParentElement()
     */
    public OTXMLElement getParentElement()
    {
        Element parent = element.getParentElement();
        
        return new JDOMElement(parent);
    }

    public boolean equals(Object other)
    {
        if(!(other instanceof JDOMElement)) return false;
        
        return ((JDOMElement)other).element.equals(element);
    }
    
}
