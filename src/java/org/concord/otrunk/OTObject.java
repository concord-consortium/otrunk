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

	public void setOTDatabase(OTrunk otDatabase);	
	
	/**
	 * This method is called when an object like this is created. 
	 * That is created for the first time ever.
	 */
	public void init();	
}
