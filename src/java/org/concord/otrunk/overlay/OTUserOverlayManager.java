package org.concord.otrunk.overlay;

import java.util.HashMap;
import java.util.Iterator;

import org.concord.framework.otrunk.OTObjectService;
import org.concord.otrunk.user.OTUserObject;

public class OTUserOverlayManager
{
	HashMap overlayToObjectServiceMap;
	HashMap userToOverlayMap;
	
	public OTUserOverlayManager() {
		overlayToObjectServiceMap = new HashMap();
		userToOverlayMap = new HashMap();
	}
	
	public void add(OTOverlay otOverlay, OTObjectService objService, OTUserObject userObject) {
		overlayToObjectServiceMap.put(otOverlay, objService);
		userToOverlayMap.put(userObject, otOverlay);
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
}
