/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2004-11-12 02:02:51 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Collection;
import java.util.Hashtable;

import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTObjectRevision;
import org.doomdark.uuid.UUID;
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
	private UUID globalId;
	private Element element;
	private String localId = null;
	
	Hashtable resources = new Hashtable();

	public XMLDataObject(Element element)
	{
		this.element = element;
	}
	
	public Element getElement()
	{
		return element;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getGlobalId()
	 */
	public UUID getGlobalId()
	{
		// TODO Auto-generated method stub
		return globalId;
	}

	public void setGlobalId(String id)
	{
		setGlobalId(new UUID(id));
	}
	
	public void setGlobalId(UUID id)
	{
		globalId = id;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#setResource(java.lang.String, java.lang.Object)
	 */
	public void setResource(String key, Object resource)
	{
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
