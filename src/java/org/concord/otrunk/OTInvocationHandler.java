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
 * $Revision: 1.19 $
 * $Date: 2007-08-01 14:08:55 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;

import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.datamodel.BlobResource;
import org.concord.otrunk.view.OTViewerHelper;


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
	OTObjectInternal otObjectImpl;
	
	public final static boolean traceListeners = 
		OTViewerHelper.getBooleanProp(OTViewerHelper.TRACE_LISTENERS_PROP, false);
	
	Class schemaInterface = null;
    OTrunkImpl db;
    OTObjectService objectService;
    	
    /**
     * @param dataObject
     * @param db
     */
    public OTInvocationHandler(OTObjectInternal otObjectImpl, OTrunkImpl db, Class schemaInterface)
    {
    	this.otObjectImpl = otObjectImpl;
        this.schemaInterface = schemaInterface; 
        this.db = db;
    }

	
	public static String getResourceName(int prefixLen, String methodName)
	{
		String resourceName = methodName.substring(prefixLen, methodName.length());
		resourceName = resourceName.substring(0,1).toLowerCase() + 
			resourceName.substring(1,resourceName.length());
		return resourceName;
	}

    public Object copyInto(Object target)
    {
        // copy the dataObject
        if(!(target instanceof Proxy)){
            // error - should throw an exception here                
            return null;
        }
        
        // get the dataObject from the handler
        InvocationHandler handler = 
            Proxy.getInvocationHandler(target);
        if(!(handler instanceof OTInvocationHandler)){
            // error - should throw an exception here                
            return null;                
        } 
     
        OTObjectInternal copyObjectImpl = 
        	((OTInvocationHandler)handler).otObjectImpl;

        otObjectImpl.copyInto(copyObjectImpl);
                
        return null;        
    }

    public void setEventSource(OTObject src)
    {
    	otObjectImpl.setEventSource(src);
    }

	protected Object getResource(String resourceName, Class returnType, Class proxyClass)
	throws Exception
	{
	    // Handle the globalId specially
	    if(resourceName.equals("globalId")) {
	    	return otObjectImpl.getGlobalId();
	    }
	    
        if(resourceName.equals("oTObjectService")) {
        	return otObjectImpl.getOTObjectService();
        }
        
        // If this class is the one handling the overlays then this call
        // would be substituted by one that goes through all of the overlayed data objects.
	    Object resourceValue = otObjectImpl.getResourceValue(resourceName);
	    
	    // we can't rely on the returnType here because it could be an
	    // interface that isn't in the ot package
	    if(resourceValue instanceof OTID){
	        OTObject object;
	        try {
	            if(resourceValue == null) {
	                return null;
	            }
	            OTID objId = (OTID)resourceValue;
	            
	            object = otObjectImpl.getOTObject(objId);
	            
	            if(object != null){
	            	if(!returnType.isAssignableFrom(object.getClass())){
	            		System.err.println("Error: Type Mismatch");
	            		System.err.println("  value: " + object);
	            		System.err.println("  parentObject: " + schemaInterface.toString());
	            		System.err.println("  resourceName: " + resourceName);
	        	        System.err.println("  expected type is: " + returnType);
	            		return null;
	            	}
	            }
	            
	            return object;
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	        }		
	        
	        return null;
	        
	    } else if(OTResourceMap.class.isAssignableFrom(returnType)) {
	        try {
	        	return otObjectImpl.getResourceMap(resourceName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        return null;
	    } else if(OTObjectMap.class.isAssignableFrom(returnType)) {
	        try {
	        	return otObjectImpl.getObjectMap(resourceName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        return null;				
	    } else if(OTResourceList.class.isAssignableFrom(returnType)) {
	        try {
	        	return otObjectImpl.getResourceList(resourceName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        return null;				
	    } else if(OTObjectList.class.isAssignableFrom(returnType)) {
	        try {					
	        	return otObjectImpl.getObjectList(resourceName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        return null;	
	    } else if(resourceValue instanceof BlobResource) {
	    	BlobResource blob = (BlobResource)resourceValue;
	    	if(returnType == byte[].class){
	    		return blob.getBytes();
	    	} else if(returnType == URL.class){
	    		return blob.getBlobURL();
	    	}
	    } else if(resourceValue == null && 
	    		(returnType == String.class || returnType.isPrimitive())) {
	        try {
	            Field defaultField = proxyClass.getField("DEFAULT_" + resourceName);
	            if(defaultField != null) {
	                return defaultField.get(null);
	            }
	        } catch (NoSuchFieldException e) {
	        	// It is normal to have undefined strings so we shouldn't throw an
	        	// exception in that case.
	        	if(returnType != String.class){
	        		throw new RuntimeException("No default value set for \"" + resourceName + "\" " +
	        				"in class: " + schemaInterface);
	        	}
	        }
	    }
	    
	    if(resourceValue == null) return null;
	    
	    if(!returnType.isInstance(resourceValue) &&
	            !returnType.isPrimitive()){
	        System.err.println("invalid resource value for: " + resourceName);
	        System.err.println("  object type: " + schemaInterface.toString());
	        System.err.println("  resourceValue is: " + resourceValue.getClass());
	        System.err.println("  expected type is: " + returnType);
	        return null;
	    }
	    
	    return resourceValue;
	    
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable
	{
		String methodName = method.getName();

		if(methodName.equals("addOTChangeListener")) {
			otObjectImpl.addOTChangeListener((OTChangeListener)args[0]);
		    return null;
		}
		
		if(methodName.equals("removeOTChangeListener")) {
			otObjectImpl.removeOTChangeListener((OTChangeListener)args[0]);
		    return null;
		}
		
		if(methodName.equals("setDoNotifyChangeListeners")) {
			otObjectImpl.setDoNotifyListeners(((Boolean)args[0]).booleanValue());
		    return null;
		}

		if(methodName.equals("notifyOTChange")) {
		    otObjectImpl.notifyOTChange((String)args[0], (String)args[1], args[2]);
		    return null;
		}


		if(methodName.equals("isResourceSet")) {
		    String resourceName = (String)args[0];
		    boolean resourceSet = otObjectImpl.isResourceSet(resourceName);
		    return Boolean.valueOf(resourceSet);
        }  else if(methodName.startsWith("is")) {
            String resourceName = getResourceName(2, methodName); 
            Class returnType = method.getReturnType();
            Class proxyClass = proxy.getClass();
            return getResource(resourceName, returnType, proxyClass);            
		} else if(methodName.startsWith("get")) {
			String resourceName = getResourceName(3, methodName); 
			Class returnType = method.getReturnType();
			Class proxyClass = proxy.getClass();
			return getResource(resourceName, returnType, proxyClass);
		} else if(methodName.startsWith("add")) {
			// String resourceName = getResourceName(3, methodName);
            (new Exception("Don't handle add yet")).printStackTrace();
            return null;
		} else if(methodName.startsWith("removeAll")) {
            (new Exception("Don't handle removeAll yet")).printStackTrace();
            return null;
		} else if(methodName.equals("toString")) {
			return otObjectImpl.getOTClassName();
		} else if(methodName.equals("hashCode")) {
			String str = otObjectImpl.getOTClassName() + "@" +  otObjectImpl.getGlobalId();
			Integer integer = new Integer(str.hashCode()); 
			return integer;
		} else if(methodName.equals("equals")) {
			Object other = args[0];
			if(!(other instanceof OTObject)){
				return Boolean.FALSE;
			}
			
			if(proxy == other) {
				return Boolean.TRUE;
			}
			
			if(((OTObject)other).getGlobalId().equals(otObjectImpl.getGlobalId())) {
				System.err.println("compared two ot objects with the same ID but different instances");
				return Boolean.TRUE;
			}
			return Boolean.FALSE;

		} else if(methodName.startsWith("set")){
			String resourceName = getResourceName(3, methodName); 
			Object resourceValue = args[0];
			
			otObjectImpl.setResource(resourceName, resourceValue);
		} else {
		    System.err.println("Unknown method \"" + methodName + "\" called on " + proxy.getClass());
		}
		return null;
	}
    
    public void notifyOTChange(String property, String operation, 
    		Object value)
    {
    	otObjectImpl.notifyOTChange(property, operation, value);    	
    }

}
