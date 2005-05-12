
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
 * $Revision: 1.9 $
 * $Date: 2005-05-12 15:27:19 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.List;
import java.util.Properties;

/**
 * StringTypeHandler
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class StringTypeHandler extends PrimitiveResourceTypeHandler
{
    boolean xmlString;
    
	public StringTypeHandler()
	{
		super("string");
	}
		
	public Object handleElement(String value, Properties elementProps)
	{
		return value;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.xml.ResourceTypeHandler#handleElement(org.w3c.dom.Element, java.util.Properties)
	 */
	public Object handleElement(OTXMLElement element, Properties elementProps,
	        String relativePath, XMLDataObject parent)
	{
	    // This is for backwards compatibility
	    // if a string element has sub elements then it is treated 
	    // as xml text.
	    // if there is a string that shouldn't have xml tags in it
	    // then a invalid resource value message will be printed.
	    List children = element.getChildren();
	    if(children != null && children.size() > 0) {
		    String contentStr = element.getContentAsXMLText();
		    return new XMLParsableString(contentStr);			    
	    }
	    
	    return element.getTextTrim();
	}
}
