/*
 * Created on Aug 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.ozone;

import org.concord.otrunk.OTDataObject;
import org.concord.otrunk.OTDatabase;
import org.concord.otrunk.OTResourceCollection;
import org.concord.otrunk.OTObject;
import org.doomdark.uuid.UUID;
import org.ozoneDB.ExternalDatabase;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OzDatabase extends OTDatabase 
{
	private ExternalDatabase db;

	// we need to save and restore these two things
	private OzDatabaseIndex dbIndex;
	private UUID rootID;
	
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

		UUID newID = dataObject.getGlobalId();
		if(newID == null) {
			throw new Exception("null id");
		}
		dbIndex.put(newID, (OzDataObject) dataObject);

		return dataObject;
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
	public void setRoot(OTObject obj) {		
		dbIndex.setRoot(obj.getGlobalId());
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDatabase#getRoot()
	 */
	public OTObject getRoot() 
	throws Exception
	{
		try {
			if(rootID == null) {
				return null;
			}
			
			return getOTObject(null, rootID);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public OTDataObject getOTDataObject(OTDataObject dataParent, UUID childID)
	throws Exception 
	{
		if(childID == null) throw new Exception("null id");
		return dbIndex.get(childID);		
	}
	
	
}
