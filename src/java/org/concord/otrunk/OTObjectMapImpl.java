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
 * $Revision: 1.11 $
 * $Date: 2007-10-04 21:18:10 $
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
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.otrunk.datamodel.OTDataMap;

/**
 * PfObjectTable
 * Class name and description
 *
 * Date created: Aug 25, 2004
 *
 * @author scott<p>
 *
 */
public class OTObjectMapImpl extends OTCollectionImpl 
	implements OTObjectMap
{
    protected OTDataMap map;
    
    /**
     * This is used to store references to the OTObjects, this prevents them from 
     * getting garbage collected as long as this collection is referenced.
     */
    protected HashMap referenceMap;
    
	public OTObjectMapImpl(String resourceName, OTDataMap map, OTObjectInternal handler)
	{
		super(resourceName, handler);
		this.map = map;
	}

	public int getNumberOfObjects()
	{
		return size();
	}
	
	protected void saveReference(String key, OTObject value)
	{
		if(referenceMap == null){
			referenceMap = new HashMap();
		}
		referenceMap.put(key, value);		
	}
	
	public OTObject getObject(String key) 
	{
		OTID objId = (OTID)map.get(key);
		if(objId == null) {
			return null;
		}
		
		try {
			OTObject otObj = objectInternal.getOTObject(objId);
			
			return otObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Vector getObjectKeys()
	{
		Vector names = new Vector();
		
		String [] keys = map.getKeys();
		
		if(keys == null) {
		    return names;
		}
		
		for(int i=0; i < keys.length; i++) {
			names.add(keys[i]);
		}
		return names;
	}
	
	public void putObject(String key, OTObject otObj)
	{		try {
			OTID objId = otObj.getGlobalId();			
		
			Object previousObject = map.put(key, objId);
			
			if(previousObject instanceof OTID){
		        try {
			        previousObject = objectInternal.getOTObject((OTID) previousObject);
		        } catch (Exception e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
		        }			
			}

			saveReference(key, otObj);
			
			notifyOTChange(OTChangeEvent.OP_PUT, key, previousObject);
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}

	public void removeAll()
    {
		map.removeAll();
		referenceMap = null;
		notifyOTChange(OTChangeEvent.OP_REMOVE_ALL, null, null);
    }

	public int size()
    {
		return map.size();		
    }
}

