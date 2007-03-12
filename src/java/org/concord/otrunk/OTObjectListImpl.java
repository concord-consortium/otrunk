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
 * $Revision: 1.9 $
 * $Date: 2007-03-12 19:14:04 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.util.Vector;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.otrunk.datamodel.OTDataList;

/**
 * OTObjectList
 * Class name and description
 *
 * Date created: Nov 9, 2004
 *
 * @author scott<p>
 *
 */
public class OTObjectListImpl extends OTCollectionImpl 
	implements OTObjectList
{
	OTObjectService objService;
	OTDataList list;
	
	public OTObjectListImpl(String property, OTDataList resList, 
			OTResourceSchemaHandler handler, OTObjectService objectService)
	{
		super(property, handler);
		this.objService = objectService;
		this.list = resList;
	}
	
	public OTObject get(int index)
	{
		try {
			OTID id = (OTID)list.get(index);
			if(id == null) {
				System.err.println("Null item in object list index: " + index);
				return null;
			}
			return objService.getOTObject(id);
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return null;
	}
	
	public Vector getVector()
	{
		Vector childVector = new Vector();

		for(int i=0; i<list.size(); i++) {
			try {	
				OTID childID = (OTID)list.get(i);
				childVector.add(objService.getOTObject(childID));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return childVector;
	}
			
	public void add(OTObject obj)
	{
		OTID id = obj.getGlobalId();
		if(id == null) {
			throw new RuntimeException("adding null id object list");
		}

		list.add(id);
		notifyOTChange(OTChangeEvent.OP_ADD, obj);
	}
	
	public void add(int index, OTObject obj)
	{
		OTID id = obj.getGlobalId();
		if(id == null) {
			throw new RuntimeException("adding null id object list");
		}

		list.add(index, id);
		notifyOTChange(OTChangeEvent.OP_ADD, obj);
	}

	/*
	 * This is a hack until we can sort this out
	 * it would be best if the users of this list could have all ids hidden from
	 * them.
	 */
	public void add(OTID id)
	{
		list.add(id);
		// TODO this should be checked to see if this is the right thing here
		notifyOTChange(OTChangeEvent.OP_ADD, id);
	}
	
	public int size()
	{
		return list.size();
	}
	
	public void removeAll()
	{
		list.removeAll();
		notifyOTChange(OTChangeEvent.OP_REMOVE_ALL, null);
	}

	/**
	 * @see org.concord.framework.otrunk.OTObjectList#remove(org.concord.framework.otrunk.OTObject)
	 */
	public void remove(OTObject obj)
	{
		OTID id = obj.getGlobalId();
		if(id == null) {
			throw new RuntimeException("adding null id object list");
		}

		list.remove(id);
		notifyOTChange(OTChangeEvent.OP_REMOVE, obj);
	}

	/**
	 * @see org.concord.framework.otrunk.OTObjectList#remove(int)
	 */
	public void remove(int index)
	{
		OTID id = (OTID)list.get(index);
		list.remove(index);

		// TODO this should be checked to see if this is the right thing here
		notifyOTChange(OTChangeEvent.OP_REMOVE, id);		
	}
}
