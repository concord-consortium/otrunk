
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

/*
 * Last modification information:
 * $Revision: 1.6 $
 * $Date: 2005-04-24 15:44:55 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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
	public Object handleElement(OTXMLElement element, Properties elementProps,
	        String relativePath)
	{
		XMLResourceList list = new XMLResourceList();
		
		List children = element.getChildren();
		int index = 0;
		for(Iterator childIter = children.iterator(); childIter.hasNext(); ) {			
		    OTXMLElement child = (OTXMLElement)childIter.next();
		    String childRelativePath = null;
		    if(relativePath != null) {
		        childRelativePath = relativePath + "[" + index + "]";		        
		    }
			Object resValue = typeService.handleLiteralElement(child, childRelativePath);
			list.add(resValue);
			index++;
		}

		return list;
	}
}
