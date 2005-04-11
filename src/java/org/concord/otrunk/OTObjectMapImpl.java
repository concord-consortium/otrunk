
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
 * $Revision: 1.3 $
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
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.datamodel.OTDataObject;

/**
 * PfObjectTable
 * Class name and description
 *
 * Date created: Aug 25, 2004
 *
 * @author scott<p>
 *
 */
public class OTObjectMapImpl implements OTObjectMap
{
	OTrunkImpl oTrunk;
	OTResourceMap map;
	OTDataObject owner;

	public OTObjectMapImpl(OTResourceMap map, OTDataObject dataObject, OTrunkImpl db)
	{
		this.oTrunk = db;
		this.map = map;
		this.owner = dataObject;
	}

	public int getNumberOfObjects()
	{
		return map.size();		
	}
	
	public OTObject getObject(String key) 
	{
		OTID objId = (OTID)map.get(key);
		if(objId == null) {
			return null;
		}
		
		try {
			OTObject pfObj = oTrunk.getOTObject(owner, objId);
			return pfObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Vector getObjectKeys()
	{
		Vector names = new Vector();
		
		String [] keys = map.getKeys();
		
		for(int i=0; i < keys.length; i++) {
			names.add(keys[i]);
		}
		return names;
	}
	
	public void putObject(String key, OTObject pfObj)
	{
		// TODO need to check for existing user
		try {
			OTID objId = pfObj.getGlobalId();			
			map.put(key, objId);
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
		
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#addChild(int, org.concord.portfolio.PortfolioObject)
	 */
	public void addChild(int index, OTObject pfObject)
	{
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#addChild(org.concord.portfolio.PortfolioObject)
	 */
	public void addChild(OTObject pfObject)
	{
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#getChild(int)
	 */
	public Object getChild(int index)
	{
		Vector keys = getObjectKeys();
		String key = (String)keys.get(index);
		return getObject(key);
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#getChildCount()
	 */
	public int getChildCount()
	{
		Vector keys = getObjectKeys();
		return keys.size();
	}

	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#removeAllChildren()
	 */
	public void removeAllChildren()
	{
	}
	
	public Vector getChildVector()
	{
		Vector childVector = new Vector();
		for(int i=0; i<getChildCount(); i++) {
			childVector.add(getChild(i));
		}
		
		return childVector;
	}
	
	public void setChildVector(Vector childVector)
	{
		return;
	}
	

}

