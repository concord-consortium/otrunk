package org.concord.otrunk.overlay;

import java.net.URL;
import java.util.Set;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.xml.XMLDatabase;

public class OTUserSingleOverlayManager extends OTUserOverlayManager
{
	Logger logger = Logger.getLogger(this.getClass().getName());

	public OTUserSingleOverlayManager(OTrunkImpl otrunk) {
		super(otrunk);
	}

	@Override
    public void addReadOnly(URL overlayURL, OTUserObject userObject, boolean isGlobal) throws Exception {
		writeLock();
		try {
    		// get the OTOverlay OTObject from the otml at the URL specified
    		OTObjectService objService = loadOverlay(overlayURL, isGlobal);
    
    		// if the overlay exists, the create an objectservice for it and register it
    		if (objService != null) {
    			// map the object service/overlay to the user
    			userObject = getAuthoredObject(userObject);
    			userToOverlayMap.put(userObject, overlayURL);
    			readOnlyUsers.add(userObject);
    		}
		} finally {
			writeUnlock();
		}
	}
	
	@Override
    public void addWriteable(URL overlayURL, OTUserObject userObject, boolean isGlobal) throws Exception {
		writeLock();
		try {
    		// get the OTOverlay OTObject from the otml at the URL specified
    		OTObjectService objService = loadOverlay(overlayURL, isGlobal);
    
    		// if the overlay exists, the create an objectservice for it and register it
    		if (objService != null) {
    			// map the object service/overlay to the user
    			userObject = getAuthoredObject(userObject);
    			userToOverlayMap.put(userObject, overlayURL);
    			writeableUsers.add(userObject);
    		}
		} finally {
			writeUnlock();
		}
	}

	@Override
    protected OTObjectService getObjectService(OTUserObject userObj, OTObject obj) {
		readLock();
		try {
    		userObj = getAuthoredObject(userObj);
    		return overlayToObjectServiceMap.get(userToOverlayMap.get(userObj));
		} finally {
			readUnlock();
		}
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public <T extends OTObject> T getOTObject(OTUserObject userObject, T object) throws Exception {
		// userObject = getAuthoredObject(userObject);
		object = getAuthoredObject(object);
		OTObjectService objService = getObjectService(userObject, object);
		if (objService == null) {
			return null;
		}
		return (T) objService.getOTObject(object.getGlobalId());
	}
	
	@Override
    public synchronized void reload(OTUserObject userObject) throws Exception {
		readLock();
		try {
    		if (! readOnlyUsers.contains(userObject)) {
    			return;
    		}
		} finally {
			readUnlock();
		}
		userObject = getAuthoredObject(userObject);
		// check the last modified of the URL and the existing db, if they're different, remove and add the db again
		XMLDatabase xmlDb = getXMLDatabase(getObjectService(userObject, null));
		if (doesDbNeedReloaded(xmlDb)) {
			remove(userObject);
			addReadOnly(xmlDb.getSourceURL(), userObject, false);
			notifyListeners(userObject);
		}
	}
	
	@Override
    public void remoteSave(OTUserObject user, OTObject object) throws Exception {
		readLock();
		try {
    		if (! writeableUsers.contains(user)) {
    			return;
    		}
		} finally {
			readUnlock();
		}
		if (isObjectModified(user, object)) {
    		incrementSubmitCount(object);
    		user = getAuthoredObject(user);
    		OTObjectService overlayObjectService = getObjectService(user, object);
    		if (object != null) {
        		copyObjectIntoOverlay(user,  object, null);
    		
        		actualRemoteSave(overlayObjectService);
    		}
		}
	}
	
	@Override
	protected Set<OTUserObject> getAllUsers()
	{
		readLock();
		try {
			return userToOverlayMap.keySet();
		} finally {
			readUnlock();
		}
	}
}
