/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2005-01-27 16:45:29 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jdom.Element;

/**
 * ListTypeHandler
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class ListTypeHandler extends ResourceTypeHandler
{
	TypeService typeService;
	
	public ListTypeHandler(TypeService dots)
	{
		super("list");
		typeService = dots;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.xml.ResourceTypeHandler#handleElement(org.w3c.dom.Element, java.util.Properties)
	 */
	public Object handleElement(Element element, Properties elementProps)
	{
		XMLResourceList list = new XMLResourceList();
		
		List children = element.getChildren();
		for(Iterator childIter = children.iterator(); childIter.hasNext(); ) {			
			Element child = (Element)childIter.next();
			Object resValue = typeService.handleLiteralElement(child);
			list.add(resValue);
		}

		return list;
	}
}
