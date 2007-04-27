/**
 * 
 */
package org.concord.otrunk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.otrunk.datamodel.BlobResource;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;

/**
 * @author scott
 *
 */
public class OTrunkUtil 
{

	/**
	 * This should be redone to only work with OTObjects, and it should
	 * use the data model under the OTObject, instead of reflection on the 
	 * object itself.
	 * 
	 * @param propertyPath
	 * @param root
	 * @return
	 * @throws NoSuchMethodException
	 */
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
		
		if(propertyName.endsWith("]")){
			Pattern arrayPattern = Pattern.compile("(.*)\\[(\\d*)\\]");
			Matcher m = arrayPattern.matcher(methodCase);
			if(m.matches()){
				methodCase = m.group(1);
				String indexStr = m.group(2);
				
				// get the ObjectList
				// call set with this index
				String methodName = "get" + methodCase;

				Method getListMethod = objClass.getMethod(methodName, null);
				Object list = null;
				try {
					list = getListMethod.invoke(obj, null);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
								
				// remove the old one 
				int index = Integer.parseInt(indexStr);
				
				if(list instanceof OTObjectList){
					OTObjectList objList = (OTObjectList) list;
					return objList.get(index);
				} else if(list instanceof OTResourceList){
					OTResourceList resList = (OTResourceList) list;
					return resList.get(index);
				}
			}
		} else {

			Method method = null;
			try {
				method = objClass.getMethod("get" + methodCase, null);
			} catch (NoSuchMethodException e) {
				// do nothing because we should try the is method
				method = objClass.getMethod("is" + methodCase, null);
			}

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
		Method setMethod = null;
		Object [] params = null;

		// check if this is an array reference
		if(propertyName.endsWith("]")){
			if(value == null){
				System.err.println("cannot store null values in a list");
				return;
			}
			
			Pattern arrayPattern = Pattern.compile("(.*)\\[(\\d*)\\]");
			Matcher m = arrayPattern.matcher(methodCase);
			if(m.matches()){
				methodCase = m.group(1);
				String indexStr = m.group(2);
				
				// get the ObjectList
				// call set with this index
				String methodName = "get" + methodCase;

				Method getListMethod = objClass.getMethod(methodName, null);
				Object list = null;
				try {
					list = getListMethod.invoke(obj, null);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
								
				// remove the old one 
				int index = Integer.parseInt(indexStr);
				
				if(list instanceof OTObjectList){
					OTObjectList objList = (OTObjectList) list;
					if(objList.size() >= (index+1)){
						objList.remove(index);
					}					
					objList.add(index, (OTObject)value);
				} else if(list instanceof OTResourceList){
					OTObjectList objList = (OTObjectList) list;
					if(objList.size() >= (index+1)){
						objList.remove(index);
					}
					objList.add(index, (OTObject)value);					
				}
			}
		} else {

			// because we don't have an easy way to figure out the correct
			// arguments for the set method, we'll just get them all 
			// and select the first one with a matching name.
			String methodName = "set" + methodCase;
			Method [] methods = objClass.getMethods();
			for(int i=0; i<methods.length; i++){
				if(methods[i].getName().equals(methodName)){
					setMethod = methods[i];
					break;
				}
			}

			if(setMethod == null){
				throw new NoSuchMethodException("propertyName: " + propertyName);
			}
			
			Class paramType = setMethod.getParameterTypes()[0];
			if(value instanceof String){
				String valueStr = (String) value;
				if(paramType == Float.class ||
						paramType == Float.TYPE){
					value = Float.valueOf(valueStr);
				} else if(paramType == Integer.class ||
						paramType == Integer.TYPE){
					value = Integer.valueOf(valueStr);
				} else if(paramType == Boolean.class ||
						paramType == Boolean.TYPE){
					value = Boolean.valueOf(valueStr);
				} else if(paramType == Double.class ||
						paramType == Double.TYPE){
					value = Double.valueOf(valueStr);
				} else if(paramType == Long.class ||
						paramType == Long.TYPE){
					value = Long.valueOf(valueStr);
				} else if(paramType == Short.class ||
						paramType == Short.TYPE){
					value = Short.valueOf(valueStr);
				} else if(paramType == URL.class){
					try {
						value = new URL(valueStr);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				} else if(paramType == byte[].class){
					try {
						URL url = new URL(valueStr);
						setBlobUrl((OTObject)obj, propertyName, url);
						return;
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
			
			if(paramType == String.class &&
					(value.getClass().isPrimitive() ||
							Number.class.isAssignableFrom(value.getClass())))
			{
				value = "" + value;
			}
			
			params = new Object[]{value};
			try {
				setMethod.invoke(obj, params);
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
			} else {
				setNonPathPropertyValue(propertyName, currentObject, value);				
			}		
		}
	}

	
	
	/**
	 * This method is needed if you use an object map which uses object ids for
	 * its keys.  This is useful if you want to look up one object using another
	 * object.  You cannot simply do 
	 * OTObject valueObject = map.getObject(keyObject.getGlobalId().toString());
	 * 
	 * because when running in user mode the keyObject id will not be the same
	 * as what is in the map.  It is wrapped with a template/user object so 
	 * changes to it can be saved correctly.
	 * 
	 * @param map
	 * @param keyObject
	 * @return
	 */
	public static OTObject getObjectFromMapWithIdKeys(
			OTObjectMap map, OTObject keyObject)
	
	{
		OTObjectService objectService = keyObject.getOTObjectService();
		
		Vector keys = map.getObjectKeys();
		for(int i=0; i<keys.size(); i++) {
		    String currentKey = (String)keys.get(i);
		    
		    OTID currentKeyId = objectService.getOTID(currentKey);
		    try {
		    	OTObject currentKeyObject = 
		    		objectService.getOTObject(currentKeyId); 

		    	if(currentKeyObject == keyObject) {
		    		return map.getObject(currentKey);
		    	}
		    } catch (Exception e){
		    	e.printStackTrace();
		    }
		}

		return null;
	}
	
	private static OTDataObject getDataObject(OTObject object)
		throws Exception
	{
		OTID id = object.getGlobalId();
		OTObjectServiceImpl objServiceImpl = 
			(OTObjectServiceImpl)object.getOTObjectService();

		OTDataObject dataObject = objServiceImpl.mainDb.getOTDataObject(null, id);
		if(dataObject == null && objServiceImpl.creationDb != null){
			dataObject = objServiceImpl.creationDb.getOTDataObject(null, id);
		}
		
		return dataObject;
	}
	
	public static void setBlobUrl(OTObject object, String propertyName, URL url)
	{
		// This is a hack because there isn't a better way yet
		OTDataObject dataObject = null;
		try {
	        dataObject = getDataObject(object);
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		
        OTDatabase db = dataObject.getDatabase();
        BlobResource blobRes = db.createBlobResource(url);
        dataObject.setResource(propertyName, blobRes);        
	}
}
