/*
 * Created on Jul 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;




/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DefaultOTObject implements OTObject
{
	private OTrunk otDatabase;
	
	private OTResourceSchema resources;
	
	public DefaultOTObject(OTResourceSchema resources)
	{
		this.resources = resources;		
	}
			
	public OTID getGlobalId()
	{
		return resources.getGlobalId();
	}
	
	public String getName()
	{
		return resources.getName();
	}
	
	public void setName(String name)
	{
		resources.setName(name);
	}
		
	public boolean getInput()
	{
		return false;
	}		

	// this needs to be public right now because some views
	// need the database inorder to handle thirdparty packages
	// that don't have per instance state.
	public OTrunk getOTDatabase()
	{
		return otDatabase;
	}
	
	public void setOTDatabase(OTrunk otDatabase)
	{
		this.otDatabase = otDatabase;
	}
	
	public void init()
	{
	}
	
	public OTObject getReferencedObject(OTID id)
	{
    	try {
    		OTrunk db = getOTDatabase();
    		return db.getOTObject(getGlobalId(), id);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
	}	
}
