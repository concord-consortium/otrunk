/*
 * Created on Aug 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.datamodel.ozone;

import org.doomdark.uuid.UUID;
import org.ozoneDB.OzoneRemote;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface OzDatabaseIndex extends OzoneRemote
{
	public void put(UUID id, OzDataObject dataObject); /*update*/
	public OzDataObject get(UUID id);
	
	public void setRoot(UUID rootID);	/*update*/
	public UUID getRoot();
}
