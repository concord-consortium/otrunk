/*
 * Created on Aug 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.datamodel.ozone;

import java.util.Vector;

import org.concord.framework.otrunk.OTObject;
import org.ozoneDB.OzoneObject;


/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OzResourceListImpl extends OzoneObject
		implements OzResourceList 
{
	Vector list = new Vector();
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#size()
	 */
	public int size() {
		return list.size();
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#get(int)
	 */
	public Object get(int index) 
	{
		return list.get(index);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#add(java.lang.Object)
	 */
	public void add(Object object) 
	{
		list.add(object);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#add(int, java.lang.Object)
	 */
	public void add(int index, Object object) 
	{
		list.add(index, object);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#set(int, java.lang.Object)
	 */
	public void set(int index, Object object) 
	{
		list.set(index, object);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#removeAll()
	 */
	public void removeAll()
	{
		list.removeAllElements();
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.ozone.OzResourceList#add(int, org.concord.otrunk.OTObject)
	 */
	public void add(int index, OTObject object)
	{
		// TODO Auto-generated method stub

	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.ozone.OzResourceList#add(org.concord.otrunk.OTObject)
	 */
	public void add(OTObject object)
	{
		// TODO Auto-generated method stub

	}
		
	/* (non-Javadoc)
	 * @see org.concord.otrunk.ozone.OzResourceList#getObject(int)
	 */
	public OTObject getObject(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#remove(int)
	 */
	public void remove(int index)
	{
		// TODO Auto-generated method stub
		
	}
}
