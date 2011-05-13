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
 * $Revision: 1.9 $
 * $Date: 2007-09-07 02:04:11 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import org.concord.otrunk.xml.XMLReferenceInfo.IntType;


/**
 * BooleanTypeHandler
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class IntegerTypeHandler extends PrimitiveResourceTypeHandler
{
	public IntegerTypeHandler()
	{
		super("int", Integer.class);
	}
	
	@Override
	protected Object handleElement(String value)
	    throws HandlerException
	{
		throw new IllegalStateException("This method isn't supported by the IntegerTypeHandler");
	}

	public Object handleElement(String value, XMLReferenceInfo resInfo)
		throws HandlerException
	{
		try {
			int ret = Integer.decode(value);
			if (resInfo != null) {
				if (value.startsWith("0x")) {
					resInfo.intType = IntType.HEX;
				} else {
					resInfo.intType = IntType.BASE10;
				}
			}
			return ret;
		} catch (Throwable e) {
			throw new HandlerException("malformed integer");
		}
	}
	
	@Override
	public Object handleElement(OTXMLElement element, String relativePath, 
		XMLDataObject parent, String propertyName)
	throws HandlerException
	{
		return handleElement(element.getTextTrim(), parent.getReferenceInfo(propertyName));
	}

	@Override
	public Object handleAttribute(String value, String name, XMLDataObject parent)
	throws HandlerException
	{
		return handleElement(value, parent.getReferenceInfo(name));
	}

}
