/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2007-10-02 01:07:23 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.ArrayList;
import java.util.HashMap;

import org.concord.otrunk.datamodel.OTDataList;


/**
 * XMLResourceList
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class XMLDataList
	implements OTDataList
{
	ArrayList<Object> list = new ArrayList<Object>();
	XMLDataObject dataObject;
	
	HashMap<Integer, XMLReferenceInfo> referenceInfoMap = new HashMap<Integer, XMLReferenceInfo>();
	
	public XMLDataList(XMLDataObject parent)
	{
	    dataObject = parent;
	    if(dataObject == null) {
	        throw new UnsupportedOperationException("passing null parent not allowed");
	    }
	}
		
	private void updateModifiedTime()
	{
		dataObject.updateModifiedTime();
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
	public boolean add(Object object)
	{
		boolean added = list.add(object);
		updateModifiedTime();
		return added;
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTResourceList#add(int, java.lang.Object)
	 */
	public void add(int index, Object object)
	{
		list.add(index, object);
		updateModifiedTime();
	}

	public Object set(int index, Object object)
	{
		Object previousValue = list.set(index, object);
		updateModifiedTime();
		return previousValue;
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
		list.clear();
		updateModifiedTime();
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#remove(int)
	 */
	public void remove(int index)
	{
		list.remove(index);
		updateModifiedTime();
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTResourceList#remove(java.lang.Object)
	 */
	public boolean remove(Object obj)
	{
		boolean removed = list.remove(obj);
		if(removed){
			updateModifiedTime();
		}
		return removed;
	}

	public void setResourceInfo(int index, XMLReferenceInfo info)
	{
		referenceInfoMap.put(new Integer(index), info);
	}
	
	public XMLReferenceInfo getReferenceInfo(int index)
	{
		return referenceInfoMap.get(new Integer(index));
	}

	public boolean contains(Object obj)
    {
		return list.contains(obj);
    }
	

}
