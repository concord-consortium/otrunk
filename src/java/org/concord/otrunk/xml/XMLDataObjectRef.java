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
 * $Revision: 1.7 $
 * $Date: 2007-02-20 00:16:40 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;



/**
 * XMLDataObjectRef
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class XMLDataObjectRef extends XMLDataObject
{
	String refId = null;
	XMLDataObject parent;
	String property;
	
	
	/**
	 * @return Returns the refId.
	 */
	public String getRefId()
	{
		return refId;
	}
	/**
	 * @param refId The refId to set.
	 */
	public void setRefId(String refId)
	{
		this.refId = refId;
	}
	
	public XMLDataObjectRef(String id, OTXMLElement element, XMLDataObject parent, 
		String property)
	{
		super(element, null, null);
		
		setRefId(id);
		this.parent = parent;
		this.property = property;
	}	
}
