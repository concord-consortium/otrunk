/*
 * Created on Aug 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk;

import java.util.Hashtable;

import org.doomdark.uuid.UUID;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class OTDatabase 
{
	public final static String RES_CLASS_NAME = "otObjectClass"; 
	
	protected Hashtable loadedObjects = new Hashtable();

	/* (non-Javadoc)
	 */
	public OTObject createObject(Class objectClass)
		throws Exception
	{
    	OTDataObject dataObject = createDataObject();
    	
		OTObject newObject = loadOTObject(dataObject, objectClass);
		dataObject.setResource("otObjectClass", objectClass.getName());
		newObject.init();
		
		return newObject;
	}

	// This is used by the user data object.  perhaps we can restrict it to that usage
	public abstract OTDataObject createDataObject() throws Exception;
	
	public abstract OTResourceCollection createCollection(OTDataObject parent, Class collectionClass) 
		throws Exception;
	
	public abstract void setRoot(OTObject obj) throws Exception;
	
	public abstract OTObject getRoot() throws Exception;	
	
	/**
	 * The dataParent must be set so the database can correctly look up the 
	 * child object.
	 *  
	 * @param dataParent
	 * @param childID
	 * @return
	 * @throws Exception
	 */
	public abstract OTDataObject getOTDataObject(OTDataObject dataParent, UUID childID)
		throws Exception;
	
	public abstract void close();
	
	public OTObject loadOTObject(OTDataObject dataObject, Class otObjectClass)
	throws	Exception
	{		
		OTObject otObject = (OTObject)otObjectClass.newInstance();
		otObject.setDataObject(dataObject);
		otObject.setOTDatabase(this);
		
		loadedObjects.put(dataObject, otObject);
		return otObject;		
	}
	
	/**
	 * 
	 * @param referingDataObject this is the data object that is refering
	 *   to this new object
	 * @param childID the id of the new object
	 * @return the requested object or null if there is a problem
	 * @throws Exception
	 */
	public OTObject getOTObject(OTDataObject referingDataObject, UUID childID)
	throws Exception
	{
		OTObject otObject = null;
		OTDataObject childDataObject = getOTDataObject(referingDataObject, childID);
		if(childDataObject == null) {
			//hmmm we have a null data object that means the child doesn't 
			//exist in the database.  
			// I suppose we could throw a special "not found" exception here
			return null;
		}
		
		otObject = (OTObject)loadedObjects.get(childDataObject);
		if(otObject != null) {
			return otObject;
		}
		
		String otObjectClassStr = (String)childDataObject.getResource("otObjectClass");
		if(otObjectClassStr == null) {
			return null;
		}
			
		Class otObjectClass = Class.forName(otObjectClassStr);
	
		return loadOTObject(childDataObject, otObjectClass);
	}

}
