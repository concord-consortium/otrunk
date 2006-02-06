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
 * $Revision: 1.8 $
 * $Date: 2006-02-06 18:18:38 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;
import org.doomdark.uuid.EthernetAddress;
import org.doomdark.uuid.NativeInterfaces;
import org.doomdark.uuid.UUID;
import org.doomdark.uuid.UUIDGenerator;


/**
 * OTUUID
 * Class name and description
 *
 * Date created: Dec 5, 2004
 *
 * @author scott<p>
 *
 */
public class OTUUID extends UUID
	implements OTID 
{
	/**
     * first version of this class
     */
    private static final long serialVersionUID = 1L;
    
    private static boolean noEthernetInterfaces;
	private static EthernetAddress hwAddress = null;
	
	static {
		// tell the UUID code to use the standard paths 
		// to find native library
		NativeInterfaces.setUseStdLibDir(true);
	}
	
	protected OTUUID(String id)
	{
		super(id);
	}
	
	public static EthernetAddress getHWAddress()	
	{
		if(noEthernetInterfaces) {
			return null;
		}
		
		if(hwAddress != null) {
			return hwAddress;
		}
		
		try {
			hwAddress = NativeInterfaces.getPrimaryInterface();
		} catch (Throwable t) {
			// can't get the hardware address for some reason
			System.err.println("OTrunk: Not using native library for uuids");
			noEthernetInterfaces = true;
			return null;
		}

    	if(hwAddress == null) {
    		System.err.println("primary interface is null");
    		EthernetAddress [] addressArray = NativeInterfaces.getAllInterfaces();
    		for(int i=0; i<addressArray.length; i++) {
    			if(addressArray[i] != null) {
    				hwAddress = addressArray[i];
    			}  			
    		}
    		if(hwAddress == null) {
    			System.err.println("Can't find a native interface");
    		}
    	}
    	
		if(hwAddress == null) {
			noEthernetInterfaces = true;
		}
		
		return hwAddress;
	}
	
	public static OTID createOTUUID()
	{
		EthernetAddress hwAddress = getHWAddress();
				
		UUIDGenerator generator = UUIDGenerator.getInstance();

		UUID id;   	
    	if(hwAddress != null) {
    		id = generator.generateTimeBasedUUID(hwAddress);
    	} else {
    		id = generator.generateTimeBasedUUID();
    	}
    	return new OTUUID(id.toString());
	}
}
