/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-10-25 05:33:57 $
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
	public ListTypeHandler(TypeService dots)
	{
		super(dots);
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
			Object childObj = typeService.handleLiteralElement(child);
			list.add(childObj);
		}

		return list;
	}

}
