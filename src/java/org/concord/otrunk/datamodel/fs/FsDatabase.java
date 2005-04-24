
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
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
 */

/*
 * Last modification information:
 * $Revision: 1.7 $
 * $Date: 2005-04-24 15:44:55 $
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

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTIDFactory;
import org.concord.otrunk.datamodel.OTUUID;
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
	OTID rootId;

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
				rootId = OTIDFactory.createOTID(rootStr);
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
			OTID childId = OTIDFactory.createOTID(childName);
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
			OTID id = dataObject.getGlobalId();
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
		OTID id = OTUUID.createOTUUID();
		
    	return createDataObject(id);
	}
	
	/** 
	 * This should verify the id wasn't used before
	 */
	public OTDataObject createDataObject(OTID id)
		throws Exception
	{
    	OTDataObject dataObject = new FsDataObject((OTUUID)id, this);
    	((FsDataObject)dataObject).creationInit();
    	
    	Object oldValue = dbIndex.put(dataObject.getGlobalId(), dataObject);
    	if(oldValue != null) {
    		dbIndex.put(dataObject.getGlobalId(), oldValue);
    		throw new Exception("repeated unique id");
    	}

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
	public void setRoot(OTID rootId)
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
	public OTDataObject getOTDataObject(OTDataObject dataParent, OTID childID)
		throws Exception
	{
		return (OTDataObject)dbIndex.get(childID);
	}

	/* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#contains(org.concord.framework.otrunk.OTID)
     */
    public boolean contains(OTID id)
    {
        return dbIndex.containsKey(id);
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

	/* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#getRelativeOTID(org.concord.framework.otrunk.OTID, java.lang.String)
     */
    public OTID getRelativeOTID(OTID parent, String relativePath)
    {
        throw new UnsupportedOperationException("FsDatabase does not support getRelativeOTID not supported");
    }
}
