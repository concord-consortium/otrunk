/*
 * Last modification information:
 * $Revision: 1.5 $
 * $Date: 2005-03-14 05:05:43 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Properties;


/**
 * BooleanTypeHandler
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class BooleanTypeHandler extends PrimitiveResourceTypeHandler
{
	public BooleanTypeHandler()
	{
		super("boolean");
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.xml.ResourceTypeHandler#handleElement(org.w3c.dom.Element, java.util.Properties)
	 */
	public Object handleElement(String value, Properties elementProps)
		throws HandleElementException
	{
		try {
			return Boolean.valueOf(value);
		} catch (Throwable e) {			
			throw new HandleElementException("malformed boolean");
		}

	}

}
