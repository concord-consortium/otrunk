
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
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
 */

/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2005-04-11 15:01:08 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.otrunk.datamodel.OTDataObject;

/**
 * OTObjectList
 * Class name and description
 *
 * Date created: Nov 9, 2004
 *
 * @author scott<p>
 *
 */
public class OTObjectListImpl implements OTObjectList
{
	OTrunkImpl oTrunk;
	OTResourceList list;
	OTDataObject owner;
	
	public OTObjectListImpl(OTResourceList resList, OTDataObject owner, 
		OTrunkImpl oTrunk)
	{
		this.oTrunk = oTrunk;
		this.list = resList;
		this.owner = owner;
	}
	
	public OTObject get(int index)
	{
		try {
			OTID id = (OTID)list.get(index);
			if(id == null) {
				System.err.println("Null item in object list index: " + index);
				return null;
			}
			return oTrunk.getOTObject(owner, id);
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
				childVector.add(oTrunk.getOTObject(owner, childID));
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
	}
	
	public void add(int index, OTObject obj)
	{
		OTID id = obj.getGlobalId();
		if(id == null) {
			throw new RuntimeException("adding null id object list");
		}

		list.add(index, id);
	}

	/*
	 * This is a hack until we can sort this out
	 * it would be best if the users of this list could have all ids hidden from
	 * them.
	 */
	public void add(OTID id)
	{
		list.add(id);
	}
	
	public int size()
	{
		return list.size();
	}
	
	public void removeAll()
	{
		list.removeAll();
	}
}
