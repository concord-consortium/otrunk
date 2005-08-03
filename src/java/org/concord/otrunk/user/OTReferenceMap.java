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
 * $Revision: 1.3 $
 * $Date: 2005-08-03 20:52:23 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.user;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;

/**
 * OTTemplateStateMap
 * Class name and description
 *
 * Date created: Apr 22, 2005
 *
 * @author scott<p>
 *
 */
public class OTReferenceMap
    extends DefaultOTObject
{
    private ResourceSchema resources; 
    public interface ResourceSchema extends OTResourceSchema
    {
        public OTUserObject getUser();
        public void setUser(OTUserObject user);

        public OTResourceMap getMap();        
    }
    
    public OTReferenceMap(ResourceSchema resources)
    {
        super(resources);
        this.resources = resources;
    }

    public void setUser(OTUserObject user)
    {
        resources.setUser(user);
    }
    
    public OTUserObject getUser()
    {
        return resources.getUser();
    }
    
    public OTDataObject getStateObject(OTDataObject template, 
            OTDatabase stateDb)
    {
		OTResourceMap stateMap = resources.getMap();
		OTID userStateId = (OTID)stateMap.get(template.getGlobalId().toString());
		// System.err.println("OTReferenceMap: requesting stateObject for: " +
		//        template.getGlobalId());
		if(userStateId == null) {
		    return null;
		}
		
		try {
		    return stateDb.getOTDataObject(null, userStateId);
		} catch (Exception e) {
		    e.printStackTrace();
		    return null;
		}        
    }
    
    public OTDataObject createStateObject(OTDataObject template,
            OTDatabase stateDb)
    {
        try {
            OTDataObject stateObject = stateDb.createDataObject();
            OTResourceMap stateMap = resources.getMap();
            stateMap.put(template.getGlobalId().toString(), 
                    stateObject.getGlobalId());

            // store some special resources in this object
			// so we know where it came from:
            // TODO add a resource that points to the map that created
            // this object something like:
			// stateObject.setResource("user-id", user.getUserId());
			stateObject.setResource("template-id", template.getGlobalId());

			stateObject.setResource(OTrunkImpl.RES_CLASS_NAME, 
			        template.getResource(OTrunkImpl.RES_CLASS_NAME));
			
            return stateObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
