package org.concord.otrunk.overlay;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.util.StandardPasswordAuthenticator;
import org.concord.otrunk.view.OTViewer;
import org.concord.otrunk.xml.XMLDatabase;

public class OTUserOverlayManager
{
	Logger logger = Logger.getLogger(this.getClass().getName());
	HashMap overlayToObjectServiceMap = new HashMap();
	HashMap userToOverlayMap = new HashMap();
	ArrayList<OTDatabase> overlayDatabases = new ArrayList<OTDatabase>();
	OTrunkImpl otrunk;
	Vector globalOverlays = new Vector();
	
	public OTUserOverlayManager(OTrunkImpl otrunk) {
		this.otrunk = otrunk;
	}
	
	/**
	 * Add an overlay to the UserOverlayManager. This can be used when you have a URL to an otml snippet which contains an OTOverlay object
	 * and you don't want to fetch the object yourself.
	 * @param overlayURL
	 * @param contextObject
	 * @param userObject
	 * @param isGlobal
	 * @throws Exception
	 */
	public void add(URL overlayURL, OTObject contextObject, OTUserObject userObject, boolean isGlobal) throws Exception {
		// get the OTOverlay OTObject from the otml at the URL specified
		OTOverlay overlay = null;
		try {
			overlay = (OTOverlay) otrunk.getExternalObject(overlayURL, contextObject.getOTObjectService(), true);
		} catch (Exception e) {
			// some error occurred...
			logger.warning("Couldn't get overlay for user\n" + overlayURL + "\n" + e.getMessage());
		}

		// if there isn't an overlay object, and it's not supposed to be a global one, go ahead and try to make a default one
		if (overlay == null && isGlobal == false) {
			// create a blank one
			try {
				logger.info("Creating empty overlay database on the fly...");
    			XMLDatabase xmldb = new XMLDatabase();
    			overlay = (OTOverlay) contextObject.getOTObjectService().createObject(OTOverlay.class);
    			xmldb.getDataObjects().put(overlay.getGlobalId(), otrunk.getDataObjectFinder().findDataObject(overlay.getGlobalId()));
    			xmldb.setRoot(overlay.getGlobalId());
    			otrunk.remoteSaveData(xmldb, overlayURL, OTViewer.HTTP_PUT, new StandardPasswordAuthenticator());
    			
    			overlay = (OTOverlay) otrunk.getExternalObject(overlayURL, contextObject.getOTObjectService());
			} catch (Exception e) {
				// still an error. skip the overlay for this user/url
				logger.warning("Couldn't create a default overlay for user\n" + overlayURL + "\n" + e.getMessage());
			}
		}

		// if the overlay exists, the create an objectservice for it and register it
		if (overlay != null) {
			OTObjectService objService = createObjectService(overlay, isGlobal);

			// map the object service/overlay to the user
			add(overlay, objService, userObject);
		}
	}
	
	/**
	 * Creates an OTObjectService object for an OTOverlay
	 * @param overlay
	 * @param isGlobal
	 * @return
	 */
	private OTObjectService createObjectService(OTOverlay overlay, boolean isGlobal) {
		// initialize an OverlayImpl with the OTOverlay
		OverlayImpl myOverlay = new OverlayImpl(overlay);
		if(isGlobal){
			globalOverlays.add(myOverlay);
		}
		// set up the CompositeDatabase
		CompositeDatabase db = new CompositeDatabase(otrunk.getDataObjectFinder(), myOverlay);
		
		// if it's not a global overlay, add all the global overlays to its stack of overlays
		if(!isGlobal){
			ArrayList overlays = new ArrayList();
			if (globalOverlays.size() > 0) {
				overlays.addAll(globalOverlays);
			}
			db.setOverlays(overlays);
		}
		// create the OTObjectService and return it
	  	OTObjectService objService = otrunk.createObjectService(db);
	  	return objService;
	}
	
	public void add(OTOverlay otOverlay, OTObjectService objService, OTUserObject userObject) {
		overlayToObjectServiceMap.put(otOverlay, objService);
		userToOverlayMap.put(userObject, otOverlay);
		
		if (objService instanceof OTObjectServiceImpl) {
			overlayDatabases.add(getDatabase(otOverlay));
		}
	}
	
	public OTObjectService getObjectService(OTOverlay overlay) {
		return (OTObjectService) overlayToObjectServiceMap.get(overlay);
	}
	
	public OTObjectService getObjectService(OTUserObject userObj) {
		return (OTObjectService) overlayToObjectServiceMap.get(userToOverlayMap.get(userObj));
	}
	
	public OTOverlay getOverlay(OTUserObject userObj) {
		return (OTOverlay) userToOverlayMap.get(userObj);
	}
	
	public OTOverlay getOverlay(OTObjectService objService) {
		if (overlayToObjectServiceMap.containsValue(objService)) {
    		for (Iterator overlayList = userToOverlayMap.keySet().iterator(); overlayList.hasNext();) {
    	        OTOverlay overlay = (OTOverlay) overlayList.next();
    	        if (userToOverlayMap.get(overlay) == objService) {
    	        	return overlay;
    	        }
            }
		}
		return null;
	}
	
	public OTUserObject getUserObject(OTOverlay overlay) {
		if (userToOverlayMap.containsValue(overlay)) {
    		for (Iterator userList = userToOverlayMap.keySet().iterator(); userList.hasNext();) {
    	        OTUserObject userObject = (OTUserObject) userList.next();
    	        if (userToOverlayMap.get(userObject) == overlay) {
    	        	return userObject;
    	        }
            }
		}
		return null;
	}
	
	public OTUserObject getUserObject(OTObjectService objService) {
		OTOverlay overlay = getOverlay(objService);
		return getUserObject(overlay);
	}
	
	public ArrayList<OTDatabase> getOverlayDatabases() {
		return this.overlayDatabases;
	}
	
	public OTObject getOTObject(OTUserObject userObject, OTID id) throws Exception {
		return getOTObject(getOverlay(userObject), id);
	}
	
	public OTObject getOTObject(OTOverlay overlay, OTID id) throws Exception {
		OTObjectService objService = getObjectService(overlay);
		if (objService == null) {
			return null;
		}
		return objService.getOTObject(id);
	}
	
	public OTDatabase getDatabase(OTOverlay overlay) {
		OTObjectServiceImpl objService = (OTObjectServiceImpl) getObjectService(overlay);
		if (objService != null) {
			return objService.getCreationDb();
		} else {
			return null;
		}
	}

	public XMLDatabase getXMLDatabase(OTOverlay overlay) {
    	OTDatabase db = getDatabase(overlay);
    	if (db instanceof XMLDatabase) {
    		return (XMLDatabase) db;
    	} else if (db instanceof CompositeDatabase) {
    		return (XMLDatabase) ((CompositeDatabase) db).getActiveOverlayDb();
    	}
    	return null;
    }
}
