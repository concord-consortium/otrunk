/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-01-25 16:19:41 $
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
 * MapTypeHandler
 * Class name and description
 *
 * Date created: Jan 21, 2005
 *
 * @author scott<p>
 *
 */
public class MapTypeHandler extends ResourceTypeHandler
{
	TypeService typeService;
	
	/**
	 * @param primitiveName
	 */
	public MapTypeHandler(TypeService dots)
	{
		super("list");
		typeService = dots;
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.xml.ResourceTypeHandler#handleElement(org.jdom.Element, java.util.Properties)
	 */
	public Object handleElement(Element element, Properties elementProps)
	{
		XMLResourceMap map = new XMLResourceMap();
		
		List children = element.getChildren();
		for(Iterator childIter = children.iterator(); childIter.hasNext(); ) {			
			Element entry = (Element)childIter.next();
			if(!entry.getName().equals("entry")) {
				throw new RuntimeException("Invalid tag inside of map element");
			}
			
			String key = entry.getAttributeValue("key");
			List entryChildren = entry.getChildren();
			Object value = null;
			if(entryChildren.size() != 1) {
				System.err.println("Warning empty entry in map element");
			} else {
				Element valueElement = (Element)entryChildren.get(0);
				value = typeService.handleLiteralElement(valueElement);
			}

			map.put(key, value);
		}

		return map;
	}

}
