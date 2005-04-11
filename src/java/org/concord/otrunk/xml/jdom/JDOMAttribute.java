
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
 * Created on Mar 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk.xml.jdom;

import org.concord.otrunk.xml.OTXMLAttribute;
import org.concord.otrunk.xml.OTXMLElement;
import org.jdom.Attribute;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JDOMAttribute
    implements OTXMLAttribute
{
    Attribute attribute;
    JDOMElement parent;
    
    JDOMAttribute(JDOMElement parent, Attribute attribute)
    {
        this.attribute = attribute;
        this.parent = parent;
    }
    
    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLAttribute#getName()
     */
    public String getName()
    {
        return attribute.getName();
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLAttribute#getValue()
     */
    public String getValue()
    {
        return attribute.getValue();
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.xml.OTXMLAttribute#getParent()
     */
    public OTXMLElement getParent()
    {
        return parent;
    }

}
