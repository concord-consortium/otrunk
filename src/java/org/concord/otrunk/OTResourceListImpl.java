/**
 * 
 */
package org.concord.otrunk;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.otrunk.datamodel.OTDataList;

/**
 * @author scott
 *
 */
public class OTResourceListImpl extends OTResourceCollectionImpl
	implements OTResourceList 
{
	OTDataList list;
	
	/**
	 * @param handler 
	 * 
	 */
	public OTResourceListImpl(String property, OTDataList list, 
			OTResourceSchemaHandler handler) 
	{
		super(property, handler);
		this.list = list;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#add(java.lang.Object)
	 */
	public void add(Object object) {
		list.add(translateToData(object));
		notifyOTChange(OTChangeEvent.OP_ADD, object);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#add(int, java.lang.Object)
	 */
	public void add(int index, Object object) {
		list.add(index, translateToData(object));
		notifyOTChange(OTChangeEvent.OP_ADD, object);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#get(int)
	 */
	public Object get(int index) {
		return translateToResource(list.get(index));
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#remove(int)
	 */
	public void remove(int index) {
		Object obj = list.get(index);
		list.remove(index);
		notifyOTChange(OTChangeEvent.OP_REMOVE, obj);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#remove(java.lang.Object)
	 */
	public void remove(Object obj) {
		list.remove(obj);
		notifyOTChange(OTChangeEvent.OP_REMOVE, obj);
		
		// FIXME if this fails we need to search for the byte[] in 
		// BlobResources
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#set(int, java.lang.Object)
	 */
	public void set(int index, Object object) {
		list.set(index, translateToData(object));
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceCollection#removeAll()
	 */
	public void removeAll() {
		list.removeAll();
		notifyOTChange(OTChangeEvent.OP_REMOVE_ALL, null);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceCollection#size()
	 */
	public int size() {
		return list.size();
	}

}
