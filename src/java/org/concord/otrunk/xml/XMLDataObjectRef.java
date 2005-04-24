
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
 * $Revision: 1.5 $
 * $Date: 2005-04-24 15:44:55 $
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
	
	public XMLDataObjectRef(String id, OTXMLElement element)
	{
		super(element, null, null);
		
		setRefId(id);		
	}
	/**
	 * @param obj
	 * @param name
	 */
	public void setReferenceSource(XMLDataObject obj, String name)
	{
		// TODO Auto-generated method stub		
	}
	
	public void setReferenceSource(XMLResourceList list, int index)
	{
		// TODO
	}
	
	public void setReferenceSource(XMLResourceMap map, String key)
	{
		// TODO
	}
}
