/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2005-01-31 17:43:20 $
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
public class IntegerTypeHandler extends PrimitiveResourceTypeHandler
{
	public IntegerTypeHandler()
	{
		super("int");
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.xml.ResourceTypeHandler#handleElement(org.w3c.dom.Element, java.util.Properties)
	 */
	public Object handleElement(String value, Properties elementProps)
		throws HandleElementException
	{
		try {
			return Integer.decode(value);
		} catch (Throwable e) {
			throw new HandleElementException("malformed integer");
		}
	}

}
