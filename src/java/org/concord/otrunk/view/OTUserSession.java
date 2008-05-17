package org.concord.otrunk.view;

import org.concord.otrunk.user.OTReferenceMap;
import org.concord.otrunk.user.OTUserObject;

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

}
