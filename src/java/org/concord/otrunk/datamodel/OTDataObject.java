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

import java.net.URL;

import org.concord.framework.otrunk.OTID;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface OTDataObject 
{
	public OTID getGlobalId();
	
	/**
	 * This should return true if the data object was
	 * changed with this call.   Most implementations check if the new 
	 * resource is differnt than the old resource, before setting the 
	 * resource.  So in that case it should return false if it decided
	 * not to set the resource.  This return value can be used to decide
	 * whether to notify listeners of a change.  The return value is used
	 * so an equality check doesn't have to be done twice.  
	 * 
	 * @param key
	 * @param resource
	 * @return
	 */
	public boolean setResource(String key, Object resource);
	
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
	public OTDataCollection getResourceCollection(String key, Class collectionClass);
	
	public OTObjectRevision getCurrentRevision();
	
	public OTDatabase getDatabase();
	
	public OTDataObjectType getType();
	
	/**
	 * There might be relative urls embedded in the content of this OTDataObject
	 * This method should return the url that these are relative too.  This can
	 * be used with the method: new URL(codebase, relative_url_string)  
	 * 
	 * This method can return null if there isn't anything appropriate to return.
	 * 
	 * @return
	 */
	public URL getCodebase();
}
