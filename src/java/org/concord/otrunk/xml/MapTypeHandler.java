/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
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
 * END LICENSE */

/*
 * Last modification information:
 * $Revision: 1.11 $
 * $Date: 2007-09-29 04:33:15 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;


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
	private static final Logger logger = Logger.getLogger(MapTypeHandler.class.getName());
	TypeService typeService;
	
	/**
	 * @param primitiveName
	 */
	public MapTypeHandler(TypeService dots)
	{
		super("map");
		typeService = dots;
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.xml.ResourceTypeHandler#handleElement(org.jdom.Element, java.util.Properties)
	 */
	public Object handleElement(OTXMLElement element, String relativePath,
	        XMLDataObject parent)
	{
		XMLDataMap map = new XMLDataMap(parent);
		
		OTDatabase otDB = parent.getDatabase();
		XMLDatabase xmlDB = null;
		if(otDB instanceof XMLDatabase){
			xmlDB = (XMLDatabase) otDB;
		}
		
		List<?> children = element.getChildren();
		for(Iterator<?> childIter = children.iterator(); childIter.hasNext(); ) {			
		    OTXMLElement entry = (OTXMLElement)childIter.next();
			if(!entry.getName().equals("entry")) {
				throw new RuntimeException("Invalid tag: " + entry.getName() + " inside of map element.");
			}
			
			String key = entry.getAttributeValue("key");
			List<?> entryChildren = entry.getChildren();
			Object value = null;
			if(entryChildren.size() != 1) {
				logger.warning("Warning invalid entry in map element");
				continue;
			}
			
		    String childRelativePath = null;
		    if(relativePath != null) {
		        childRelativePath = relativePath + "['" + key + "']";		        
		    }

		    OTXMLElement valueElement = (OTXMLElement)entryChildren.get(0);
		    value = typeService.handleLiteralElement(valueElement, childRelativePath);
		    if (parent != null) {
		    	if (value instanceof OTDataObject) {
					OTDataObject obj = (OTDataObject) value;
					logger.finest("Processed child: " + obj.getGlobalId() + " of parent: " + parent.getGlobalId());
					// FIXME Not sure what the property string is here...
					xmlDB.recordReference(parent, obj, relativePath);
				}
			}
		    map.put(key, value);
		}

		return map;
	}

}
