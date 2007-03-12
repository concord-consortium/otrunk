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
 * $Revision: 1.12 $
 * $Date: 2007-03-12 20:59:31 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.lang.reflect.Method;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
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
    OTObject otObject;
    
	public OTBasicObjectHandler(OTDataObject dataObject, OTrunkImpl db, 
            OTObjectService objectService, Class objectInterface)
	{
		super(dataObject, db, objectService, objectInterface);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable
	{
	    if(otObject == null) {
	        otObject = (OTObject)proxy;
	        setEventSource(otObject);
	    }
	    
	    if(otObject != proxy) {
	        throw new RuntimeException("Trying to use the same handler for 2 proxy objects");
	    }
	    
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
