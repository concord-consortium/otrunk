/*
 * Created on Aug 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.datamodel.ozone;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTResourceList;
import org.ozoneDB.OzoneRemote;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface OzResourceList extends OTResourceList, OzoneRemote
{
	public Object get(int index);
	
	public void add(Object object);/*update*/
	
	public void add(int index, Object object);/*update*/
	
	public OTObject getObject(int index);
	
	public void add(OTObject object);/*update*/
	
	public void add(int index, OTObject object);/*update*/
}
