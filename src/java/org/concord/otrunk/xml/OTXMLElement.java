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
 * Created on Mar 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk.xml;

import java.util.List;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface OTXMLElement
{
    public String getName();
    
    /**
     * Should return an empty list if there are no children
     * @return
     */
    public List getChildren();
   
    public OTXMLElement getChild(String name);
    
    public String getAttributeValue(String attribute);
    
    /**
     * Should return an empty list if there are no attributes
     * @return
     */
    public List getAttributes();
    
    public String getTextTrim();
    
    /**
     * Return the content inside this element as a string including
     * xml tags and attributes.
     * 
     * So this should return things like:
     * Hello <bold>this is bold</bold>.  This is a paragraph element <para/>
     * 
     * 
     * @return
     */
    public String getContentAsXMLText();
    
    /**
     * Returns null if there is no parent element
     * @return
     */
    OTXMLElement getParentElement();
}
