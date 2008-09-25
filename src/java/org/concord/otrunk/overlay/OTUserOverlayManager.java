package org.concord.otrunk.overlay;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.user.OTUserObject;

public class OTUserOverlayManager
{
	HashMap overlayToObjectServiceMap;
	HashMap userToOverlayMap;
	Vector overlayDatabases;
	
	public OTUserOverlayManager() {
		overlayToObjectServiceMap = new HashMap();
		userToOverlayMap = new HashMap();
	}
	
	public void add(OTOverlay otOverlay, OTObjectService objService, OTUserObject userObject) {
		overlayToObjectServiceMap.put(otOverlay, objService);
		userToOverlayMap.put(userObject, otOverlay);
		
		if (objService instanceof OTObjectServiceImpl) {
			overlayDatabases.add(((OTObjectServiceImpl) objService).getCreationDb());
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
	
	public Vector getOverlayDatabases() {
		return this.overlayDatabases;
	}
	
	public OTObject getOverlayObject(OTUserObject userObject, OTID id) throws Exception {
		return getOverlayObject(getOverlay(userObject), id);
	}
	
	public OTObject getOverlayObject(OTOverlay overlay, OTID id) throws Exception {
		OTObjectService objService = getObjectService(overlay);
		return objService.getOTObject(id);
	}
}
