package org.concord.otrunk.view;

import java.net.URL;

import org.concord.framework.otrunk.OTrunk;
import org.concord.otrunk.user.OTReferenceMap;
import org.concord.otrunk.user.OTUserObject;
import org.doomdark.uuid.UUID;

/**
 * OTUserSession <br>
 * This interface is used by OTViewer to work with a users layer of data.  This layer
 * could be a separate database or just a sub part of a database.
 * 
 * <p>
 * Date created: May 16, 2008
 * 
 * @author scytacki<p>
 *
 */
public interface OTUserSession
{
	/**
	 * This will be called before any of the other lifecycle methods. 
	 * 
	 * @param otrunk
	 */
	public void setOTrunk(OTrunk otrunk);
	
	/**
	 * Sets the context url of the user session data, in case the session data is not loaded from a URL
	 * (for instance, when running under SAIL). This should be called before calling load().
	 * @param url The url from which relative references will be resolved.
	 */
	public void setContextURL(URL url);
	public URL getContextURL();

	/**
	 * This will be called by the loadUserSession method of OTViewer.  
	 * It is similar to newLayer() and open() but this method does not
	 * show any gui to the user.  It just uses the current state of the 
	 * userSession to load in the data.  After this method is called 
	 * getUserObject and getReferenceMap should work.  setOTrunk will 
	 * be called before this method.
	 */
	public void load() throws Exception;
	
	/**
	 * This will return if the session has any unsaved changes.
	 * 
	 * @return
	 */
	public boolean hasUnsavedChanges();
	
	/**
	 * This is used by the OTViewer so any changes made to this point are
	 * ignored when deciding if the database needs to be saved.  The is
	 * not required to be implemented.
	 */
	public void ignoreCurrentUnsavedChanges();
	
	/**
	 * Does this user session have the ability to do a save
	 * 
	 * @return
	 */
	public boolean allowSave();

	/**
	 * This should persist any current changes.   After it is called
	 * hasUnsavedChanges should return false.  If this is the first time the data
	 * is being saved and it doesn't have a place to be saved to, then the
	 * implementation should call its own saveAs();
	 * 
	 */
	public void save();
	
	/**
	 * This should do a final save and then close down any resources the session is 
	 * using.  The session might have a timer that is saving automatically.  Or it 
	 * might have a connection open to database.  These things should be closed
	 * when this method is called.
	 * 
	 * If there are no unsaved changes then this should be the same as close()
	 * 
	 * This interface has a single method for saving and close instead of a separate 
	 * close method, because the interaction between a final save and the close is usually
	 * intertwined.  For example if a timer is running then it should be shut down before
	 * the final save.  And then if a database is open it needs to be shut down after the
	 * final save.
	 */
	public void saveAndClose();

	/**
	 * This method can be used instead of saveAndClose if the data should not be saved
	 * but the user session is being closed.
	 */
	public void close();
	
	/**
	 * This should return true if this user session has the ability to do save as
	 * @return
	 */
	public boolean allowSaveAs();
	
	/**
	 * Show a dialog for allowing the user to select a new place to save their work
	 * after this is done this will be the place where the work is saved from now on.
	 */
	public void saveAs();
	
	/**
	 * This label is used in the title bar of the OTViewer window, for a file or url based system
	 * this is typically the url of the file.
	 * 
	 * If null is returned then no label will be used and in the title bar of the OTViewer window
	 * @return
	 */
	public String getLabel();
	
	/**
	 * This determines if this user session allows the user to start with a completely new
	 * layer.  
	 * 
	 * @return
	 */
	public boolean allowNewLayer();
	
	/**
	 * Make a completely new layer.  OTViewer will check if their are unsaved changes and if so 
	 * ask the user if they want to save their current data before creating a new set of data.
	 * 
	 * This method should register the new reference map it creates with OTrunkImpl	
	 * 
	 *	After this is called the save method should become a saveAs because the name for the new
	 *	data will not be known after calling new.
	 * @throws Exception 
	 * 
	 */
	public void newLayer() throws Exception;

	/**
	 * This determines if this user session allows the user to open new set of data. 
	 * @return
	 */
	public boolean allowOpen();
	
	/**
	 * This should show a dialog that allows the user to choose a set of data to open.
	 * It returns false if they choose not to open.
	 */
	public boolean open();
	
	/**
	 * Get the reference map for this session.
	 * 
	 * @return
	 */
	public OTReferenceMap getReferenceMap();
	
	/**
	 * A convience method for getting the user out of the reference map.
	 * @return
	 */
	public OTUserObject getUserObject();

	/**
	 * This should only return true if it is ok to call the load method.
	 * Some user session implementations require a few properties to be setup before
	 * load can be called.  Objects using OTUserSession can use this method to make
	 * sure the user session is really setup.
	 * @return
	 */
	public boolean isInitialized();
	
	/**
	 * This might be called before load.  This can be used by the OTUserSession instance to look up
	 * the correct user data.  It can also be used when a new set of learner data is created, so it
	 * is assigned to this workgroup.  
	 * @param combinedName the names of the users in the workgroup
	 * @param workgroupToken a token that should be persisted so a portal implementation that doesn't
	 * use uuids can still match up this data.  These should really be scoped to the portal.
	 * @param workgroupId a uuid to identify this set of data.
	 */
	public void setWorkgroup(String combinedName, String workgroupToken, UUID workgroupId);
}
