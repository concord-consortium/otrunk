/**
 * 
 */
package org.concord.otrunk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import org.concord.framework.otrunk.OTObject;

/**
 * @author scott
 *
 */
public class OTrunkUtil {
	public static Object getPropertyValue(String propertyPath, Object root)
	   throws NoSuchMethodException
	{
		Object currentObject = root;
		StringTokenizer toks = new StringTokenizer(propertyPath, "/");
		while(toks.hasMoreTokens()){
			String propertyName = toks.nextToken();
			currentObject = 
				getNonPathPropertyValue(propertyName, currentObject);
		}
		return currentObject;
	}
	
	public final static Object getNonPathPropertyValue(String propertyName, Object obj) 
		throws NoSuchMethodException
	{
		// find the get or is method on the object with this name
		Class objClass = obj.getClass();
		String methodCase = propertyToMethodCase(propertyName);
		Method method = null;
		try {
			method = objClass.getMethod("get" + methodCase, null);
		} catch (NoSuchMethodException e) {
			// do nothing because we should try the is method
		}
		method = objClass.getMethod("is" + methodCase, null);
		try {
			return method.invoke(obj, null);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public final static void setNonPathPropertyValue(String propertyName, Object obj, 
			Object value) 
	throws NoSuchMethodException
	{
		// find the get or is method on the object with this name
		Class objClass = obj.getClass();
		String methodCase = propertyToMethodCase(propertyName);
		
		// because we don't have an easy way to figure out the correct
		// arguments for the set method, we'll just get them all 
		// and select the first one with a matching name.
		String methodName = "set" + methodCase;
		Method [] methods = objClass.getMethods();
		Method setMethod = null;
		for(int i=0; i<methods.length; i++){
			if(methods[i].getName().equals(methodName)){
				setMethod = methods[i];
				break;
			}
		}

		if(setMethod == null){
			throw new NoSuchMethodException("propertyName: " + propertyName);
		}
		
		try {
			setMethod.invoke(obj, new Object[]{value});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public final static String propertyToMethodCase(String property)
	{
		return property.substring(0,1).toUpperCase() + property.substring(1);
	}
	
	public static void setPropertyValue(String propertyPath, OTObject root, 
			Object value) throws NoSuchMethodException
	
	{
		Object currentObject = root;
		StringTokenizer toks = new StringTokenizer(propertyPath, "/");
		while(toks.hasMoreTokens()){
			String propertyName = toks.nextToken();
			if(toks.hasMoreTokens()){
				currentObject = 
					getNonPathPropertyValue(propertyName, currentObject);
				continue;
			} else {
				
			}
			
		}
	}

}
