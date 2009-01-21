package org.concord.otrunk.overlay;

import java.util.ArrayList;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;

public class OverlayImpl
    implements Overlay
{
	private OTOverlay otOverlay;

	/**
	 * This is the database of the overlay itself.  This is NOT the composite database that 
	 * presents the combination of multiple overlays.
	 */
    private OTDatabase overlayDb;

	private ArrayList<OverlayListener> listeners = new ArrayList<OverlayListener>();

	public OverlayImpl(OTOverlay otOverlay)
	{
		this.otOverlay = otOverlay;
		
		OTObjectServiceImpl objServiceImpl = 
			(OTObjectServiceImpl)otOverlay.getOTObjectService();

		overlayDb = objServiceImpl.getCreationDb();

		
	}
	
	public boolean contains(OTID id)
	{
		// If we haven't done it before, we should go through all the delta objects at this 
		// point and track down any non delta objects they are creating.
		
		// All of the values in the otOverlays map are delta objects.
		// If we could check containment then any reference to a child element 
		// which was contained by a delta object would be a non delta object
		// if we can't use containment, then we can check if the reference object is in our
		// map, but we have no way to know if the object is part of the overlay or not.  
		
		// Because we don't have explicit containment yet, (maybe now is the time)
		// we need to make a list in the OTObject to track non delta objects.
		
		// TODO Auto-generated method stub
		OTResourceList nonDeltaObjects = otOverlay.getNonDeltaObjects();
		for(int i=0; i<nonDeltaObjects.size(); i++){
			OTID nonDeltaId = (OTID)nonDeltaObjects.get(i);
			if(nonDeltaId.equals(id)){
				return true;
			}
		}

		return false;
	}

	public OTDataObject createDeltaObject(OTDataObject baseObject)
	{
        try {
            OTDataObject stateObject = overlayDb.createDataObject(baseObject.getType());
            OTResourceMap deltaObjectMap = otOverlay.getDeltaObjectMap();
            deltaObjectMap.put(baseObject.getGlobalId().toExternalForm(), 
                    stateObject.getGlobalId());
			
            for(int i=0; i<listeners.size(); i++){
            	((OverlayListener)listeners.get(i)).newDeltaObject(this, baseObject);
            }
            
            return stateObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}

	public OTDataObject getDeltaObject(OTDataObject baseObject)
	{
		OTResourceMap detalObjectMap = otOverlay.getDeltaObjectMap();
		OTID deltaObjectId = (OTID)detalObjectMap.get(baseObject.getGlobalId().toExternalForm());
		// System.err.println("OTReferenceMap: requesting stateObject for: " +
		//        template.getGlobalId());
		if(deltaObjectId == null) {
		    return null;
		}
		
		try {
		    return overlayDb.getOTDataObject(null, deltaObjectId);
		} catch (Exception e) {
		    e.printStackTrace();
		    return null;
		}        
	}

	public OTDatabase getOverlayDatabase()
	{
		return overlayDb;
	}

	public void registerNonDeltaObject(OTDataObject childObject)
    {
		OTID id = childObject.getGlobalId();
		OTResourceList nonDeltaObjects = otOverlay.getNonDeltaObjects();
		for(int i=0; i<nonDeltaObjects.size(); i++){
			OTID nonDeltaId = (OTID)nonDeltaObjects.get(i);
			if(nonDeltaId.equals(id)){
				// This objec is already registered.
				return;
			}
		}

		nonDeltaObjects.add(id);	    
    }

	public void addOverlayListener(OverlayListener listener)
    {
		listeners.add(listener);	    
    }

	public void removeOverlayListener(OverlayListener listener)
    {
		listeners.remove(listener);	    
    }

}
