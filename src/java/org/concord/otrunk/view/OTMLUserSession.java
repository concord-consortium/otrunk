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
import org.concord.framework.otrunk.OTrunk;
import org.concord.otrunk.OTStateRoot;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.user.OTReferenceMap;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.xml.ExporterJDOM;
import org.concord.otrunk.xml.XMLDatabase;
import org.concord.swing.MostRecentFileDialog;
import org.doomdark.uuid.UUID;

public class OTMLUserSession
    implements OTUserSession
{
	File currentUserFile;
	URL currentUserUrl;
	XMLDatabase userDataDB;
	
	String userName;	

	Component dialogParent;

	private OTrunkImpl otrunk;
	
	OTReferenceMap refMap;
	protected String workgroupName;
	private UUID workgroupId;
	
	public OTMLUserSession()
	{		
	}
	
	public OTMLUserSession(File dbFile, String name)
	{
		this.currentUserFile = dbFile;
		this.userName = name;
	}
	
	public OTMLUserSession(URL dbUrl, String name)
	{		
		this.currentUserUrl = dbUrl;
		this.userName = name;
	}
	
	public OTMLUserSession(XMLDatabase db, String name)
	{
		this.userDataDB = db;
		this.userName = name;
	}
	
	public void setOTrunk(OTrunk otrunk)
	{
		this.otrunk = (OTrunkImpl)otrunk;
	}
	
	public void load() throws Exception
	{
		// This will be the case if we were initialized with a file
		if(currentUserFile != null && currentUserUrl == null && userDataDB == null){
			load(currentUserFile);
			return;
		}

		// This will be the case if we were initialized with a url
		if(currentUserUrl != null && userDataDB == null){
			load(currentUserUrl);
			return;
		}
		
		if(userDataDB != null){
			load(userDataDB);
		} else {
			newLayer();
			if(userName != null){
				getUserObject().setName(userName);
			}
		}		
	}
		
	
	protected void load(URL url) throws Exception
	{
		currentUserUrl = url;		
		userDataDB = new XMLDatabase(currentUserUrl);
		userDataDB.loadObjects();			

		load(userDataDB);		
	}
	
	protected void load(File file) throws Exception
	{
		currentUserFile = file;
		URL fileUrl  = file.toURL();			
		
		load(fileUrl);
		if(userName == null){
			OTUserObject userObject = getUserObject();
			if(userObject.getName() == null){
				userObject.setName(currentUserFile.getName());
			}			
		}						
	}
	
	protected void load(XMLDatabase db) throws Exception
	{
    	initUserDb(db);
    	
    	// Set the username
    	OTUser user = refMap.getUser();
        OTUserObject aUser = (OTUserObject)user;
        if(userName != null){
        	aUser.setName(userName);
        }
        
		otrunk.registerReferenceMap(getReferenceMap());
		
		// just loading the database shouldn't cause it to have changes that need
		// saving.  If the user name was set that would be change.
		userDataDB.setDirty(false);
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
				
		// CHECKME it isn't clear if the userName should be cleared here or not 
		
		try {
			load(file);
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

	public boolean isInitialized()
    {
		// This implementation is always initialized.  Because it will 
		// just create a new database.
		return true;
    }

	public void saveAndClose()
    {	    
		// allow subclasses to do things like shut down timers before the final save.
		preSaveAndClose();
		
		if(hasUnsavedChanges()){
			save();
		}
		
		close();
    }

	public void close()
	{
		// there is nothing to do on close		
	}
	
	public void setWorkgroup(String workgroupName, UUID workgroupId)
    {
		this.workgroupName = workgroupName;
		this.workgroupId = workgroupId;	    
    }
	
	/**
	 * This method can be overriden if something needs to be done before
	 * a final save.
	 */
	protected void preSaveAndClose()
	{
		
	}
}
