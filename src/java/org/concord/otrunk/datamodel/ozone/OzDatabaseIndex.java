/*
 * Created on Aug 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.datamodel.ozone;

import org.concord.framework.otrunk.OTID;
import org.ozoneDB.OzoneRemote;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface OzDatabaseIndex extends OzoneRemote
{
	public OzDataObject put(OTID id, OzDataObject dataObject); /*update*/
	public OzDataObject get(OTID id);
	
	public void setRoot(OTID rootID);	/*update*/
	public OTID getRoot();
}
