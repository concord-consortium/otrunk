/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-10-25 05:33:57 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import org.jdom.Element;


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
	
	public XMLDataObjectRef(String id, Element element)
	{
		super(element);
		
		setRefId(id);		
	}
}
