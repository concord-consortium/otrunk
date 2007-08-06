package org.concord.otrunk.overlay;

import org.concord.framework.otrunk.OTID;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;

/**
 * This interface abstracts the concept of an overlay.  There will be another OTObject which
 * provides the state to back this interface and intermediate object to implement this object.
 * There will also be an implementation which uses the old object structure.
 * 
 * An Overlay contains a set of delta objects and a set of non-delta objects. 
 * The delta objects add modifications to base objects.  The non-delta objects are complete
 * objects which are contained by the overlay.   
 * 
 * Within an overlay there can only be one delta object for any base object.  Multiple overlays
 * can be layered to combine a multiple delta objects for one base object.  The combination
 * of one or more delta objects with a base object is a composite object.
 * 
 * @author scytacki
 *
 */
public interface Overlay
{
	/**
	 * Return the delta which is overlayed ontop of the passed in base object.  Null is returned
	 * if there is no delta object overlaying this base object.
	 * 
	 * @param baseObject
	 * @return
	 */
	public OTDataObject getDeltaObject(OTDataObject baseObject);

	public OTDataObject createDeltaObject(OTDataObject baseObject);
	
	/**
	 * This will always return true if a non-delta id that is owned by this overlay is passed in.
	 * It might also return true if the id is the id of a delta object owned by this overlay. 
	 * 
	 * The second condition is so it can be efficiently implemented by different data models.
	 * 
	 * @param id
	 * @return
	 */
	public boolean contains(OTID id);
	
	public OTDatabase getOverlayDatabase();
	
	/**
	 * This method is used by the CompositeDatabase to register objects that were created 
	 * by the database.  If the ids of one of these registered objects is passed to the 
	 * contains method it should return true
	 * 
	 * @param childObject
	 */
	public void registerNonDeltaObject(OTDataObject childObject);
}
