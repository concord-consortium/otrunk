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
 * $Revision: 1.16 $
 * $Date: 2007-10-10 03:09:05 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.util.HashMap;
import java.util.Vector;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
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
	protected OTDataList list;
	
    /**
     * This is used to store references to the OTObjects, this prevents them from 
     * getting garbage collected as long as this collection is referenced. <p>
     * 
     * Using an ArrayList seemed like the natural thing to do, but it is hard to keep
     * that synchronized with the data list.  So instead a map is used.
     */
	protected HashMap referenceMap;
	
	public OTObjectListImpl(String property, OTDataList resList, OTObjectInternal objectInternal)
	{
		super(property, objectInternal);
		this.list = resList;
	}

	protected OTID getId(int index)
	{
		OTID id = (OTID)list.get(index);
		if(id == null) {
			System.err.println("Null item in object list: \n" + 
					"   " + objectInternal.getGlobalId() + "." +
					property + "[" + index + "]");
			
			return null;
		}
		
		return id;
	}
	
	public OTObject get(int index)
	{
		try {
			OTID id = getId(index);
			if(id == null) {
				return null;
			}
			OTObject otObject = objectInternal.getOTObject(id);
			
			if(referenceMap == null){
				referenceMap = new HashMap();
			}
			referenceMap.put(id, otObject);
			return otObject;
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
				OTID childID = getId(i);
				if(childID == null){
					childVector.add(null);
				} else {
					childVector.add(objectInternal.getOTObject(childID));					
				}
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
		
		if(referenceMap == null){
			referenceMap = new HashMap();
		}
		referenceMap.put(id, obj);
		
		notifyOTChange(OTChangeEvent.OP_ADD, obj, null);
	}
	
	public void add(int index, OTObject obj)
	{
		OTID id = obj.getGlobalId();
		if(id == null) {
			throw new RuntimeException("adding null id object list");
		}

		list.add(index, id);
		
		if(referenceMap == null){
			referenceMap = new HashMap();
		}
		referenceMap.put(id, obj);
		
		notifyOTChange(OTChangeEvent.OP_ADD, obj, null);
	}

	/*
	 * This is a hack until we can sort this out
	 * it would be best if the users of this list could have all ids hidden from
	 * them.
	 */
	public void add(OTID id)
	{
		list.add(id);

		// FIXME will screw up some listeners which expect an object not an 
		//  id.  But the reason this call is here is for efficiency so the actual
		//  OTObject doesn't need to be created.  So it isn't clear what to do  		
		notifyOTChange(OTChangeEvent.OP_ADD, id, null);
	}
	
	public void set(int index, OTObject obj)
	{
		OTID id = obj.getGlobalId();
		if(id == null) {
			throw new RuntimeException("adding null id object list");
		}
		
		Object previousObject = list.set(index, id);
		
		if(previousObject instanceof OTID){
	        try {
		        previousObject = objectInternal.getOTObject((OTID) previousObject);
	        } catch (Exception e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
	        }			
		}

		if(referenceMap == null){
			referenceMap = new HashMap();
		}
		referenceMap.put(id, obj);
		
		if(previousObject != null){
			// FIXME we should remove the reference from this list only if it hasn't
			// be set into 2 different places.  We'd need to track where each object 
			// was inserted to do this correctly.
		}
		
		notifyOTChange(OTChangeEvent.OP_SET, obj, previousObject);
	}

	public int size()
	{
		return list.size();
	}
	
	public void removeAll()
	{
		list.removeAll();
		
		referenceMap = null;
		
		notifyOTChange(OTChangeEvent.OP_REMOVE_ALL, null, null);
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
		if(referenceMap != null){
			referenceMap.put(id, null);
		}
		
		notifyOTChange(OTChangeEvent.OP_REMOVE, obj, null);
	}

	/**
	 * @see org.concord.framework.otrunk.OTObjectList#remove(int)
	 */
	public void remove(int index)
	{
		OTID id = getId(index);
		list.remove(index);

		
		OTObject obj = null;
		if(id != null){
			try {
				obj = objectInternal.getOTObject(id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// this is odd that we are removing the object twice
	        // but this will remove any duplicates I suppose.
			list.remove(id);
			if(referenceMap != null){
				referenceMap.put(id, null);
			}
		}
		

		notifyOTChange(OTChangeEvent.OP_REMOVE, obj, null);		
	}
		
	/**
	 * This is package protected.  It should not be used outside of this package,
	 * because it will be removed at some point.
	 * 
	 * @return
	 */
	OTDataList getDataList()
	{
		return list;
	}	
}
