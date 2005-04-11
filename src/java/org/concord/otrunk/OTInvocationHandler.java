
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
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
 */

/*
 * Last modification information:
 * $Revision: 1.12 $
 * $Date: 2005-04-11 15:01:08 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.datamodel.OTDataObject;


/**
 * OTInvocationHandler
 * Class name and description
 *
 * Date created: Nov 8, 2004
 *
 * @author scott<p>
 *
 */
public class OTInvocationHandler
	implements InvocationHandler
{
	OTDataObject dataObject;
	OTrunkImpl db;
	
	public OTInvocationHandler(OTDataObject dataObject, OTrunkImpl db)
	{
		this.dataObject = dataObject;
		this.db = db;
	}
	
	public static String getResourceName(int prefixLen, String methodName)
	{
		String resourceName = methodName.substring(prefixLen, methodName.length());
		resourceName = resourceName.substring(0,1).toLowerCase() + 
			resourceName.substring(1,resourceName.length());
		return resourceName;
	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable
	{
		String methodName = method.getName();
		
	    // Handle the globalId specially
	    if(methodName.equals("getGlobalId")) {
	        return dataObject.getGlobalId();
	    }
	    
	    return null;
	}
}
