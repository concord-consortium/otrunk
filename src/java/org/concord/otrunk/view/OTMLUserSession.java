package org.concord.otrunk.view;

import java.awt.Component;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTUser;
import org.concord.otrunk.OTStateRoot;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.user.OTReferenceMap;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.xml.ExporterJDOM;
import org.concord.otrunk.xml.XMLDatabase;
import org.concord.swing.MostRecentFileDialog;

public class OTMLUserSession
    implements OTUserSession
{
	XMLDatabase userDataDB;

	File currentUserFile = null;

	Component dialogParent;

	private OTrunkImpl otrunk;
	
	OTReferenceMap refMap;
	
	public OTMLUserSession(OTrunkImpl otrunk)
    {
		this.otrunk = otrunk;
    }

	public void initSession(XMLDatabase db, String name) throws Exception
	{
		if(db != null){
			load(db, name);
		} else {
			newLayer();
			if(name != null){
				getUserObject().setName(name);
			}
		}
	}
	
	public void load(URL url, String name) throws Exception
	{
		XMLDatabase db = new XMLDatabase(url);
		db.loadObjects();
		
		load(db, name);
	}
	
	public void load(XMLDatabase db, String name) throws Exception
	{
    	initUserDb(db);
    	
    	// Set the username
    	OTUser user = refMap.getUser();
        OTUserObject aUser = (OTUserObject)user;
        if(name != null){
        	aUser.setName(name);
        }
        
		otrunk.registerReferenceMap(getReferenceMap());
		
		// just loading the database shouldn't cause it to have changes that need
		// saving.  If the user name was set that would be change.
		userDataDB.setDirty(false);
	}
	
	public void load(File file, String name) throws MalformedURLException, Exception
    {
		currentUserFile = file;
		load(file.toURL(), name);	
		
		if(name == null){
			OTUserObject userObject = getUserObject();
			if(userObject.getName() == null){
				userObject.setName(file.getName());
			}			
		}
    }

	public boolean allowNewLayer()
	{
		return true;
	}

	public boolean allowOpen()
	{
		return true;
	}

	public boolean allowSaveAs()
	{
		return true;
	}

	public boolean allowSave()
	{
		return true;
	}
	
	public String getLabel()
	{
		// we might not always have a current User file so this might need to return something else.
		if(currentUserFile != null){
			return currentUserFile.toString();
		} else {
			return null;
		}
	}

	public OTReferenceMap getReferenceMap()
	{
		return refMap;
	}

	public OTUserObject getUserObject()
	{
		return refMap.getUser();
	}

	public boolean hasUnsavedChanges()
	{
		if(userDataDB == null){
			return false;
		}
		return userDataDB.isDirty();
	}

	public void ignoreCurrentUnsavedChanges()
	{
		userDataDB.setDirty(false);
	}

	public void newLayer() throws Exception
	{
		// need to make a brand new stateDB
		userDataDB = new XMLDatabase();
		// System.out.println("otrunk: " + otrunk + " userDatabase: " +
		// userDataDB);
		OTObjectService objService = otrunk.createObjectService(userDataDB);

		OTStateRoot stateRoot =
		    (OTStateRoot) objService.createObject(OTStateRoot.class);
		userDataDB.setRoot(stateRoot.getGlobalId());
		stateRoot.setFormatVersionString("1.0");

		OTUserObject userObject = (OTUserObject) objService.createObject(OTUserObject.class);

        OTID userId = userObject.getUserId();
                        
        OTObjectMap userStateMapMap = stateRoot.getUserMap();

        refMap = (OTReferenceMap)objService.createObject(OTReferenceMap.class);
        userStateMapMap.putObject(userId.toExternalForm(), refMap);
        refMap.setUser(userObject);

        otrunk.registerReferenceMap(refMap);

		userDataDB.setDirty(false);

		currentUserFile = null;
	}

	public boolean open()
	{
		MostRecentFileDialog mrfd =
		    new MostRecentFileDialog("org.concord.otviewer.openotml");
		mrfd.setFilenameFilter("otml");

		int retval = mrfd.showOpenDialog(getDialogParent());

		File file = null;
		if (retval == MostRecentFileDialog.APPROVE_OPTION) {
			file = mrfd.getSelectedFile();
		}

		if (file == null || !file.exists()){
			return false;
		}
		
		try {
	        load(file, null);
	        return true;
        } catch (MalformedURLException e) {
	        e.printStackTrace();
        } catch (Exception e) {
	        e.printStackTrace();
        }
        
		return false;
	}

	public void save()
	{
		if (currentUserFile == null || !currentUserFile.exists()) {
			saveAs();
			return;
		}

		if (currentUserFile.exists()) {
			try {
				ExporterJDOM.export(currentUserFile, userDataDB.getRoot(),
				        userDataDB);
				userDataDB.setDirty(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void saveAs()
	{
		MostRecentFileDialog mrfd =
		    new MostRecentFileDialog("org.concord.otviewer.saveotml");
		mrfd.setFilenameFilter("otml");

		if (currentUserFile != null) {
			mrfd.setCurrentDirectory(currentUserFile.getParentFile());
			mrfd.setSelectedFile(currentUserFile);
		}

		int retval = mrfd.showSaveDialog(getDialogParent());

		File file = null;
		if (retval == MostRecentFileDialog.APPROVE_OPTION) {
			file = mrfd.getSelectedFile();

			String fileName = file.getPath();
			currentUserFile = file;

			if (!fileName.toLowerCase().endsWith(".otml")) {
				currentUserFile =
				    new File(currentUserFile.getAbsolutePath() + ".otml");
			}

			try {
				ExporterJDOM.export(currentUserFile, userDataDB.getRoot(),
				        userDataDB);
				userDataDB.setDirty(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Component getDialogParent()
    {
    	return dialogParent;
    }

	public void setDialogParent(Component dialogParent)
    {
    	this.dialogParent = dialogParent;
    }

	public OTDatabase getUserDataDb()
    {
		return userDataDB;
    }

	protected void initUserDb(XMLDatabase userDataDb) 
	throws Exception
	{
		this.userDataDB = userDataDb;
		otrunk.addDatabase(userDataDB);		

		OTObjectService objService = otrunk.createObjectService(userDataDb);

		OTDataObject rootDO = userDataDb.getRoot();

		OTStateRoot stateRoot = 
			(OTStateRoot)objService.getOTObject(rootDO.getGlobalId());

		OTObjectMap userMap = stateRoot.getUserMap();

		// find the user from this database.
		// this currently is the first user in the userMap
		Vector keys = userMap.getObjectKeys();
		refMap = (OTReferenceMap)userMap.getObject((String)keys.get(0));
	}



}
