/*
 * Created on Jul 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk;


import org.doomdark.uuid.UUID;


/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DefaultOTObject implements OTObject
{	
	public final static String RES_NAME = "name";
	
	private OTDataObject dataObject; 
	private OTDatabase otDatabase;
	
	public UUID getGlobalId()
	{
		return getDataObject().getGlobalId();
	}
	
	public String getName()
	{
		return (String)getResource(RES_NAME);
	}
	
	public void setName(String name)
	{
		setResource(RES_NAME, name);
	}
		
	public boolean getInput()
	{
		return false;
	}		
	
	public OTDataObject getDataObject()
	{
		return dataObject;
	}
	
	public void setDataObject(OTDataObject dataObject)
	{
		this.dataObject = dataObject;
	}
	
	// this needs to be public right now because some views
	// need the database inorder to handle thirdparty packages
	// that don't have per instance state.
	public OTDatabase getOTDatabase()
	{
		return otDatabase;
	}
	
	public void setOTDatabase(OTDatabase otDatabase)
	{
		this.otDatabase = otDatabase;
	}
	
	public void init()
	{
	}

	protected Object getResource(String name)
	{
		return getDataObject().getResource(name);
	}
	
	protected void setResource(String name, Object value)
	{
		getDataObject().setResource(name, value);
	}
	
	protected OTResourceList getResourceList(String key)
	{
		try {
			OTDataObject dataObject = getDataObject();

			OTResourceList list = (OTResourceList)dataObject.getResource(key);
			if(list == null) {
				list = (OTResourceList)getOTDatabase().createCollection(dataObject, OTResourceList.class);
				dataObject.setResource(key, list);
			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}	
	
	protected OTResourceMap getResourceMap(String key)
	{
		try {
			OTDataObject dataObject = getDataObject();
			
			OTResourceMap map = (OTResourceMap)dataObject.getResource(key);
			if(map == null) {
				map = (OTResourceMap)getOTDatabase().createCollection(dataObject, OTResourceMap.class);
				dataObject.setResource(key, map);
			}

			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}	

	protected OTObject getObjectResource(String name)
	{
		OTObject object;
		try {
			OTDatabase db = getOTDatabase();
			UUID objId = (UUID)getResource(name);
			if(objId == null) {
				return null;
			}

			object = (OTObject)db.getOTObject(getDataObject(), objId);
			
			return object;
		} catch (Exception e)
		{
			e.printStackTrace();
		}		

		return null;
	}
	
	protected void setObjectResource(String name, OTObject object)
	{
		UUID objId = object.getGlobalId();
		setResource(name, objId);
	}
}
