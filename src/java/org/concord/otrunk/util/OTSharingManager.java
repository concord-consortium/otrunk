package org.concord.otrunk.util;

import java.util.Vector;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectFilter;

public interface OTSharingManager
{
	/**
	 * 
	 * @return all objects being shared
	 */
	public Vector getAllSharedObjects();
	
	/**
	 * 
	 * @param clazz specific class of object
	 * @return all shared objects of type clazz
	 */
	public Vector getAllSharedObjects(Class clazz);
	
	/**
	 * 
	 * @param filter OTObjectFilter
	 * @return all shared objects matching the OTObjectFilter
	 */
	public Vector getAllSharedObjects(OTObjectFilter filter);
	
	/**
	 * 
	 * @param obj The OTObject to be shared
	 */
	public void share(OTObject obj);
	
	/**
	 * 
	 * @param obj the object to be removed from the shared list
	 */
	public void remove(OTObject obj);
	
	/**
	 * Method to determine if an OTObject is already shared or not
	 * @param obj The object that may be shared
	 * @return true if the object is currently shared, false otherwise
	 */
	public boolean isShared(OTObject obj);
	
	/**
	 * Add a listener which gets notified when objects are shared and removed from the sharing manager.
	 * @param listener
	 */
	public void addOTSharingListener(OTSharingListener listener);
	
	/**
	 * Remove a listener from the notification list. If the listener was not in the list, the method completes silently.
	 * @param listener
	 */
	public void removeOTSharingListener(OTSharingListener listener);
}
