/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2005-03-31 21:07:26 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.lang.reflect.Method;

import org.concord.otrunk.datamodel.OTDataObject;


/**
 * OTBasicObjectHandler
 * Class name and description
 *
 * Date created: Nov 9, 2004
 *
 * @author scott<p>
 *
 */
public class OTBasicObjectHandler extends OTResourceSchemaHandler
{
	public OTBasicObjectHandler(OTDataObject dataObject, OTrunkImpl db)
	{
		super(dataObject, db);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable
	{
		String methodName = method.getName();
		
		if(methodName.equals("setOTDatabase")) {
			throw new RuntimeException("shouldn't be calling setDataObject");
		}
		
		// skip the init call if this is a basic object that is being proxied
		if(methodName.equals("init")) {
			return null;
		}
		
		return super.invoke(proxy, method, args);
	}
}
