/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-06 03:51:34 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTResourceList;

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
	OTrunk oTrunk;
	OTResourceList list;
	OTDataObject owner;
	
	public OTObjectListImpl(OTResourceList resList, OTDataObject owner, 
		OTrunk oTrunk)
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
