/*
 * Created on Jul 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.datamodel.ozone;

import java.util.Hashtable;

import org.concord.otrunk.datamodel.OTObjectRevision;
import org.concord.otrunk.datamodel.OTResourceCollection;
import org.concord.otrunk.datamodel.OTResourceList;
import org.doomdark.uuid.EthernetAddress;
import org.doomdark.uuid.NativeInterfaces;
import org.doomdark.uuid.UUID;
import org.doomdark.uuid.UUIDGenerator;
import org.ozoneDB.OzoneObject;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OzDataObjectImpl extends OzoneObject
	implements OzDataObject 
{
    final static long serialVersionUID = 1L;

    Hashtable resources = new Hashtable();
	UUID id;
    
	public void generateID()
	{
    	UUIDGenerator generator = UUIDGenerator.getInstance();
    	EthernetAddress hwAddress = NativeInterfaces.getPrimaryInterface();
    	id = generator.generateTimeBasedUUID(hwAddress);
	}
	
	public void setGlobalId(UUID id)
	{
		this.id = id;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.OzDataObject#addResource(java.lang.String, java.lang.Object)
	 */
	public void setResource(String name, Object resource) 
	{
		resources.put(name, resource);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getResource(java.lang.String)
	 */
	public Object getResource(String name) 
	{
		return resources.get(name);
	}

	public String [] getResourceKeys()
	{
		Object [] keys = resources.keySet().toArray();
		String [] strKeys = new String [keys.length];
		System.arraycopy(keys, 0, strKeys, 0, keys.length);
		return strKeys;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getResourceList(java.lang.String)
	 */
	public OTResourceCollection getResourceCollection(String key) 
	{
		Object listObj = resources.get(key);
		if(listObj instanceof OTResourceList) {
			return (OTResourceList)listObj;
		}
		
		if(listObj != null) {
			// an non list object is stored in this resource slot
			// probably we should throw an exception
			return null;
		}
		
		// create a resource list object
		// add it as a resource with this name
		OzResourceList list = (OzResourceList)(database().createObject(OzResourceListImpl.class));
		resources.put(key, list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getGlobalId()
	 */
	public UUID getGlobalId() 
	{
		return id;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDataObject#getCurrentRevision()
	 */
	public OTObjectRevision getCurrentRevision()
	{
		// TODO Auto-generated method stub
		return null;
	}	
}
