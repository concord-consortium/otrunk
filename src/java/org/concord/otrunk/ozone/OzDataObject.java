/*
 * Created on Aug 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.ozone;


import org.concord.otrunk.OTDataObject;
import org.concord.otrunk.OTResourceCollection;
import org.ozoneDB.OzoneRemote;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface OzDataObject 
	extends OTDataObject, OzoneRemote 
{
	public void setResource(String name, Object resource); /*update*/
	public void generateID(); /*update*/
	public OTResourceCollection getResourceCollection(String key); /*update*/

}
