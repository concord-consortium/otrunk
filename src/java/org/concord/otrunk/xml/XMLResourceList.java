/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2004-11-22 23:05:40 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Vector;

import org.concord.otrunk.datamodel.OTResourceList;


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

}
