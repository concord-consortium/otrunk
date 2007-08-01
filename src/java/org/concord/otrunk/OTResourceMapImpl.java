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
		map.put(key, toBeStored);
		notifyOTChange(OTChangeEvent.OP_PUT, key);
	}

	public void removeAll() 
	{
		map.removeAll();
		notifyOTChange(OTChangeEvent.OP_REMOVE_ALL, null);
	}

	public int size() 
	{
		return map.size();
	}		
}
