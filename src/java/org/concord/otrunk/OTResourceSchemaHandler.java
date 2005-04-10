/*
 * Created on Mar 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.datamodel.OTDataObject;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OTResourceSchemaHandler extends OTInvocationHandler
{
    Class schemaInterface = null;
    
    /**
     * @param dataObject
     * @param db
     */
    public OTResourceSchemaHandler(OTDataObject dataObject, OTrunkImpl db, Class schemaInterface)
    {
        super(dataObject, db);
        this.schemaInterface = schemaInterface; 
        // TODO Auto-generated constructor stub
    }

	public Object handleGet(String resourceName, Class returnType, Class proxyClass)
	throws Exception
	{
	    // Handle the globalId specially
	    if(resourceName.equals("globalId")) {
	        return dataObject.getGlobalId();
	    }
	    
	    if(resourceName.equals("oTDatabase")) {
	        return db;
	    }
	    
	    Object resourceValue = dataObject.getResource(resourceName);
	    
	    // we can't rely on the returnType here because it could be an
	    // interface that isn't in the ot package
	    if(resourceValue instanceof OTID){
	        OTObject object;
	        try {
	            if(resourceValue == null) {
	                return null;
	            }
	            OTID objId = (OTID)resourceValue;
	            
	            object = (OTObject)db.getOTObject(dataObject, objId);
	            
	            return object;
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	        }		
	        
	        return null;
	        
	    } else if(OTResourceMap.class.isAssignableFrom(returnType)) {
	        try {					
	            OTResourceMap map = (OTResourceMap)dataObject.getResourceCollection(
	                    resourceName, OTResourceMap.class);
	            return map;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        return null;
	    } else if(OTObjectMap.class.isAssignableFrom(returnType)) {
	        try {					
	            OTResourceMap map = (OTResourceMap)dataObject.getResourceCollection(
	                    resourceName, OTResourceMap.class);
	            return new OTObjectMapImpl(map, dataObject, db);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        return null;				
	    } else if(OTResourceList.class.isAssignableFrom(returnType)) {
	        try {					
	            OTResourceList list = (OTResourceList)dataObject.getResourceCollection(
	                    resourceName, OTResourceList.class);
	            return list;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        return null;				
	    } else if(OTObjectList.class.isAssignableFrom(returnType)) {
	        try {					
	            OTResourceList list = (OTResourceList)dataObject.getResourceCollection(
	                    resourceName, OTResourceList.class);
	            return new OTObjectListImpl(list, dataObject, db);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        return null;				
	    } else if(resourceValue == null && returnType.isPrimitive()) {
	        try {
	            Field defaultField = proxyClass.getField("DEFAULT_" + resourceName);
	            if(defaultField != null) {
	                return defaultField.get(null);
	            }
	        } catch (NoSuchFieldException e) {
	            throw new RuntimeException("No default value set for \"" + resourceName + "\" " +
		                "in class: " + schemaInterface);
	        }
	    }
	    
	    if(resourceValue == null) return null;
	    
	    if(!returnType.isInstance(resourceValue) &&
	            !returnType.isPrimitive()){
	        System.err.println("invalid resource value for: " + resourceName);
	        System.err.println("  object type: " + dataObject.getResource(OTrunkImpl.RES_CLASS_NAME));
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
		if(methodName.equals("isResourceSet")) {
		    String resourceName = (String)args[0];
		    Object resourceValue = dataObject.getResource(resourceName);
		    return Boolean.valueOf(resourceValue != null);
		} else if(methodName.startsWith("get")) {
			String resourceName = getResourceName(3, methodName); 
			Class returnType = method.getReturnType();
			Class proxyClass = proxy.getClass();
			return handleGet(resourceName, returnType, proxyClass);
		} else if(methodName.startsWith("add")) {
			String resourceName = getResourceName(3, methodName); 
			System.err.println("Don't handle add yet");
			return null;
		} else if(methodName.startsWith("removeAll")) {
			System.err.println("Dont' handle remove All yet");
			return null;
		} else if(methodName.equals("toString")) {
			return dataObject.getResource(OTrunkImpl.RES_CLASS_NAME) + "@" +  dataObject.getGlobalId();
		} else if(methodName.equals("equals")) {
			Object other = args[0];
			if(!(other instanceof OTObject)){
				return Boolean.FALSE;
			}
			
			if(proxy == other) {
				return Boolean.TRUE;
			}
			
			if(((OTObject)other).getGlobalId().equals(dataObject.getGlobalId())) {
				System.err.println("compared two ot objects with the same ID but different instances");
				return Boolean.TRUE;
			}
			return Boolean.FALSE;

		} else if(methodName.startsWith("set")){
			String resourceName = getResourceName(3, methodName); 
			Object resourceValue = args[0];
			
			if(resourceValue instanceof OTObject) {
				OTObject child = (OTObject)resourceValue;
				OTID childId = child.getGlobalId();
				resourceValue = childId;
			} 
			dataObject.setResource(resourceName, resourceValue);			
		} else {
		    System.err.println("Unknown method \"" + methodName + "\" called on " + proxy.getClass());
		}
		return null;
	}
    
}
