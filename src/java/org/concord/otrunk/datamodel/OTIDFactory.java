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
 * $Revision: 1.6 $
 * $Date: 2005-08-22 21:09:52 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;


/**
 * OTIDFactory
 * Class name and description
 *
 * Date created: Dec 5, 2004
 *
 * @author scott<p>
 *
 */
public class OTIDFactory
{	
	public static OTID createOTID(String otidStr)
	{

	    int bangIndex = otidStr.indexOf("!");
	    if(bangIndex == 0) {
	        throw new RuntimeException("Unknown id format");
	    }
	    
	 // First, check to see if it's a transient id
	    if (otidStr.startsWith(OTTransientMapID.TRANSIENT_ID_PREFIX)) {
	    	
	    	String dbIdStr = otidStr.substring(OTTransientMapID.TRANSIENT_ID_PREFIX.length(), bangIndex);
	    	OTID dbId = OTIDFactory.createOTID(dbIdStr);
	    	
	    	String objIdStr = otidStr.substring(bangIndex+1);
	    	OTID objId = OTIDFactory.createOTID(objIdStr);
	    	
	    	return new OTTransientMapID(dbId, objId);
	    }
	    
	    // ok, it's not a transient id...
	    
	    String currentIdStr = otidStr;	    	   
	    if(bangIndex > 0) {
	        currentIdStr = otidStr.substring(0,bangIndex);
	    }
	    
        OTID currentId = null;
        // First try to make an OTUUID out of the id
		try {
			currentId = new OTUUID(currentIdStr);
		} catch (Exception e) {
			// FIXME we should only catch malformed exceptions here
		    currentId = new OTPathID(currentIdStr);
		}
	    
		if(bangIndex < 0) {
		    // end of recursion
		    return currentId;
		}
		
		// recurse
		String relativePath = otidStr.substring(bangIndex+1);
		OTID relativeId = createOTID(relativePath);
		return new OTRelativeID(currentId, relativeId);		
    }
}
