/*
 * Created on Aug 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Hashtable;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTResourceCollection;
import org.concord.otrunk.datamodel.OTUser;
import org.concord.otrunk.datamodel.OTUserDataObject;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OTrunk
{
	public static final String RES_CLASS_NAME = "otObjectClass";

	protected Hashtable loadedObjects = new Hashtable();
	
	protected OTDatabase db;
	
	public OTrunk(OTDatabase db)
	{
		this.db = db;
	}
	
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

	public OTDataObject createDataObject()
		throws Exception
	{
		return db.createDataObject();
	}
	
	public OTResourceCollection createCollection(OTDataObject dataObject, 
			Class collectionClass)
		throws Exception
	{
		return db.createCollection(dataObject, collectionClass);
	}
	
	public void setRoot(OTObject obj) throws Exception
	{
		OTID id = obj.getGlobalId();
		db.setRoot(id);
	}
	
	public OTObject getRoot() throws Exception
	{
		OTDataObject rootDO = getRootDataObject();
		if(rootDO == null) {
			return null;
		}
		return getOTObject(rootDO);
	}
	
	/**
	 * The dataParent must be set so the database can correctly look up the 
	 * child object.
	 *  
	 * @param dataParent
	 * @param childID
	 * @return
	 * @throws Exception
	 */
	public OTDataObject getOTDataObject(OTDataObject dataParent, OTID childID)
		throws Exception
	{
		// sanity check
		if(childID == null) {
			throw new Exception("Null child Id");
		}
		// 
		return db.getOTDataObject(dataParent, childID);
	}
	
	public void close()
	{
		db.close();
	}
	
	public OTObject loadOTObject(OTDataObject dataObject, Class otObjectClass)
	throws	Exception
	{
		OTObject otObject = null;
		
		if(otObjectClass.isInterface()) {
			InvocationHandler handler = new OTBasicObjectHandler(dataObject, this);

		    otObject = (OTObject)Proxy.newProxyInstance(otObjectClass.getClassLoader(),
		    		new Class[] { otObjectClass }, handler);
		} else {					
			otObject = setResourcesFromSchema(dataObject, otObjectClass);
			
			// this is a necessary evil for the time being
			if(otObject instanceof DefaultOTObject) {
				((DefaultOTObject)otObject).setOTDatabase(this);
			}
		}
		
		loadedObjects.put(dataObject, otObject);
		return otObject;		
	}
	
	/**
	 * Warning: this is method should only be used when you don't know
	 * which object is requesting the new OTObject.  The requestion object
	 * is currently used to keep the context of user mode or authoring mode
	 * @param childID
	 * @return
	 * @throws Exception
	 */
	public OTObject getOTObject(OTID childID)
		throws Exception
	{
		return getOTObject(getRootDataObject(), childID);
	}
	
	public OTObject getOTObject(OTID referingId , OTID childID)
		throws Exception
	{
		OTDataObject referingObj = db.getOTDataObject(null, referingId);
		return getOTObject(referingObj, childID);
	}
	
	/**
	 * 
	 * @param referingDataObject this is the data object that is refering
	 *   to this new object
	 * @param childID the id of the new object
	 * @return the requested object or null if there is a problem
	 * @throws Exception
	 */
	public OTObject getOTObject(OTDataObject referingDataObject, OTID childID)
	throws Exception
	{
		// sanity check
		if(childID == null) {
			throw new Exception("Null child id");
		}
		OTDataObject childDataObject = getOTDataObject(referingDataObject, childID);
		if(childDataObject == null) {
			//hmmm we have a null data object that means the child doesn't 
			//exist in the database.  
			// I suppose we could throw a special "not found" exception here
			return null;
		}

		return getOTObject(childDataObject);
	}

	/**
	 * This method is only used internally. Once a data object has
	 * been tracked down then method is used to get the OTObject
	 * it checks the cache of loadedObjects before making a new one
	 * @param childDataObject
	 * @return
	 * @throws Exception
	 */
	private OTObject getOTObject(OTDataObject childDataObject)
		throws Exception
	{
		OTObject otObject = null;
		
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
	
	/**
	 * Track down the objects schema by looking at the type
	 * of class of the argument to setResources method
	 * 
	 * @param dataObject
	 * @param otObject
	 */
	public OTObject setResourcesFromSchema(OTDataObject dataObject, Class otObjectClass)
	{
		Constructor [] memberConstructors = otObjectClass.getConstructors();
		Constructor resourceConstructor = memberConstructors[0]; 
		Class [] params = resourceConstructor.getParameterTypes();
		
		if(memberConstructors.length > 1 || params == null ||
				params.length != 1) {
			System.err.println("OTObjects should only have 1 constructor" + "\n" +
					" that takes one argument which is the resource interface");
			return null;
		}
		
		Class schemaClass = params[0];

		InvocationHandler handler = new OTInvocationHandler(dataObject, this);

	    Object resources = Proxy.newProxyInstance(schemaClass.getClassLoader(),
	    		new Class[] { schemaClass }, handler);
	    
	    OTObject otObject = null;
	    try {
	    	otObject = (OTObject)resourceConstructor.newInstance(new Object[] { resources });
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }	    
	    
	    return otObject;
	}

	public OTObject getUserRuntimeObject(OTObject authoredObject, OTUser user)
		throws Exception
	{
		OTID authoredId = authoredObject.getGlobalId();
		OTDataObject authoredDataObj = db.getOTDataObject(null, authoredId);
		OTUserDataObject userData = 
			new OTUserDataObject(authoredDataObj, user, this);

		// this is a hack it should get the class from the authoring root
		// and use that to make the portfolio object
		String otObjectClassStr = (String)userData.getResource("otObjectClass");
		if(otObjectClassStr == null) {
			return null;
		}

		try {
			Class otObjectClass = Class.forName(otObjectClassStr);
			OTObject userObject = loadOTObject(userData, otObjectClass);
			return userObject;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return
	 */
	public OTDataObject getRootDataObject()
		throws Exception
	{
		return db.getRoot();
	}

	
}
