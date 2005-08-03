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
 * Created on Aug 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceCollection;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface OTDataObject 
{
	public OTID getGlobalId();
	
	public void setResource(String key, Object resource);
	public Object getResource(String key);
	public String [] getResourceKeys();
	
	/**
	 * This returns a collection of resources.  There are currently only 2
	 * classes that can be used here: OTResourceList and OTResourceMap
	 * 
	 * @param key
	 * @param collectionClass
	 * @return
	 */
	public OTResourceCollection getResourceCollection(String key, Class collectionClass);
	
	public OTObjectRevision getCurrentRevision();
	
	public OTDatabase getDatabase();
}
