/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2004-12-15 22:52:15 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Properties;

import org.jdom.Element;

/**
 * BooleanTypeHandler
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class BooleanTypeHandler extends ResourceTypeHandler
{
	public BooleanTypeHandler()
	{
		super("boolean");
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.xml.ResourceTypeHandler#handleElement(org.w3c.dom.Element, java.util.Properties)
	 */
	public Object handleElement(Element element, Properties elementProps)
	{
		String value = element.getTextTrim();
		try {
			return Boolean.valueOf(value);
		} catch (Throwable e) {
			
			throw new RuntimeException("syntax error in: " + 
					TypeService.elementPath(element), e);
		}

	}

}
