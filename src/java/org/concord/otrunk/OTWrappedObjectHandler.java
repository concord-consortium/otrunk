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
 * Created on Mar 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk;

import java.lang.reflect.Method;

import org.concord.otrunk.datamodel.OTDataObject;


/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OTWrappedObjectHandler extends OTInvocationHandler
{
        
	public OTWrappedObjectHandler(OTDataObject dataObject, OTrunkImpl db)
	{
	    super(dataObject, db);
	}
	
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable
	{
	    String methodName = method.getName();
	
	    if(methodName.equals("getObject")){
	        // create the object this wraps
	        Class retClass = method.getReturnType();
	        Method [] methods = retClass.getMethods();
	        
	        Object retObject = retClass.newInstance();
	        
	        // set all the properties from the dataObject on this
	        String [] keys = dataObject.getResourceKeys();
	        for(int i=0; i<keys.length; i++){
	            Object value = dataObject.getResource(keys[i]);
	            String setMethodName = "set" + 
	            	keys[i].substring(0,1).toUpperCase() + 
	            	keys[i].substring(1,keys[i].length());
	            Method setMethod = null;
	            for(int j=0; j<methods.length; j++){
	                if(methods[j].getName().equals(setMethodName)){
	                    setMethod = methods[j];
	                }
	            }
	            
	            setMethod.invoke(retObject,new Object [] {value});
	        }
	        
	        // object
	        return retObject;
	    }
	    
	    if(methodName.equals("saveObject")){
	        // get all the properties from the objects
	        return null;
	    }
	    
	    return super.invoke(proxy, method, args);
	}	
}
