package org.concord.otrunk.util;

import java.util.Enumeration;
import java.util.Vector;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectFilter;

/**
 * 
 * @author sfentress
 *
 */
public class OTSharingManagerImpl implements OTSharingManager
{
	private OTSharingBundle.ResourceSchema registry;
	private Vector listeners;
	
	public OTSharingManagerImpl(OTSharingBundle.ResourceSchema registry)
	{
		this.registry = registry;
	}
	
	/**
	 * 
	 * @return all objects being shared
	 */
	public Vector getAllSharedObjects(){
		return registry.getSharedObjects().getVector();
	}
	
	/**
	 * 
	 * @param clazz specific class of object
	 * @return all shared objects of type clazz. If null, all shared objects are returned
	 */
	public Vector getAllSharedObjects(Class clazz){
		final Class myClazz = clazz;
		
		if (myClazz == null) {
			return getAllSharedObjects();
		}
		
		OTObjectFilter filter = new OTObjectFilter() {
			public boolean keepObject(OTObject obj)
			{
				return myClazz.isAssignableFrom(obj.getClass());
			}
		};
		
		return getAllSharedObjects(filter);
	}
	
	/**
	 * 
	 * @param filter an OTObjectFilter implementation
	 * @return all shared objects matching OTObjectFilter filter
	 */
	public Vector getAllSharedObjects(OTObjectFilter filter) {
		return filter.filterList(getAllSharedObjects());
	}
	
	/**
	 * This will add the object to the list, if it isn't already there.
	 * 
	 * This should really only add the reference to this
	 * object, but as it stands, it might move the object
	 * from its original location to the shared objects
	 * list...
	 * 
	 * @param obj The OTObject to be shared
	 */
	public void share(OTObject obj){
		if (! isShared(obj)){
			registry.getSharedObjects().add(obj);
			notifyListeners(new OTSharingEvent(OTSharingEvent.SHARED, obj));
		}
	}
	
	public void remove(OTObject obj)
    {
		if (isShared(obj)) {
			registry.getSharedObjects().remove(obj);
	    	notifyListeners(new OTSharingEvent(OTSharingEvent.REMOVED, obj));
		}
    }
	
	public boolean isShared(OTObject obj)
    {
	    return registry.getSharedObjects().getVector().contains(obj);
    }

	public void addOTSharingListener(OTSharingListener listener)
    {
		if (listeners == null) {
			listeners = new Vector();
		}
		
	    listeners.add(listener);
    }

	public void removeOTSharingListener(OTSharingListener listener)
    {
		if (listeners == null) {
			listeners = new Vector();
		}
		
	    listeners.remove(listener);
    }
	
	private void notifyListeners(OTSharingEvent event) {
		if (listeners == null) {
			listeners = new Vector();
		}
		
		Enumeration ls = listeners.elements();
		while (ls.hasMoreElements()) {
			OTSharingListener lis = (OTSharingListener) ls.nextElement();
    		if (event.getType() == OTSharingEvent.SHARED) {
    			lis.objectShared(event);
    		} else if (event.getType() == OTSharingEvent.REMOVED) {
    			lis.objectRemoved(event);
    		}
		}
	}

}
