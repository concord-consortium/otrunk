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
 * $Revision: 1.13 $
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
	private static final Logger logger = Logger.getLogger(ListTypeHandler.class.getName());
	TypeService typeService;
	
	public ListTypeHandler(TypeService dots)
	{
		super("list");
		typeService = dots;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.xml.ResourceTypeHandler#handleElement(org.w3c.dom.Element, java.util.Properties)
	 */
	public Object handleElement(OTXMLElement element, String relativePath,
	        XMLDataObject parent)
	{
		XMLDataList list = new XMLDataList(parent);
		
		List<?> content = element.getContent();
		String previousComment = null;

		OTDatabase otDB = parent.getDatabase();
		XMLDatabase xmlDB = null;
		if(otDB instanceof XMLDatabase){
			xmlDB = (XMLDatabase) otDB;
		}
		
		int index = 0;
		for(Iterator<?> childIter = content.iterator(); childIter.hasNext(); ) {			
		    OTXMLContent childContent = (OTXMLContent)childIter.next();
		    if(childContent instanceof OTXMLComment){
		    	previousComment = ((OTXMLComment) childContent).getText();
		    }
		    
		    if(!(childContent instanceof OTXMLElement)){
		    	continue;
		    }
		    
		    OTXMLElement child = (OTXMLElement) childContent;

			if(previousComment != null && xmlDB.isTrackResourceInfo()){
				XMLReferenceInfo info = list.getReferenceInfo(index);
				if(info == null){
					info = new XMLReferenceInfo();
					list.setResourceInfo(index, info);
				}
				
				info.comment = previousComment;
			}

			String childRelativePath = null;
		    if(relativePath != null) {
		        childRelativePath = relativePath + "[" + index + "]";		        
		    }
			Object resValue = typeService.handleLiteralElement(child, childRelativePath);
			if (parent != null) {
    			if (resValue instanceof OTDataObject) {
    				OTDataObject obj = (OTDataObject) resValue;
					logger.finest("Processed child: " + obj.getGlobalId() + " of parent: " + parent.getGlobalId());
					// FIXME Not sure what the property string is here...
					xmlDB.recordReference(parent, obj, relativePath);
				}
			}
			list.add(resValue);
			index++;
			
			previousComment = null;
		}

		return list;
	}
}
