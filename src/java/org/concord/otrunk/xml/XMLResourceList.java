/*
 * Last modification information:
 * $Revision: 1.5 $
 * $Date: 2005-03-10 03:52:25 $
 * $Author: imoncada $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Vector;

import org.concord.framework.otrunk.OTResourceList;


/**
 * XMLResourceList
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class XMLResourceList
	implements OTResourceList
{
	Vector list = new Vector();

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

	public void set(int index, Object object)
	{
		list.set(index, object);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceCollection#size()
	 */
	public int size()
	{
		return list.size();
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceCollection#removeAll()
	 */
	public void removeAll()
	{
		list.removeAllElements();
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#remove(int)
	 */
	public void remove(int index)
	{
		list.remove(index);
	}

}
