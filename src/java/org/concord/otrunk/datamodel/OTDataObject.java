/*
 * Created on Aug 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;

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
		
	public OTObjectRevision getCurrentRevision();
}
