/*
 * Created on Aug 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk;

import org.doomdark.uuid.UUID;

//import java.util.Vector;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface OTObject 
{
	public UUID getGlobalId();
	
	public String getName();
	
	public void setName(String name);

	public boolean getInput();

	public OTDataObject getDataObject();
	
	public void setDataObject(OTDataObject dataObject);
			
	public void setOTDatabase(OTDatabase otDatabase);	
	
	public void init();
	
}
