/*
 * Created on Mar 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk.xml.jdom;

import org.concord.otrunk.xml.OTXMLAttribute;
import org.concord.otrunk.xml.OTXMLElement;
import org.jdom.Attribute;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JDOMAttribute
    implements OTXMLAttribute
{
    Attribute attribute;
    JDOMElement parent;
    
    JDOMAttribute(JDOMElement parent, Attribute attribute)
    {
        this.attribute = attribute;
        this.parent = parent;
    }
    
    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLAttribute#getName()
     */
    public String getName()
    {
        return attribute.getName();
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLAttribute#getValue()
     */
    public String getValue()
    {
        return attribute.getValue();
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLAttribute#getParent()
     */
    public OTXMLElement getParent()
    {
        return parent;
    }

}
