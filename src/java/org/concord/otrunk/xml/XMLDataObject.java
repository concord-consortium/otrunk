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
 * $Revision: 1.12 $
 * $Date: 2005-08-03 20:52:23 $
 * $Author: maven $
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
import org.concord.otrunk.datamodel.OTDatabase;
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
	private XMLDatabase database = null;
	private OTXMLElement element;
	private String localId = null;
	
	Hashtable resources = new Hashtable();

	
	
	XMLDataObject(OTXMLElement element, OTID id, XMLDatabase db)
	{
		this.element = element;
		globalId = id;
		database = db;
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
		return globalId;
	}

	public OTDatabase getDatabase()
	{
	    return database;
	}
	
	public void setGlobalId(String id)
	{
		setGlobalId(OTIDFactory.createOTID(id));
	}
	
	public void setGlobalId(OTID id)
	{
		globalId = id;
	}
	
	public void setResource(String key, Object resource)
	{
	    setResource(key, resource, true);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#setResource(java.lang.String, java.lang.Object)
	 */
	void setResource(String key, Object resource, boolean markDirty)
	{
		Object oldObject = resources.get(key);		

		// Hashtables can't know the different between null and empty
		// so if it is null we'll just remove it
	    if(resource == null) {
	        resources.remove(key);
	    } else {
			resources.put(key, resource);			
		}

	    if(!markDirty) return;
	    
		if((oldObject == null && resource == null) || 
		         (oldObject != null && 
		                 oldObject.equals(resource))) {		    
		    // the object wasn't really modified
		} else {
			updateModifiedTime();
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

	void updateModifiedTime()
	{
	    // update our revision 
	    // tell the database that it is "dirty" and needs to 
	    // be synced
	    database.setDirty(true);
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
			collection =  new XMLResourceList(this);
		} else if(collectionClass.equals(OTResourceMap.class)) {
			collection =  new XMLResourceMap(this);
		}
		
		if(collection != null) {
		    // don't mark the object as dirty until the collection has actually be modified
		    setResource(key, collection, false);
		}
	    return collection;
	}
}
