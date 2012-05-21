package org.concord.otrunk.overlay;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDataObjectFinder;
import org.concord.otrunk.datamodel.OTDataPropertyReference;
import org.concord.otrunk.user.OTReferenceMap;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.view.OTMLUserSession;
import org.concord.otrunk.view.OTUserSession;
import org.concord.otrunk.xml.XMLDataList;
import org.concord.otrunk.xml.XMLDataMap;
import org.concord.otrunk.xml.XMLDataObject;
import org.concord.otrunk.xml.XMLDatabase;

public class RotatingReferenceMapDatabase extends CompositeDatabase
{
	private static final Logger logger = Logger.getLogger(RotatingReferenceMapDatabase.class.getName());
	private OTReferenceMap originalReferenceMap;
	private OTUserSession userSession;
	
	public RotatingReferenceMapDatabase(OTDataObjectFinder objectFinder, OTReferenceMap activeOverlay, OTUserSession userSession)
    {
	    super(objectFinder, activeOverlay);
	    this.originalReferenceMap = activeOverlay;
	    this.userSession = userSession;
	    
	    registerNonDeltaObjects(activeOverlay);
    }
	
    // we need to find and record all of the non-delta objects in this base active overlay
    // so that later when we rotate, they'll be properly pulled up into the new layer
	private void registerNonDeltaObjects(OTReferenceMap activeOverlay)
    {
	    OTResourceMap map = activeOverlay.getMap();
	    for (String key : map.getKeys()) {
	    	Object value = map.get(key);
	    	if (value instanceof OTID) {
	    		recursivelyResolve((OTID) value);
	    	}
	    }
    }
	
	private ArrayList<OTID> resolvedIds = new ArrayList<OTID>();
	private void recursivelyResolve(OTID id) {
		if (resolvedIds.contains(id)) {
			return;
		}
		resolvedIds.add(id);
		try {
    		OTDataObject otDataObject = getOTDataObject(null, id);
    		if (otDataObject != null) {
        		for (String k : otDataObject.getResourceKeys()) {
        			Object child = otDataObject.getResource(k);
        			if (child instanceof OTID) {
        				recursivelyResolve((OTID) child);
        			} else if (child instanceof XMLDataList) {
        				XMLDataList list = (XMLDataList) child;
        				for (int i = 0; i < list.size(); i++) {
        					Object o = list.get(i);
        					if (o instanceof OTID) {
        						recursivelyResolve((OTID) o);
        					}
        				}
        			} else if (child instanceof XMLDataMap) {
        				XMLDataMap map = (XMLDataMap) child;
        				for (String key : map.getKeys()) {
        					Object o = map.get(key);
        					if (o instanceof OTID) {
        						recursivelyResolve((OTID) o);
        					}
        				}
        			}
        		}
    		}
		} catch (Exception e) {
			// failed to find the data object for this id
			logger.log(Level.WARNING, "Failed to find data object for id: " + id.toString(), e);
		}
	}

	@Override
	public OTDataObject getRoot() throws Exception
	{
	    return activeOverlayDb.getRoot();
	}
	
	public OTReferenceMap rotate(final OTReferenceMap newActiveReferenceMap) {
		final OTReferenceMap oldActiveReferenceMap = (OTReferenceMap) this.activeOverlay;

		Runnable rotator = new Runnable() {
	        public void run()
	        {
	    		try {
	    			writeLock();
    				newActiveReferenceMap.setUser(getUser());
    				newActiveReferenceMap.setWorkgroupId(getWorkgroupId());
    				newActiveReferenceMap.setWorkgroupToken(getWorkgroupToken());
    				
    				// Set a new activedb and activeoverlay
    				activeOverlay = newActiveReferenceMap;
    				activeOverlayDb = newActiveReferenceMap.getOverlayDatabase();
    				
    				if (userSession instanceof OTMLUserSession && activeOverlayDb instanceof XMLDatabase) {
    					((OTMLUserSession) userSession).setUserDataDb((XMLDatabase) activeOverlayDb);
    				}
    				
    				setPullAllAttributesIntoCurrentLayer(true);
    				
    				// Move the old activeoverlay into the middle deltas
    				pushMiddleOverlay(oldActiveReferenceMap);
    				
    				// Update all of the existing composite data objects to refresh their middle deltas
    				pushMiddleDeltas();
	    		} finally {
	    			writeUnlock();
	    		}
	        }
        };
		if (! EventQueue.isDispatchThread()) {
			try {
	            EventQueue.invokeAndWait(rotator);
            } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            } catch (InvocationTargetException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
		} else {
			rotator.run();
		}

		return oldActiveReferenceMap;
	}

	private void pushMiddleOverlay(OTReferenceMap oldActiveReferenceMap)
    {
	    this.middleOverlays.add(0, oldActiveReferenceMap);
    }

	private void pushMiddleDeltas()
    {
	    for (CompositeDataObject dataObj : dataObjectMap.values()) {
	    	dataObj.resetBaseObject();
	    	OTDataObject[] middleDeltas = createMiddleDeltas(dataObj.getBaseObject());
	    	dataObj.resetActiveDeltaObject();
	    	dataObj.setMiddleDeltas(middleDeltas);
        }
    }
	
	// methods to be able to pass info when rotating maps
    private OTUserObject getUser() {
    	return originalReferenceMap.getUser();
    }
    
    private String getWorkgroupId() {
    	return originalReferenceMap.getWorkgroupId();
    }
    
    private String getWorkgroupToken() {
    	return originalReferenceMap.getWorkgroupToken();
    }
    
    // Set preserveUUID on any non-composite composite data objects that have multiple parents
    @Override
    public void recordReference(OTDataObject parent, OTDataObject child, String property)
    {
        super.recordReference(parent, child, property);
        checkPreserveUUID(child);
    }
    
    @Override
    public void recordReference(OTID parentID, OTID childID, String property)
    {
        super.recordReference(parentID, childID, property);
        checkPreserveUUID(dataObjectMap.get(childID.getMappedId()));
    }
    
    private void checkPreserveUUID(OTDataObject child) {
    	if (child == null) { return; }
    	ArrayList<OTDataPropertyReference> incomingRefs = getIncomingReferences(child.getGlobalId().getMappedId());
        if (incomingRefs != null && incomingRefs.size() > 1) {
        	if (child instanceof CompositeDataObject) {
        		if (((CompositeDataObject) child).isComposite()) {
        			child = ((CompositeDataObject) child).getActiveDeltaObject();
        		} else {
        			child = ((CompositeDataObject) child).getBaseObject();
        		}
        	}
        	if (child instanceof XMLDataObject) {
        		((XMLDataObject) child).setPreserveUUID(true);
        	}
        }
    }
}
