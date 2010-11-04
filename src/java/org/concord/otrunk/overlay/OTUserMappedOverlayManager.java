package org.concord.otrunk.overlay;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.wrapper.OTObjectSet;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTTransientMapID;
import org.concord.otrunk.net.HTTPRequestException;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.xml.XMLDatabase;

public class OTUserMappedOverlayManager
    extends OTUserOverlayManager
{
	private static final Logger logger = Logger.getLogger(OTUserMappedOverlayManager.class.getName());
	public OTUserMappedOverlayManager(OTrunkImpl otrunk)
    {
	    super(otrunk);
	    OTDatabase db;
	    try {
    	    OTOverlay overlay = otrunk.createObject(OTOverlay.class);
    	    db = new CompositeDatabase(otrunk.getDataObjectFinder(), new OverlayImpl(overlay));
	    } catch (Exception e) {
	    	db = new XMLDatabase();
	    }
	    tempObjService = otrunk.createObjectService(db);
    }

	private HashMap<OTUserObject, OTObjectToOverlayReferenceMap> userToOverlayReferenceMaps = new HashMap<OTUserObject, OTObjectToOverlayReferenceMap>();
	private OTObjectService tempObjService;
	
	/**
	 * 
	 * @param user
	 * @param original
	 * @param reference - if null, gets the most recent entry
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public <T extends OTObject> T getOTObject(OTUserObject user, T original, OTOverlayReference reference) {
		writeLock();
		try {
    		OTID authoredId = getAuthoredId(original);
    		
    		if (reference == null) {
    			reference = findLastReference(user, original);
    		}
    		
    		if (reference != null) {
    			URL overlayURL = reference.getOverlayURL();
    			if (reference.getCanReload() && overlayToObjectServiceMap.containsKey(overlayURL)) {
    				try {
	                    if (doesUrlNeedReloaded(overlayURL)) {
	                    	removeReference(overlayURL);
	                    }
                    } catch (Exception e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                    removeReference(overlayURL);
                    }
    			}
    			if (! overlayToObjectServiceMap.containsKey(overlayURL)) {
    				loadOverlay(overlayURL, false);
    			}
    			OTObjectService objService = overlayToObjectServiceMap.get(overlayURL);
    			try {
    	            return (T) objService.getOTObject(authoredId);
                } catch (Exception e) {
    	            logger.log(Level.SEVERE, "Can't load object (" + authoredId + ") from object service with url: " + overlayURL, e);
                }
    		}
    		try {
    			return (T) tempObjService.getOTObject(authoredId);
    		} catch (Exception e) {
    			logger.log(Level.SEVERE, "Can't load object (" + authoredId + ") from temp object service", e);
    		}
    		
	        return null;
		} finally {
			writeUnlock();
		}
	}
	
	private OTOverlayReference findLastReference(OTUserObject user, OTObject object) {
		readLock();
		try {
    		if (userToOverlayReferenceMaps.containsKey(user)) {
    			OTID authoredId = getAuthoredId(object);
    			OTObjectToOverlayReferenceMap referenceMap = userToOverlayReferenceMaps.get(user);
    			OTObjectSet set = (OTObjectSet) referenceMap.getObjectToOverlayMap().getObject(authoredId.toExternalForm());
    			if (set == null || set.getObjects().size() < 1) {
    				return null;
    			}
    			return (OTOverlayReference) set.getObjects().get(set.getObjects().size()-1);
    		}
    		return null;
		} finally {
			readUnlock();
		}
	}

	@Override
    public void addReadOnly(URL referenceMapURL, OTUserObject userObject, boolean isGlobal)
	    throws Exception
	{
		writeLock();
		try {
    		OTObjectToOverlayReferenceMap referenceMap = loadRemoteObject(referenceMapURL, OTObjectToOverlayReferenceMap.class);
    		userToOverlayReferenceMaps.put(userObject, referenceMap);
    		userToOverlayMap.put(userObject, referenceMapURL);
    		readOnlyUsers.add(userObject);
		} finally {
			writeUnlock();
		}
	}
	
	@Override
    public void addWriteable(URL referenceMapURL, OTUserObject userObject, boolean isGlobal)
	    throws Exception
	{
		writeLock();
		try {
    		OTObjectToOverlayReferenceMap referenceMap = loadRemoteObject(referenceMapURL, OTObjectToOverlayReferenceMap.class);
    		userToOverlayReferenceMaps.put(userObject, referenceMap);
    		userToOverlayMap.put(userObject, referenceMapURL);
    		writeableUsers.add(userObject);
		} finally {
			writeUnlock();
		}
	}

	@Override
    public <T extends OTObject> T getOTObject(OTUserObject userObject, T object) throws Exception
	{
		return getOTObject(userObject, object, null);
	}
	
	@Override
	protected OTObjectService getObjectService(OTUserObject userObject, OTObject object)
	{
		if (object.getGlobalId() instanceof OTTransientMapID) {
			return object.getOTObjectService();
		}
	    return getOTObject(userObject, object, null).getOTObjectService();
	}
	
	@Override
	protected Set<OTUserObject> getAllUsers()
	{
		readLock();
		try {
			return userToOverlayReferenceMaps.keySet();
		} finally {
			readUnlock();
		}
	}

	@Override
    public void remove(OTUserObject userObject)
	{
		writeLock();
		try {
    		userToOverlayReferenceMaps.remove(userObject);
    		super.remove(userObject);
		} finally {
			writeUnlock();
		}
	}
	
	private void removeReference(URL referenceURL) {
		writeLock();
		try {
    		OTObjectService objService = overlayToObjectServiceMap.get(referenceURL);
    	    
    		otrunk.removeObjectService((OTObjectServiceImpl) objService);
    		overlayToObjectServiceMap.remove(referenceURL);
		} finally {
			writeUnlock();
		}
	}

	@Override
    public void reload(OTUserObject userObject) throws Exception
	{
		writeLock();
		try {
    		if (! readOnlyUsers.contains(userObject)) {
    			return;
    		}
    		OTObjectToOverlayReferenceMap otObjectToOverlayReferenceMap = userToOverlayReferenceMaps.get(userObject);
    		if (otObjectToOverlayReferenceMap == null) {
    			return;
    		}
    		XMLDatabase xmlDb = getXMLDatabase(otObjectToOverlayReferenceMap.getOTObjectService());
    		if (doesDbNeedReloaded(xmlDb)) {
    			logger.info("Reloading database: " + xmlDb.getSourceURL());
    			remove(userObject);
    			addReadOnly(xmlDb.getSourceURL(), userObject, false);
    			// TODO we could potentially also figure out which object changed and include that in our notification
    			notifyListeners(userObject);
    		}
		} finally {
			writeUnlock();
		}
	}

	@Override
    public void remoteSave(OTUserObject user, OTObject object) throws Exception
	{
		writeLock();
		try {
    		if (! writeableUsers.contains(user)) {
    			return;
    		}
    		OTOverlayReference ref = spawnOverlay(user, object);
    		addReferenceToMap(user, object, ref);
		} finally {
			writeUnlock();
		}
	}

	private void addReferenceToMap(OTUserObject user, OTObject object, OTOverlayReference ref) throws HTTPRequestException, Exception
    {
		writeLock();
		try {
    	    // add overlay reference to the user's overlay reference map
    		OTID authoredId = getAuthoredId(object);
    		OTObjectToOverlayReferenceMap map = userToOverlayReferenceMaps.get(user);
    		OTObjectSet otObjectSet = (OTObjectSet)map.getObjectToOverlayMap().getObject(authoredId.toExternalForm());
    		if (otObjectSet == null) {
    			otObjectSet = map.getOTObjectService().createObject(OTObjectSet.class);
    			map.getObjectToOverlayMap().putObject(authoredId.toExternalForm(), otObjectSet);
    		}
    		if (! otObjectSet.getObjects().contains(ref)) {
        		otObjectSet.getObjects().add(ref);
        		// save overlay reference database
        		actualRemoteSave(map.getOTObjectService());
    		}
		} finally {
			writeUnlock();
		}
    }
	
	private OTOverlayReference spawnOverlay(OTUserObject user, OTObject object) throws Exception {
		writeLock();
		try {
    		// generate next overlay url
    		OTOverlayReference overlayRef = generateNextOverlayReference(user, object);
    		// create overlay for object
    		OTObject obj = getOTObject(user, object, overlayRef);
    		OTObjectService objService = obj.getOTObjectService();
    		// copy object into overlay
    		if (copyObjectIntoOverlay(user, object, obj)) {
    			// save overlay
    			actualRemoteSave(objService);
    		}
    		return overlayRef;
		} finally {
			writeUnlock();
		}
	}

	private OTOverlayReference generateNextOverlayReference(OTUserObject user, OTObject object)
    {
		readLock();
		try {
    		OTID authoredId = getAuthoredId(object);
    	    OTOverlayReference lastRef = findLastReference(user, object);
    	    int number = 1;
    	    if (lastRef != null) {
    	    	number = lastRef.getNumber()+1;
    	    }
    	    String suffix = "-" + authoredId.toExternalForm() + "-" + number;
    	    suffix = suffix.replaceAll("[^a-zA-Z0-9\\-]+", "-") + ".otml";
    	    OTOverlayReference newRef;
            try {
    	        newRef = userToOverlayReferenceMaps.get(user).getOTObjectService().createObject(OTOverlayReference.class);
            } catch (Exception e1) {
    	        e1.printStackTrace();
    	        return null;
            }
    	    newRef.setNumber(number);
    	    URL url = null;
    	    try {
    	    	// create as URI first so that it can do any escaping of invalid url chars when it converts to a URL
    	        url = new URI(userToOverlayMap.get(user).toExternalForm().replaceFirst("\\.otml", suffix)).toURL();
            } catch (Exception e) {
    	        e.printStackTrace();
    	        return null;
            }
    	    newRef.setOverlayURL(url);
    	    return newRef;
		} finally {
			readUnlock();
		}
    }
}
