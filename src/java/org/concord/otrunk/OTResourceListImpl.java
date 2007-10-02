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
	protected OTDataList list;
	
	/**
	 * @param handler 
	 * 
	 */
	public OTResourceListImpl(String property, OTDataList list, 
		OTObjectInternal handler) 
	{
		super(property, handler);
		this.list = list;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#add(java.lang.Object)
	 */
	public void add(Object object) 
	{
		Object toBeStored = translateExternalToStored(object);
		list.add(toBeStored);
		notifyOTChange(OTChangeEvent.OP_ADD, object, null);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#add(int, java.lang.Object)
	 */
	public void add(int index, Object object) 
	{
		Object toBeStored = translateExternalToStored(object);

		list.add(index, toBeStored);
		notifyOTChange(OTChangeEvent.OP_ADD, object, null);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#get(int)
	 */
	public Object get(int index) 
	{
		Object stored =  list.get(index);
		return translateStoredToExternal(stored);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#remove(int)
	 */
	public void remove(int index) 
	{
		Object obj = list.get(index);
		list.remove(index);
		notifyOTChange(OTChangeEvent.OP_REMOVE, obj, null);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#remove(java.lang.Object)
	 * FIXME if this fails we need to search for the byte[] in 
	 * BlobResources
	 */
	public void remove(Object obj) 
	{
		list.remove(obj);
		notifyOTChange(OTChangeEvent.OP_REMOVE, obj, null);
		
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#set(int, java.lang.Object)
	 */
	public void set(int index, Object object) 
	{
		Object toBeStored = translateExternalToStored(object);
		Object previousValue = list.set(index, toBeStored);
		notifyOTChange(OTChangeEvent.OP_SET, object, previousValue);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceCollection#removeAll()
	 */
	public void removeAll() 
	{
		list.removeAll();
		notifyOTChange(OTChangeEvent.OP_REMOVE_ALL, null, null);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceCollection#size()
	 */
	public int size() 
	{
		return list.size();
	}
}
