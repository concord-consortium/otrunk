/**
 * 
 */
package org.concord.otrunk;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.datamodel.OTDataMap;

/**
 * @author scott
 *
 */
public class OTResourceMapImpl extends OTResourceCollectionImpl
	implements OTResourceMap 
{
	protected OTDataMap map;

	/**
	 * 
	 */
	public OTResourceMapImpl(String property, OTDataMap map, 
		OTObjectInternal handler) 
	{
		super(property, handler);
		this.map = map;
	}
	
	public Object get(String key) 
	{
		return translateStoredToExternal(map.get(key));
	}

	public String[] getKeys() 
	{
		return map.getKeys();
	}

	public void put(String key, Object resource) 
	{
		Object toBeStored = translateExternalToStored(resource);
		Object previousValue = map.put(key, toBeStored);
		// FIXME this doesn't translate the previous value to its 
		// external format, because that would cause a bunch of bytes  to 
		// be downloaded for something that is just being thrown away
		notifyOTChange(OTChangeEvent.OP_PUT, key, previousValue);
	}

	public void clear()
    {
		map.removeAll();
		notifyOTChange(OTChangeEvent.OP_REMOVE_ALL, null, null);	    
    }		

	public int size() 
	{
		return map.size();
	}		
}
