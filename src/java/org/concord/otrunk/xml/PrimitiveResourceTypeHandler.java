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
 * Created on Jan 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk.xml;


/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class PrimitiveResourceTypeHandler extends ResourceTypeHandler 
{
	Class primitiveClass;
	
	public PrimitiveResourceTypeHandler(String primitiveName, Class primitiveClass)
	{
		super(primitiveName);
		this.primitiveClass = primitiveClass;
	}
	
	abstract public Object handleElement(String value)
		throws HandleElementException;
	
	/**
	 * You must override this method if this resource needs more than a string
	 * so objects, lists, and maps need to override this method.
	 * 
	 * @param element
	 * @return
	 */
	public Object handleElement(OTXMLElement element, String relativePath, 
	        XMLDataObject parent)
	throws HandleElementException
	{
		return handleElement(element.getTextTrim());
	}
	
	public Class getPrimitiveClass()
	{
		return primitiveClass;
	}
}
