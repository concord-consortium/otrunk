/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-10-25 05:33:57 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.fs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.concord.otrunk.OTDataObject;
import org.concord.otrunk.OTDatabase;
import org.concord.otrunk.OTResourceCollection;
import org.concord.otrunk.OTResourceList;
import org.concord.otrunk.OTResourceMap;
import org.concord.otrunk.OTUser;
import org.concord.otrunk.OTUserDataObject;
import org.concord.otrunk.OTObject;
import org.doomdark.uuid.EthernetAddress;
import org.doomdark.uuid.NativeInterfaces;
import org.doomdark.uuid.UUID;
import org.doomdark.uuid.UUIDGenerator;


/**
 * FsDatabase
 * Class name and description
 *
 * Date created: Aug 22, 2004
 *
 * @author scott<p>
 *
 */
public class FsDatabase extends OTDatabase
{
	// This needs to be initialized from files in the db folder
	// and on closing it should be written out again
	Hashtable dbIndex = new Hashtable();

	// This needs to be initialized and saved in the db folder
	// index file.
	UUID rootId;

	File databaseFolder;
	
	public FsDatabase(File dbFolder)	
		throws Exception
	{		
		databaseFolder = dbFolder;
		load(dbFolder);
		
	}
	
	protected void load(File dbFolder)
		throws Exception
	{
		if (!dbFolder.exists()) {
			dbFolder.mkdirs();
		}
		
		if (!dbFolder.isDirectory()) {
			throw new Exception("Database folder is not a folder, " +
						"it is a regular file");
		}
		
		File dbPropFile = new File(dbFolder, "db.prop"); 
		if (dbPropFile.exists() ) {
			Properties dbProp = new Properties();
			FileInputStream dbPropStream = new FileInputStream(dbPropFile);
			dbProp.load(dbPropStream);
			dbPropStream.close();
			String rootStr = dbProp.getProperty("root-id", null);
			if (rootStr != null) {
				rootId = new UUID(rootStr);
			}			
		}
		
		System.out.println("loading objects");
		
		File []  children = dbFolder.listFiles();
		for (int i=0; i<children.length; i++) {
			String childName = children[i].getName();
			if(childName.equals("db.prop") ||
					childName.equals("CVS")) {
				continue;
			}
			
			OTDataObject obj;			
			UUID childId = new UUID(childName);
			try {
				FileInputStream inStream = new FileInputStream(children[i]);
				ObjectInputStream objInStream = new ObjectInputStream(inStream);
				obj = (OTDataObject)objInStream.readObject();
				inStream.close();
			} catch (Exception e) {
				System.err.println("Error loading obj: " + childId);
				e.printStackTrace();
				continue;
			}
			if(!childId.equals(obj.getGlobalId())) {
				throw new Exception ("Mismatched ids");
			}
			
			dbIndex.put(childId, obj);
		}
	}
	
	protected void store(File dbFolder)
	throws Exception
	{
		if (!dbFolder.exists()) {
			dbFolder.mkdirs();
		}
		
		if (!dbFolder.isDirectory()) {
			throw new Exception("Database folder is not a folder, " +
			"it is a regular file");
		}
		
		if(rootId != null) {
			File dbPropFile = new File(dbFolder, "db.prop"); 
			FileOutputStream outStream = new FileOutputStream(dbPropFile);
			Properties dbProp = new Properties();
			dbProp.put("root-id", rootId.toString());
			dbProp.store(outStream, "");
			outStream.close();
		}
		
		// TODO we should store each of these elements in a 
		// temporary place incase there is an error writting
		// an object.  Currently when there is an error writting an element
		// the whole database is corrupted.
		
		Enumeration elements = dbIndex.elements();
		while (elements.hasMoreElements()) {
			FsDataObject dataObject = (FsDataObject)(elements.nextElement());
			UUID id = dataObject.getGlobalId();
			String fileName = id.toString();
			File dataFile = new File(dbFolder, fileName);
			FileOutputStream outStream = new FileOutputStream(dataFile);
			ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
			objOutStream.writeObject(dataObject);
			objOutStream.close();
		}
	}
	
	
	/* 
	 * This returns a classless dataobject currently this is only used
	 * by the user data object code
	 * (non-Javadoc)
	 * @see org.concord.otrunk.OTDatabase#createDataObject()
	 */
	public OTDataObject createDataObject()
		throws Exception
	{
		UUIDGenerator generator = UUIDGenerator.getInstance();
    	EthernetAddress hwAddress = NativeInterfaces.getPrimaryInterface();
    	UUID id = generator.generateTimeBasedUUID(hwAddress);

    	OTDataObject dataObject = new FsDataObject(id);
    	((FsDataObject)dataObject).creationInit();
    	
    	dbIndex.put(dataObject.getGlobalId(), dataObject);

    	return dataObject;
	}
	
	public OTDataObject createDataObject(byte [] objectBytes)
		throws Exception
	{
		ByteArrayInputStream inStream = new ByteArrayInputStream(objectBytes);
		ObjectInputStream objInStream = new ObjectInputStream(inStream);
		OTDataObject obj = (OTDataObject)objInStream.readObject();
		inStream.close();
		return obj;
	}		

	public void importDataObject(OTDataObject obj)
	{
		dbIndex.put(obj.getGlobalId(), obj);
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDatabase#createCollection(java.lang.Class)
	 */
	public OTResourceCollection createCollection(OTDataObject parent, Class collectionClass)
		throws Exception
	{
		if(collectionClass.equals(OTResourceList.class)) {
			return new FsResourceList((FsDataObject)parent);
		} else if(collectionClass.equals(OTResourceMap.class)) {
			return new FsResourceMap((FsDataObject)parent);
		}
		
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDatabase#setRoot(org.concord.portfolio.PortfolioObject)
	 */
	public void setRoot(OTObject obj)
		throws Exception
	{
		rootId = obj.getGlobalId();

	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDatabase#getRoot()
	 */
	public OTObject getRoot()
		throws Exception
	{
		if (rootId == null) {
			return null;
		}
		
		return getOTObject(null, rootId);
	}

	
	public byte [] getObjectBytes(OTDataObject dObj)
		throws Exception 
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
		objOutStream.writeObject(dObj);
		objOutStream.close();
		return outStream.toByteArray();		
	}
		
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDatabase#getOTDataObject(org.concord.otrunk.OTDataObject, org.doomdark.uuid.UUID)
	 */
	public OTDataObject getOTDataObject(OTDataObject dataParent, UUID childID)
		throws Exception
	{
		// if the mode is authoring then just return the requested object
		// if the mode is student then the requested node needs to be looked up
		// in the users object table.  Then that object is setup to reference 
		// the authoring object.
		OTDataObject childDataObject = (OTDataObject)dbIndex.get(childID);
		if ( (dataParent == null) || 
				!(dataParent instanceof OTUserDataObject)) {
			return childDataObject;
		}	
		
		OTUserDataObject userObject;
		OTUser user = ((OTUserDataObject)dataParent).getUser();	
		
		return user.getUserDataObject(childDataObject);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTDatabase#close()
	 */
	public void close()
	{
		try {
			store(databaseFolder);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
