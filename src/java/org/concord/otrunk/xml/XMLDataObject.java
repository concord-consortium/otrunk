/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2004-12-06 03:51:35 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Collection;
import java.util.Hashtable;

import org.concord.framework.otrunk.OTID;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTIDFactory;
import org.concord.otrunk.datamodel.OTObjectRevision;
import org.jdom.Element;


/**
 * XMLDataObject
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class XMLDataObject
	implements OTDataObject
{
	private OTID globalId;
	private Element element;
	private String localId = null;
	
	Hashtable resources = new Hashtable();

	public XMLDataObject(Element element, OTID id)
	{
		this.element = element;
		globalId = id;
	}
	
	public Element getElement()
	{
		return element;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getGlobalId()
	 */
	public OTID getGlobalId()
	{
		// TODO Auto-generated method stub
		return globalId;
	}

	public void setGlobalId(String id)
	{
		setGlobalId(OTIDFactory.createOTID(id));
	}
	
	public void setGlobalId(OTID id)
	{
		globalId = id;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#setResource(java.lang.String, java.lang.Object)
	 */
	public void setResource(String key, Object resource)
	{
		// What should we do if the resource is null??
		resources.put(key, resource);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getResource(java.lang.String)
	 */
	public Object getResource(String key)
	{
		return resources.get(key);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getResourceKeys()
	 */
	public String[] getResourceKeys()
	{
		Object [] keys = resources.keySet().toArray();
		String [] strKeys = new String [keys.length];
		System.arraycopy(keys, 0, strKeys, 0, keys.length);
		return strKeys;
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getCurrentRevision()
	 */
	public OTObjectRevision getCurrentRevision()
	{
		return null;
	}

	public Collection getResourceEntries()
	{
		return resources.entrySet();
	}
	
	/**
	 * @return Returns the localId.
	 */
	public String getLocalId()
	{
		return localId;
	}
	/**
	 * @param localId The localId to set.
	 */
	public void setLocalId(String localId)
	{
		this.localId = localId;
	}
}
