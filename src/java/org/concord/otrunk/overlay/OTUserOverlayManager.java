package org.concord.otrunk.overlay;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.OTrunkUtil;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTTransientMapID;
import org.concord.otrunk.net.HTTPRequestException;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.util.StandardPasswordAuthenticator;
import org.concord.otrunk.view.OTConfig;
import org.concord.otrunk.view.OTViewer;
import org.concord.otrunk.xml.XMLDatabase;

public abstract class OTUserOverlayManager
{
	private static final Logger logger = Logger.getLogger(OTUserOverlayManager.class.getName());
	protected static boolean doHeadBeforeGet = true;
	protected static StandardPasswordAuthenticator authenticator = new StandardPasswordAuthenticator();

	protected OTrunkImpl otrunk;
	protected ArrayList<OverlayImpl> globalOverlays = new ArrayList<OverlayImpl>();
	protected HashMap<URL, OTObjectService> overlayToObjectServiceMap = new HashMap<URL, OTObjectService>();
	protected HashMap<OTUserObject, URL> userToOverlayMap = new HashMap<OTUserObject, URL>();
	protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);


	protected ArrayList<OTUserObject> readOnlyUsers = new ArrayList<OTUserObject>();
	protected ArrayList<OTUserObject> writeableUsers = new ArrayList<OTUserObject>();
	
	protected HashMap<OTID, Integer> nonRecurseObjects = new HashMap<OTID, Integer>();
	
	private Map<OTUserObject, ArrayList<OverlayUpdateListener>> listenerMap = Collections.synchronizedMap(new HashMap<OTUserObject, ArrayList<OverlayUpdateListener>>());
	private List<OverlayUpdateListener> globalListeners = Collections.synchronizedList(new ArrayList<OverlayUpdateListener>());
	
	public static void setHeadBeforeGet(boolean doHead) {
		doHeadBeforeGet = doHead;
	}
	
	/**
	 * Add an overlay or overlayreferencemap to the UserOverlayManager. This can be used when you have a URL to an otml snippet which contains an OTOverlay or OTOverlayReferenceMap object
	 * and you don't want to fetch the object yourself.
	 * @param overlayURL
	 * @param userObject
	 * @param isGlobal
	 * @throws Exception
	 */
	public abstract void addReadOnly(URL overlayURL, OTUserObject userObject, boolean isGlobal) throws Exception;
	public abstract void addWriteable(URL overlayURL, OTUserObject userObject, boolean isGlobal) throws Exception;

	public abstract <T extends OTObject> T getOTObject(OTUserObject userObject, T object) throws Exception;
	
	protected abstract OTObjectService getObjectService(OTUserObject userObject, OTObject object);

	public void remove(OTUserObject userObject) {
		writeLock();
		try {
    		userObject = getAuthoredObject(userObject);
    		URL otOverlay = userToOverlayMap.get(userObject);
    		OTObjectService objService = overlayToObjectServiceMap.get(otOverlay);
    
    		otrunk.removeObjectService((OTObjectServiceImpl) objService);
    		overlayToObjectServiceMap.remove(otOverlay);
    		
    		readOnlyUsers.remove(userObject);
    		writeableUsers.remove(userObject);
		} finally {
			writeUnlock();
		}
	}

	public abstract void reload(OTUserObject userObject) throws Exception;

    public long getLastModified(OTUserObject userObject, OTObject object)
	{
		try {
	        OTObject otObject = getOTObject(userObject, object);
	        XMLDatabase db = getXMLDatabase(otObject);
	        return db.getUrlLastModifiedTime();
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return 0;
        }
	}

	/**
	 * Copies an object into an overlay and saves the overlay to the remote file. Basically a
	 * combination of stageObject() followed by remoteSaveStagedObject().
	 * @param user
	 * @param object
	 * @throws Exception
	 */
	public abstract void remoteSave(OTUserObject user, OTObject object) throws Exception;
	
//	/**
//	 * Copies an object into an overlay and returns the object accessed through that overlay.
//	 * This allows further modifications to be made to the object before it is saved remotely.
//	 * @param user
//	 * @param object
//	 * @throws Exception
//	 */
//	public abstract OTObject stageObject(OTUserObject user, OTObject object) throws Exception;
//	
//	/**
//	 * Saves the overlay for this staged object.
//	 * @param user
//	 * @param object
//	 * @throws Exception
//	 */
//	public abstract void remoteSaveStagedObject(OTUserObject user, OTObject object) throws Exception;
	
	protected abstract Set<OTUserObject> getAllUsers();
	
	public OTUserOverlayManager(OTrunkImpl otrunk) {
		this.otrunk = otrunk;
		XMLDatabase.SILENT_DB = true;
	}
	
	protected <T extends OTObject> T getAuthoredObject(T object) {
		if (object == null) {
			return null;
		}
		try {
			object = otrunk.getRuntimeAuthoredObject(object);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Couldn't get authored version of user object!", e);
		}
		return object;
	}
	
    @SuppressWarnings("unchecked")
    protected synchronized <T extends OTObject> T loadRemoteObject(URL url, Class<T> klass) {
    	writeLock();
    	try {
    		T remoteObject = null;
    		try {
    			remoteObject = (T) otrunk.getExternalObject(url, otrunk.getRootObjectService(), true);
    		} catch (ClassCastException e) {
    			// something is there, but not the type of object expected
    			throw e;
    		} catch (Exception e) {
    			// some error occurred...
    			logger.warning("Couldn't get overlay for user\n" + url + "\n" + e.getMessage());
    		}
    		
    		// if there isn't an overlay object, and it's not supposed to be a global one, go ahead and try to make a default one
    		if (remoteObject == null) {
    			// create a blank one
    			try {
    				logger.info("Creating empty database on the fly (root: " + klass.getSimpleName() + ")...");
        			XMLDatabase xmldb = new XMLDatabase();
        			OTObjectService objService = otrunk.createObjectService(xmldb);
        			remoteObject = objService.createObject(klass);
    
        			xmldb.setRoot(remoteObject.getGlobalId());
        			otrunk.remoteSaveData(xmldb, url, OTViewer.HTTP_PUT, authenticator);
    
        			remoteObject = (T) otrunk.getExternalObject(url, otrunk.getRootObjectService());
    			} catch (Exception e) {
    				// still an error. skip the overlay for this user/url
    				logger.warning("Couldn't create a default overlay for user\n" + url + "\n" + e.getMessage());
    			}
    		}
    		return remoteObject;
        } finally {
    		writeUnlock();
        }
	}

	protected OTObjectService loadOverlay(URL overlayURL, boolean isGlobal) {
		writeLock();
		try {
    		// get the OTOverlay OTObject from the otml at the URL specified
    		OTOverlay overlay = loadRemoteObject(overlayURL, OTOverlay.class);
    		OTObjectService objService = null;
    		// if the overlay exists, the create an objectservice for it and register it
    		if (overlay != null) {
    			OTDatabase db = registerOverlay(overlay, isGlobal);
    			objService = createObjectService(db);
    			overlayToObjectServiceMap.put(overlayURL, objService);
    		}
    		return objService;
		} finally {
			writeUnlock();
		}
	}
    
    protected CompositeDatabase registerOverlay(OTOverlay overlay, boolean isGlobal) {
    	writeLock();
    	try {
    		// initialize an OverlayImpl with the OTOverlay
    		OverlayImpl myOverlay = new OverlayImpl(overlay);
    		if(isGlobal){
    			globalOverlays.add(myOverlay);
    		}
    		// set up the CompositeDatabase
    		CompositeDatabase db = new CompositeDatabase(otrunk.getDataObjectFinder(), myOverlay);
    
    		// if it's not a global overlay, add all the global overlays to its stack of overlays
    		if(!isGlobal){
    			ArrayList<Overlay> overlays = new ArrayList<Overlay>();
    			if (globalOverlays.size() > 0) {
    				overlays.addAll(globalOverlays);
    			}
    			db.setOverlays(overlays);
    		}
    		return db;
    	} finally {
    		writeUnlock();
    	}
    }
	
	/**
	 * Creates an OTObjectService object for an OTOverlay
	 * @param overlay
	 * @param isGlobal
	 * @return
	 */
	protected OTObjectService createObjectService(OTDatabase db) {
		writeLock();
		try {
    		// create the OTObjectService and return it
    	  	OTObjectService objService = otrunk.createObjectService(db);
    	  	return objService;
		} finally {
			writeUnlock();
		}
	}

    protected OTObjectService getObjectService(OTOverlay overlay) {
    	readLock();
    	try {
    		return overlayToObjectServiceMap.get(overlay);
    	} finally {
    		readUnlock();
    	}
	}

    protected OTDatabase getDatabase(OTObject object) {
		OTObjectServiceImpl objService = (OTObjectServiceImpl) object.getOTObjectService();
		return getDatabase(objService);
	}
    
    protected OTDatabase getDatabase(OTObjectService objService) {
		if (objService != null && objService instanceof OTObjectServiceImpl) {
			return ((OTObjectServiceImpl)objService).getCreationDb();
		}
		return null;
	}

    protected XMLDatabase getXMLDatabase(OTObject object) {
    	OTDatabase db = getDatabase(object);
    	if (db instanceof XMLDatabase) {
    		return (XMLDatabase) db;
    	} else if (db instanceof CompositeDatabase) {
    		return (XMLDatabase) ((CompositeDatabase) db).getActiveOverlayDb();
    	}
    	return null;
    }
    
    protected XMLDatabase getXMLDatabase(OTObjectService objectService) {
    	OTDatabase db = getDatabase(objectService);
    	if (db instanceof XMLDatabase) {
    		return (XMLDatabase) db;
    	} else if (db instanceof CompositeDatabase) {
    		return (XMLDatabase) ((CompositeDatabase) db).getActiveOverlayDb();
    	}
    	return null;
    }

	/* (non-Javadoc)
     * @see org.concord.otrunk.overlay.OTUserOverlayManager#pruneDatabase(org.concord.otrunk.overlay.OTOverlay)
     */
    protected void pruneDatabase(OTObjectService overlayObjectService) {
		OTDatabase db = getDatabase(overlayObjectService);
    	if (db instanceof CompositeDatabase) {
    		((CompositeDatabase) db).pruneNonDeltaObjects();
    	}
	}
    
    protected boolean copyObjectIntoOverlay(OTUserObject user, OTObject object, OTObject newObject) {
    	writeLock();
    	try {
        	OTObject newWrappedObject = newObject;
        	if (newWrappedObject == null) {
        		newWrappedObject = getChangedWrappedObject(user, object);
        	}
        	// only save if there are changes
            if (newWrappedObject != null) {
            	int depth = -1;
            	boolean onlyChanges = true;
            	if (nonRecurseObjects.containsKey(getAuthoredId(object))) {
            		depth = nonRecurseObjects.get(getAuthoredId(object));
            		onlyChanges = false;
            	}
    	        ((OTObjectServiceImpl) object.getOTObjectService()).copyInto(object, newWrappedObject, depth, onlyChanges);
    	        return true;
            }
            return false;
    	} catch (Exception e) {
    		logger.log(Level.SEVERE, "Couldn't save object into overlay!", e);
    		return false;
    	} finally {
    		writeUnlock();
    	}
    	
    }
    
    /**
	 * Returns null if the object hasn't changed, otherwise returns the otobject loaded from the overlay
	 * @return
	 */
	private OTObject getChangedWrappedObject(OTUserObject user, OTObject obj) throws Exception {
        OTObject newWrappedObject = getOTObject(user, obj);
        if (OTrunkUtil.compareObjects(newWrappedObject, obj, true)) {
        	return null;
        }
        return newWrappedObject;
	}
	
	protected void actualRemoteSave(OTObjectService overlayObjectService) throws HTTPRequestException, Exception
    {
		writeLock();
		try {
            if (otrunk.isSailSavingDisabled() && ! OTConfig.isIgnoreSailViewMode()) {
            	logger.info("Not saving overlay because SAIL saving is disabled");
            } else {
            	pruneDatabase(overlayObjectService);
            	otrunk.remoteSaveData(getXMLDatabase(overlayObjectService), OTViewer.HTTP_PUT, authenticator);
            }
		} finally {
			writeUnlock();
		}
    }
	
	protected void notifyListeners(final OTUserObject user) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				synchronized(globalListeners) {
		    		for (OverlayUpdateListener l : globalListeners) {
		    			l.updated(user);
		    		}
				}
				synchronized (listenerMap) {
		    		ArrayList<OverlayUpdateListener> listeners = listenerMap.get(user);
		    		if (listeners != null) {
		    			for (OverlayUpdateListener l : listeners) {
		    				l.updated(user);
		    			}
		    		}
				}
			}
		});
	}
	
	/**
	 * register a listener which will get notified when any user's overlay gets reloaded
	 * @param listener
	 */
    public void addOverlayUpdateListener(OverlayUpdateListener listener) {
		synchronized(globalListeners) {
    		if (! globalListeners.contains(listener)) {
    			globalListeners.add(listener);
    		}
		}
	}
    
    /**
	 * register a listener which will get notified when the specified user's overlay gets reloaded
	 * @param listener
	 * @param user
	 */
    public void addOverlayUpdateListener(OverlayUpdateListener listener, OTUserObject user) {
		synchronized (listenerMap) {
			if (! userToOverlayMap.keySet().contains(user)) {
				throw new RuntimeException("Trying to register a listener for a user that's not loaded!");
			}
    		ArrayList<OverlayUpdateListener> currentListeners = listenerMap.get(user);
    		if (currentListeners == null) {
    			currentListeners = new ArrayList<OverlayUpdateListener>();
    		}
    		if (! currentListeners.contains(listener)) {
    			currentListeners.add(listener);
    		}
    		listenerMap.put(user, currentListeners);
		}
	}
	
    /**
	 * remove a listener
	 * @param listener
	 */
    public void removeOverlayUpdateListener(OverlayUpdateListener listener) {
    	synchronized(globalListeners) {
    		globalListeners.remove(listener);
    	}
		synchronized (listenerMap) {
    		for (ArrayList<OverlayUpdateListener> listeners : listenerMap.values()) {
    			listeners.remove(listener);
    		}
		}
	}
    
    /**
     * Save all user overlays.
     * @param object
     * @throws Exception
     */
	public void remoteSaveAll(OTObject object) throws Exception
	{
		writeLock();
		try {
    	    for (OTUserObject user : writeableUsers) {
    	    	remoteSave(user, object);
    	    }
		} finally {
			writeUnlock();
		}
	}
	
	public boolean isModified(OTUserObject user, OTObject obj, boolean includeChildren) {
		OTObjectService objService = getObjectService(user, obj);
		if (objService == null) {
			return false;
		}
		return otrunk.isModified(obj, objService, includeChildren);
	}
	
	public void reloadAll() throws Exception {
		writeLock();
		try {
    		// use an array of the set, so that the set can be manipulated in the reload method, and we won't
    		// have concurrent modification problems
    		Object[] allUsers = readOnlyUsers.toArray();
    		for (Object user: allUsers) {
    			reload((OTUserObject)user);
    		}
		} finally {
			writeUnlock();
		}
	}
	
	protected OTID getAuthoredId(OTObject object) {
		OTID id = object.getGlobalId();
		if (id instanceof OTTransientMapID) {
			id = ((OTTransientMapID)id).getMappedId();
		}
		return id;
	}
	
	protected boolean doesUrlNeedReloaded(URL url) throws ProtocolException, IOException {
		OTObjectService otObjectService = overlayToObjectServiceMap.get(url);
		if (otObjectService == null) {
			return true;
		}
		XMLDatabase db = getXMLDatabase(otObjectService);
		return doesDbNeedReloaded(db);
	}
	
	protected boolean doesDbNeedReloaded(XMLDatabase xmlDb) throws IOException, ProtocolException {
		if (xmlDb == null) {
			return false;
		}
		if (doHeadBeforeGet) {
    		long existingTime = xmlDb.getUrlLastModifiedTime();
    		
    		URLConnection conn = xmlDb.getSourceURL().openConnection();
    		if (conn instanceof HttpURLConnection) {
    			((HttpURLConnection) conn).setRequestMethod(OTViewer.HTTP_HEAD);
    		}
    		
    		long serverTime = conn.getLastModified();
    		logger.finer("checking reload of: " + xmlDb.getSourceURL());
    		if (existingTime != 0 && serverTime != 0 && existingTime == serverTime) {
    			// no reload needed
    			logger.finer("Not reloading overlay as modified time is the same as the currently loaded version");
    			return false;
    		} else {
    			logger.finer("Modified times indicated reload needed. current: " + existingTime + ", server: " + serverTime);
    			return true;
    		}
		}
		return true;
	}
	
	public void addNonRecurseObject(OTObject obj, int depth) {
		OTID authoredId = getAuthoredId(obj);
		nonRecurseObjects.put(authoredId, depth);
	}
	
	public void setReadOnly(OTUserObject user) {
		user = getAuthoredObject(user);
		writeLock();
		try {
    		if (writeableUsers.remove(user)) {
    			readOnlyUsers.add(user);
    		}
		} finally {
			writeUnlock();
		}
	}
	
	public void setWriteable(OTUserObject user) {
		user = getAuthoredObject(user);
		writeLock();
		try {
    		if (readOnlyUsers.remove(user)) {
    			writeableUsers.add(user);
    		}
		} finally {
			writeUnlock();
		}
	}
	
	protected void writeLock() {
		readWriteLock.writeLock().lock();
	}
	
	protected void writeUnlock() {
		readWriteLock.writeLock().unlock();
	}
	
	protected void readLock() {
		readWriteLock.readLock().lock();
	}
	
	protected void readUnlock() {
		readWriteLock.readLock().unlock();
	}

}