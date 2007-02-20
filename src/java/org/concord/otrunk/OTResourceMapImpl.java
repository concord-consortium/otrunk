/**
 * 
 */
package org.concord.otrunk;

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
	public OTResourceMapImpl(OTDataMap map) {
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
	}

	public void removeAll() {
		map.removeAll();
	}

	public int size() {
		return map.size();
	}	
}
