/*
 * Last modification information:
 * $Revision: 1.8 $
 * $Date: 2005-03-31 21:07:26 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Collection;
import java.util.Hashtable;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTIDFactory;
import org.concord.otrunk.datamodel.OTObjectRevision;


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
	private OTXMLElement element;
	private String localId = null;
	
	Hashtable resources = new Hashtable();

	public XMLDataObject(OTXMLElement element, OTID id)
	{
		this.element = element;
		globalId = id;
	}
	
	public OTXMLElement getElement()
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
		// Hashtables can't know the different between null and empty
		// so if it is null we'll just remove it
	    if(resource == null) {
	        resources.remove(key);
	    } else {
			resources.put(key, resource);			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getResource(java.lang.String)
	 */
	public Object getResource(String key)
	{
		Object resource = resources.get(key);
		
		if(resource instanceof XMLBlobResource) {
			byte [] bytes = ((XMLBlobResource)resource).getBytes();
			return bytes;
		}

		return resources.get(key);
	}

	public boolean isBlobResource(String key)
	{
		return resources.get(key) instanceof XMLBlobResource;	    
	}
	
	public XMLBlobResource getBlobResource(String key)
	{
	    return (XMLBlobResource)resources.get(key);
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
	
	public OTResourceCollection getResourceCollection(String key, Class collectionClass)
	{
		Object listObj = getResource(key);
		if(collectionClass.isInstance(listObj)) {
			return (OTResourceCollection)listObj;
		}

	    OTResourceCollection collection = null;
		if(collectionClass.equals(OTResourceList.class)) {
			collection =  new XMLResourceList();
		} else if(collectionClass.equals(OTResourceMap.class)) {
			collection =  new XMLResourceMap();
		}
		
		if(collection != null) {
		    setResource(key, collection);
		}
	    return collection;
	}
}
