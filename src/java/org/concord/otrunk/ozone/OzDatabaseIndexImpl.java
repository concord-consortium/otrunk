package org.concord.otrunk.ozone;
import java.util.Hashtable;

import org.doomdark.uuid.UUID;
import org.ozoneDB.OzoneObject;
/*
 * Created on Aug 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OzDatabaseIndexImpl extends OzoneObject
		implements OzDatabaseIndex 
{
	private Hashtable databaseIndex = new Hashtable();
	private UUID rootID;
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OzDatabaseIndex#put(org.concord.otrunk.OTDataObjectID, org.concord.portfolio.OzonePfDataObject)
	 */
	public void put(UUID id, OzDataObject dataObject) 
	{
		// TODO Auto-generated method stub
		databaseIndex.put(id, dataObject);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OzDatabaseIndex#get(org.concord.otrunk.OTDataObjectID)
	 */
	public OzDataObject get(UUID id) 
	{
		// TODO Auto-generated method stub
		return (OzDataObject)databaseIndex.get(id);
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.OzonePfDatabaseIndex#putRoot(org.concord.otrunk.OTDataObjectID)
	 */
	public void setRoot(UUID rootID) 
	{
		this.rootID = rootID;				
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.OzonePfDatabaseIndex#getRoot()
	 */
	public UUID getRoot() {
		// TODO Auto-generated method stub
		return rootID;
	}

}
