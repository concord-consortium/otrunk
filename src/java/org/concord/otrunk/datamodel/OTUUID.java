/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-06 03:51:35 $
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
;


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
	static {
		// tell the UUID code to use the standard paths 
		// to find native library
		NativeInterfaces.setUseStdLibDir(true);
	}
	
	protected OTUUID(String id)
	{
		super(id);
	}
	
	public static OTID createOTUUID()
	{
		UUIDGenerator generator = UUIDGenerator.getInstance();
    	EthernetAddress hwAddress = NativeInterfaces.getPrimaryInterface();
    	
    	// FIXME: there has to be a better way to do this without
    	// making a throw away object.
    	UUID id = generator.generateTimeBasedUUID(hwAddress);
    	return new OTUUID(id.toString());
	}
}
