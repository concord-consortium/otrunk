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
 * $Revision: 1.15 $
 * $Date: 2007-06-27 21:35:14 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Collection;
import java.util.Hashtable;

import org.concord.framework.otrunk.OTID;
import org.concord.otrunk.datamodel.OTDataCollection;
import org.concord.otrunk.datamodel.OTDataList;
import org.concord.otrunk.datamodel.OTDataMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDataObjectType;
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
	private OTDataObjectType type;
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
	
	public void setType(OTDataObjectType type)
	{
		this.type = type;
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
	
	public boolean setResource(String key, Object resource)
	{
	    return setResource(key, resource, true);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#setResource(java.lang.String, java.lang.Object)
	 */
	boolean setResource(String key, Object resource, boolean markDirty)
	{
		Object oldObject;		

		// Hashtables can't know the different between null and empty
		// so if it is null we'll just remove it
	    if(resource == null) {
	        oldObject = resources.remove(key);
	    } else {
	    	oldObject = resources.put(key, resource);			
		}

	    if(!markDirty) return true;
	    
		if((oldObject == null && resource == null) || 
		         (oldObject != null && 
		                 oldObject.equals(resource))) {		    
		    // the object wasn't really modified
			return false;
		} else {
			updateModifiedTime();
		}		
		
		return true;
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
	
	public OTDataCollection getResourceCollection(String key, Class collectionClass)
	{
		Object listObj = getResource(key);
		if(collectionClass.isInstance(listObj)) {
			return (OTDataCollection)listObj;
		}

	    OTDataCollection collection = null;
		if(collectionClass.equals(OTDataList.class)) {
			collection =  new XMLDataList(this);
		} else if(collectionClass.equals(OTDataMap.class)) {
			collection =  new XMLDataMap(this);
		}
		
		if(collection != null) {
		    // don't mark the object as dirty until the collection has actually be modified
		    setResource(key, collection, false);
		}
	    return collection;
	}

	public OTDataObjectType getType()
    {
		return type;
    }
}
