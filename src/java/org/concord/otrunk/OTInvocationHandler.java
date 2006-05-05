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
 * $Revision: 1.15 $
 * $Date: 2006-05-05 16:00:32 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.otrunk.datamodel.DataObjectUtil;
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
	
	public OTInvocationHandler(OTDataObject dataObject)
	{
		this.dataObject = dataObject;
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
	    } else if(methodName.equals("copyInto")) {
            return copyInto(args[0]);
        }
	    
	    return null;
	}
    
    public Object copyInto(Object target)
    {
        // copy the dataObject
        if(!(target instanceof Proxy)){
            // error - should throw an exception here                
            return null;
        }
        
        // copy the dataObject
        InvocationHandler handler = 
            Proxy.getInvocationHandler(target);
        if(!(handler instanceof OTInvocationHandler)){
            // error - should throw an exception here                
            return null;                
        } 
     
        OTDataObject copyDataObject = 
            ((OTInvocationHandler)handler).dataObject;
        
        DataObjectUtil.copyInto(dataObject, copyDataObject);
        
        return null;        
    }
}
