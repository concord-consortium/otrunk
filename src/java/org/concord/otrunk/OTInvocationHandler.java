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
 * $Revision: 1.24 $
 * $Date: 2007-10-03 21:44:16 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;
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
	protected static HashMap<String, Method> internalMethodMap;
	 
	OTObjectInternal otObjectImpl;
	
    /**
     * @param dataObject
     * @param db
     */
    public OTInvocationHandler(OTObjectInternal otObjectImpl, OTrunkImpl db, Class<?> schemaInterface)
    {
    	this.otObjectImpl = otObjectImpl;
        
        if(internalMethodMap == null){
        	initializeInternalMethodMap();
        }
    }

	protected static void initializeInternalMethodMap()
    {
		internalMethodMap = new HashMap<String, Method>();
		
		Method [] interfaceMethods = OTObjectInterface.class.getMethods();
		
		for(int i=0; i<interfaceMethods.length; i++){
			Method interfaceMethod = interfaceMethods[i];
			
			try {
	            Method internalMethod = OTObjectInternal.class.getMethod(interfaceMethod.getName(),
	            		interfaceMethod.getParameterTypes());
	            internalMethodMap.put(internalMethod.getName(), internalMethod);
	            
	            // replace the internal methods
	            internalMethod = OTObjectInternal.class.getMethod("internalEquals", new Class []{Object.class});
	            internalMethodMap.put("equals", internalMethod);
	            
	            internalMethod = OTObjectInternal.class.getMethod("internalHashCode");
	            internalMethodMap.put("hashCode", internalMethod);

	            internalMethod = OTObjectInternal.class.getMethod("internalToString");
	            internalMethodMap.put("toString", internalMethod);
            } catch (SecurityException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            } catch (NoSuchMethodException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
            
            
		}
	    
    }

	public static String getResourceName(int prefixLen, String methodName)
	{
		String resourceName = methodName.substring(prefixLen, methodName.length());
		resourceName = resourceName.substring(0,1).toLowerCase() + 
			resourceName.substring(1,resourceName.length());
		return resourceName;
	}

    public void setEventSource(OTObject src)
    {
    	otObjectImpl.setEventSource(src);
    }

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable
	{
		// do a direct mapping to our internal object
		// we'll have to fix some of the special cases but most are going to just work
		
		String methodName = method.getName().intern();

		Method internalMethod = (Method) internalMethodMap.get(methodName);		
		if(internalMethod != null){
			return internalMethod.invoke(otObjectImpl, args);
		}
		
		// The return type is needed as a hint to the getResource method
		// This hint is needed to handle Blob resources which can be returned
		// as either urls or byte[].
		Class<?> returnType = method.getReturnType();
		
		if(methodName.startsWith("is")) {
            String resourceName = getResourceName(2, methodName);
            return otObjectImpl.getResource(resourceName, returnType);            
		} else if(methodName.startsWith("get")) {
			String resourceName = getResourceName(3, methodName).intern(); 
			return otObjectImpl.getResource(resourceName, returnType);
		} else if(methodName.startsWith("set")){
			String resourceName = getResourceName(3, methodName); 
			Object resourceValue = args[0];
			
			otObjectImpl.setResource(resourceName, resourceValue);
		} else if(methodName.startsWith("add")) {
			// String resourceName = getResourceName(3, methodName);
            (new Exception("Don't handle add yet")).printStackTrace();
            return null;
		} else if(methodName.startsWith("removeAll")) {
            (new Exception("Don't handle removeAll yet")).printStackTrace();
            return null;
		} else {
		    System.err.println("Unknown method \"" + methodName + "\" called on " + proxy.getClass());
		}
		return null;
	}
		
	/**
	 * Using data objects outside of the OTrunk project is discouraged.  They might
	 * change in the future so they shouldn't be depended upon.
	 * 
	 * @param otObject
	 * @return
	 */
	static OTDataObject getOTDataObject(OTObject otObject)
	{
		Proxy proxy = null;
		if(otObject instanceof DefaultOTObject){
			proxy = (Proxy) ((DefaultOTObject) otObject).otResourceSchema();
		} else if (otObject instanceof Proxy){
			proxy = (Proxy) otObject;
		} else {
			// this object isn't directly managed by us
			// A known example of this is OTXHTMLWrapperDoc which implements OTObject
			// and just wraps an object with a view so the main OTCompoundDoc code 
			// can display the object.  
			// In these cases fall back to the less efficient lookup approach
			OTObjectServiceImpl originalObjectService = (OTObjectServiceImpl) otObject.getOTObjectService();
			
			try {
	            return originalObjectService.getOTDataObject(otObject.getGlobalId());
            } catch (Exception e) {
            	e.printStackTrace();
            	return null;
            }				

		}
		
		OTInvocationHandler invocationHandler = 
			(OTInvocationHandler) Proxy.getInvocationHandler(proxy);
		return invocationHandler.otObjectImpl.dataObject;
	}

}
