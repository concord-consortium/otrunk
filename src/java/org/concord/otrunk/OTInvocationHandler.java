/*
 * Last modification information:
 * $Revision: 1.11 $
 * $Date: 2005-03-31 21:07:26 $
 * $Author: scytacki $
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
