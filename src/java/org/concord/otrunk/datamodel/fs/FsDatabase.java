/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-11-12 02:02:51 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel.fs;

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

import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTResourceCollection;
import org.concord.otrunk.datamodel.OTResourceList;
import org.concord.otrunk.datamodel.OTResourceMap;
import org.concord.otrunk.datamodel.OTUser;
import org.concord.otrunk.datamodel.OTUserDataObject;
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
public class FsDatabase implements OTDatabase
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
	 * @see org.concord.otrunk.OTDatabase#getRoot()
	 */
	public OTDataObject getRoot()
		throws Exception
	{
		if (rootId == null) {
			return null;
		}
			
		return getOTDataObject(null, rootId);
	}

	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.datamodel.OTDatabase#setRoot(org.concord.otrunk.datamodel.OTDataObject)
	 */
	public void setRoot(UUID rootId)
		throws Exception
	{
		this.rootId = rootId;
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
