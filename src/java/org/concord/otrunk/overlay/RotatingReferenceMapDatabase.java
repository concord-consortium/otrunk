package org.concord.otrunk.overlay;

import java.util.logging.Logger;

import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDataObjectFinder;
import org.concord.otrunk.user.OTReferenceMap;
import org.concord.otrunk.user.OTUserObject;

public class RotatingReferenceMapDatabase extends CompositeDatabase
{
	private static final Logger logger = Logger.getLogger(RotatingReferenceMapDatabase.class.getName());
	private OTReferenceMap originalReferenceMap;
	
	public RotatingReferenceMapDatabase(OTDataObjectFinder objectFinder, OTReferenceMap activeOverlay)
    {
	    super(objectFinder, activeOverlay);
	    this.originalReferenceMap = activeOverlay;
    }
	
	@Override
	public OTDataObject getRoot() throws Exception
	{
	    return activeOverlayDb.getRoot();
	}
	
	public OTReferenceMap rotate(OTReferenceMap newActiveReferenceMap) {
		newActiveReferenceMap.setUser(getUser());
		newActiveReferenceMap.setWorkgroupId(getWorkgroupId());
		newActiveReferenceMap.setWorkgroupToken(getWorkgroupToken());
		
		// Set a new activedb and activeoverlay
		OTReferenceMap oldActiveReferenceMap = (OTReferenceMap) this.activeOverlay;
		
		this.activeOverlay = newActiveReferenceMap;
		this.activeOverlayDb = newActiveReferenceMap.getOverlayDatabase();
		
		setPullAllAttributesIntoCurrentLayer(true);
		
		// Move the old activeoverlay into the middle deltas
		pushMiddleOverlay(oldActiveReferenceMap);
		
		// Update all of the existing composite data objects to refresh their middle deltas
		pushMiddleDeltas();

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
}
