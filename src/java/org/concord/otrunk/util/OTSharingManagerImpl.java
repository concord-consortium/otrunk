package org.concord.otrunk.util;

import java.util.Vector;

import org.concord.framework.otrunk.OTObject;

/**
 * 
 * @author sfentress
 *
 */
public class OTSharingManagerImpl implements OTSharingManager
{
	private OTSharingBundle.ResourceSchema registry;
	
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
	 * @return all shared objects of type clazz
	 */
	public Vector getAllSharedObjects(Class clazz){
		Vector allObjects = getAllSharedObjects();
		Vector clazzObjects = new Vector();
		for (int i = 0; i < allObjects.size(); i++) {
			Object obj = allObjects.get(i);
	        if (clazz.isAssignableFrom(obj.getClass())){
	        	clazzObjects.add(obj);
	        }
        }
		return clazzObjects;
	}
	
	/**
	 * This should really only add the reference to this
	 * object, but as it stands, it might move the object
	 * from its original location to the shared objects
	 * list...
	 * 
	 * @param obj The OTObject to be shared
	 */
	public void share(OTObject obj){
		registry.getSharedObjects().add(obj);
	}
	
	public void remove(OTObject obj)
    {
	    registry.getSharedObjects().remove(obj);
    }

}
