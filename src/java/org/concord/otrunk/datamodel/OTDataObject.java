/*
 * Created on Aug 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceCollection;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface OTDataObject 
{
	public OTID getGlobalId();
	
	public void setResource(String key, Object resource);
	public Object getResource(String key);
	public String [] getResourceKeys();
	
	/**
	 * This returns a collection of resources.  There are currently only 2
	 * classes that can be used here: OTResourceList and OTResourceMap
	 * 
	 * @param key
	 * @param collectionClass
	 * @return
	 */
	public OTResourceCollection getResourceCollection(String key, Class collectionClass);
	
	public OTObjectRevision getCurrentRevision();
}
