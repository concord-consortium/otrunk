/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2004-11-22 23:05:40 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * StringTypeHandler
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class StringTypeHandler extends ResourceTypeHandler
{
	public StringTypeHandler()
	{
		super("string");
	}
		
	/* (non-Javadoc)
	 * @see org.concord.portfolio.xml.ResourceTypeHandler#handleElement(org.w3c.dom.Element, java.util.Properties)
	 */
	public Object handleElement(Element element, Properties elementProps)
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
		
		return new XMLParsableString(contentStr);
		
		/*
		 * parse all strings because we no longer have a way to add params
		if(elementProps != null &&
				elementProps.getProperty("parse") != null) {
			return new XMLParsableString(contentStr);
		}
		
		return contentStr;
		*/
	}
}
