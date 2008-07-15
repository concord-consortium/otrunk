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
}
