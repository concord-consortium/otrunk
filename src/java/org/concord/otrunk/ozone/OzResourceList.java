/*
 * Created on Aug 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.ozone;

import org.concord.otrunk.OTResourceList;
import org.ozoneDB.OzoneRemote;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface OzResourceList extends OTResourceList, OzoneRemote
{
	public void add(Object object);/*update*/
	
	public void add(int index, Object object);/*update*/
}
