/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-04-26 15:41:41 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Properties;

import org.concord.otrunk.OTXMLString;

/**
 * XMLStringTypeHandler
 * Class name and description
 *
 * Date created: Apr 25, 2005
 *
 * @author scott<p>
 *
 */
public class XMLStringTypeHandler extends PrimitiveResourceTypeHandler
{
    public XMLStringTypeHandler()
    {
        super("xmlstring");
    }
    
	public Object handleElement(String value, Properties elementProps)
	{
		return new OTXMLString(value);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.xml.ResourceTypeHandler#handleElement(org.w3c.dom.Element, java.util.Properties)
	 */
	public Object handleElement(OTXMLElement element, Properties elementProps,
	        String relativePath)
	{	    
	    String contentStr = element.getContentAsXMLText();
	    return new XMLParsableString(contentStr);			    
	}
}
