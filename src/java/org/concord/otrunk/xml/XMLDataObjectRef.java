/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2004-11-22 23:05:40 $
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
		super(element, null);
		
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
