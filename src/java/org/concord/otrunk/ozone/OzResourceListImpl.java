/*
 * Created on Aug 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.ozone;

import java.util.Vector;

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
	 * @see org.concord.otrunk.OTResourceList#removeAll()
	 */
	public void removeAll()
	{
		list.removeAllElements();
	}
}
