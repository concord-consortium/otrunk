/*
 * Created on Aug 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.datamodel.ozone;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.ozoneDB.ExternalDatabase;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OzDatabase implements OTDatabase
{
	private ExternalDatabase db;

	// we need to save and restore these two things
	private OzDatabaseIndex dbIndex;
	private OTID rootID;
	
	public OzDatabase()
	{
		try {
			// create and open a new database connection
			db = ExternalDatabase.openDatabase( "ozonedb:remote://localhost:3333" );
			System.out.println( "Connected ..." );
			
			db.reloadClasses();
	
			dbIndex = null;

			try {
				dbIndex = (OzDatabaseIndex)(db.objectForName("ot_data_index"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(dbIndex == null) 
			{
				dbIndex = (OzDatabaseIndex)(db.createObject(OzDatabaseIndexImpl.class, 0, "ot_data_index"));
			}
						
			rootID = dbIndex.getRoot();
			if (rootID != null) {
				System.out.println( "Got root id: " + rootID); 
			} else {
				System.out.println( "Didn't find root id");
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
			
		}

	}
	
	public void close()
	{
		db.close();
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDatabase#createDataObject()
	 */
	public OTDataObject createDataObject()
		throws Exception
	{
		OTDataObject dataObject = (OTDataObject)db.createObject( OzDataObjectImpl.class.getName());
		
		((OzDataObject)dataObject).generateID();

		OTID newID = dataObject.getGlobalId();
		if(newID == null) {
			throw new Exception("null id");
		}
		dbIndex.put(newID, (OzDataObject) dataObject);

		return dataObject;
	}

	public OTDataObject createDataObject(OTID id)
		throws Exception
	{
		OzDataObject dataObject = (OzDataObject)db.createObject( OzDataObjectImpl.class.getName());

		dataObject.setGlobalId(id);

		OzDataObject oldObject = dbIndex.put(id, (OzDataObject) dataObject);
		if(oldObject != null) {
			dbIndex.put(id, oldObject);
			throw new Exception("repeated unique id");
		}

		return null;
	}

	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDatabase#createCollection(org.concord.otrunk.OTDataObject, java.lang.Class)
	 */
	public OTResourceCollection createCollection(OTDataObject parent,
			Class collectionClass)
		throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDatabase#setRoot(org.concord.otrunk.OTObject)
	 */
	public void setRoot(OTID rootId) {		
		dbIndex.setRoot(rootId);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDatabase#getRoot()
	 */
	public OTDataObject getRoot() 
		throws Exception
	{
		try {
			if(rootID == null) {
				return null;
			}
			
			return getOTDataObject(null, rootID);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public OTDataObject getOTDataObject(OTDataObject dataParent, OTID childID)
	throws Exception 
	{
		if(childID == null) throw new Exception("null id");
		return dbIndex.get(childID);		
	}
	
	
}
