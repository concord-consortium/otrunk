/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2005-03-14 05:05:43 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Properties;

/**
 * StringTypeHandler
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class StringTypeHandler extends PrimitiveResourceTypeHandler
{
	public StringTypeHandler()
	{
		super("string");
	}
		
	public Object handleElement(String value, Properties elementProps)
	{
		return value;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.xml.ResourceTypeHandler#handleElement(org.w3c.dom.Element, java.util.Properties)
	 */
	public Object handleElement(OTXMLElement element, Properties elementProps)
	{
	    String contentStr = element.getContentAsXMLText();
	    		
		return new XMLParsableString(contentStr);		
	}
}
