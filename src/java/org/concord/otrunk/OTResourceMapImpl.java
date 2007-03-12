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
	OTDataMap map;

	/**
	 * 
	 */
	public OTResourceMapImpl(String property, OTDataMap map, 
			OTResourceSchemaHandler handler) 
	{
		super(property, handler);
		this.map = map;
	}
	
	public Object get(String key) {
		return translateToResource(map.get(key));
	}

	public String[] getKeys() {
		return map.getKeys();
	}

	public void put(String key, Object resource) {
		map.put(key, translateToData(resource));
		notifyOTChange(OTChangeEvent.OP_PUT, key);
	}

	public void removeAll() {
		map.removeAll();
		notifyOTChange(OTChangeEvent.OP_REMOVE_ALL, null);
	}

	public int size() {
		return map.size();
	}	
}
