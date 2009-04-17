/**
 * 
 */
package org.concord.otrunk;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.framework.otrunk.OTXMLString;
import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.concord.otrunk.datamodel.BlobResource;

/**
 * @author scott
 *
 */
public class OTrunkUtil 
{
	private static final Logger logger = Logger.getLogger(OTrunkUtil.class.getCanonicalName());

	/**
	 * 
	 * @param propertyPath
	 * @param root
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Object getPropertyValue(String propertyPath, OTObject root)
	   throws NoSuchMethodException
	{
		Object currentObject = root;
		StringTokenizer toks = new StringTokenizer(propertyPath, "/");
		String currentProperty = null;
		while(toks.hasMoreTokens()){
			if(!(currentObject instanceof OTObject)){
				throw new RuntimeException("Invalid path: " + propertyPath + 
						" element: " + currentProperty + " did not return an OTObject");
			}
			
			currentProperty = toks.nextToken();
			currentObject = 
				getNonPathPropertyValue(currentProperty, (OTObject) currentObject);
		}
		return currentObject;
	}
	
	public final static Object getNonPathPropertyValue(String propertyName, OTObject obj) 
		throws NoSuchMethodException
	{
		// find the get or is method on the object with this name
		
		if(propertyName.endsWith("]")){
			Pattern arrayPattern = Pattern.compile("(.*)\\[(\\d*)\\]");
			Matcher m = arrayPattern.matcher(propertyName);
			if(m.matches()){
				propertyName = m.group(1);
				String indexStr = m.group(2);
				
				OTClassProperty classProperty = obj.otClass().getProperty(propertyName);
				if(classProperty == null){
					throw new RuntimeException("Property: " + propertyName + 
							" doesn't exist on object: " + obj);
				}
				Object list = obj.otGet(classProperty);
												
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

			OTClassProperty classProperty = obj.otClass().getProperty(propertyName);
			if(classProperty == null){
				throw new RuntimeException("Property: " + propertyName + 
						" doesn't exist on object: " + obj);
			}
			return obj.otGet(classProperty);
		}
		
		return null;
	}

	public final static void setNonPathPropertyValue(String propertyName, OTObject obj, 
			Object value) 
	throws NoSuchMethodException
	{
		// check if this is an array reference
		if(propertyName.endsWith("]")){
			if(value == null){
				System.err.println("cannot store null values in a list");
				return;
			}
			
			Pattern arrayPattern = Pattern.compile("(.*)\\[(\\d*)\\]");
			Matcher m = arrayPattern.matcher(propertyName);
			if(m.matches()){
				propertyName = m.group(1);
				String indexStr = m.group(2);
				
				// get the ObjectList
				// call set with this index
				OTClassProperty property = obj.otClass().getProperty(propertyName);
				
				Object list = obj.otGet(property);
												
				// remove the old one 
				int index = Integer.parseInt(indexStr);
				
				if(list instanceof OTObjectList){
					OTObjectList objList = (OTObjectList) list;
					objList.set(index, (OTObject)value);
				} else if(list instanceof OTResourceList){
					OTObjectList objList = (OTObjectList) list;
					if(objList.size() >= (index+1)){
						objList.remove(index);
					}
					objList.add(index, (OTObject)value);					
				}
			}
		} else {

			OTClassProperty property = obj.otClass().getProperty(propertyName);

			if(property == null){
				throw new NoSuchMethodException("propertyName: " + propertyName + 
						" newValue: " + value);
			}

			Class<?> paramType = property.getType().getInstanceClass();
			if(value instanceof String){
				String valueStr = (String) value;
				if(paramType == Float.class){
					value = Float.valueOf(valueStr);
				} else if(paramType == Integer.class){
					value = Integer.valueOf(valueStr);
				} else if(paramType == Boolean.class){
					value = Boolean.valueOf(valueStr);
				} else if(paramType == Double.class){
					value = Double.valueOf(valueStr);
				} else if(paramType == Long.class){
					value = Long.valueOf(valueStr);
				} else if(paramType == Short.class){
					value = Short.valueOf(valueStr);
				} else if(paramType == BlobResource.class){
					try {
						value = new URL(valueStr);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				} else if(paramType == OTXMLString.class){
					value = new OTXMLString(valueStr);
				}				
			}
			
			if(paramType == String.class){
				if(value == null){
					// leave it alone so the param is set to null
				} else if(value.getClass().isPrimitive() ||
						value instanceof Number ||
						value instanceof Boolean){
					value = "" + value;
				} else if(value instanceof OTXMLString){
					value = ((OTXMLString)value).getContent();
				}
				
			}
			
			obj.otSet(property, value);			
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
		String currentProperty = null;
		while(toks.hasMoreTokens()){
			if(!(currentObject instanceof OTObject)){
				throw new RuntimeException("Invalid path: " + propertyPath + 
						" element: " + currentProperty + " did not return an OTObject");
			}
			

			currentProperty = toks.nextToken();
			if(toks.hasMoreTokens()){
				currentObject = 
					getNonPathPropertyValue(currentProperty, (OTObject) currentObject);
			} else {
				setNonPathPropertyValue(currentProperty, (OTObject) currentObject, value);				
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
		
		Vector<String> keys = map.getObjectKeys();
		for(int i=0; i<keys.size(); i++) {
		    String currentKey = keys.get(i);
		    
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
	
	public static String escapeReplacement(String replacement) {
    	if (replacement == null) {
    		return null;
    	}
    
    	// escape $ and \ incase these are used in the text
    	// we need 8 backslashes here because
    	// first java compiler strips off half so it is now
    	// "\\\\"
    	// then regex replacer strips off half so it is now
    	// "\\"
    	// and that is what we want in the replacement so the
    	// the next replacer turns it into a "\" again. :)
    	replacement = replacement.replaceAll("\\\\", "\\\\\\\\");
    
    	// We need 6 backslashes because
    	// first the java compiler strips off half of them so the sting
    	// becomes: \\\$
    	// then the replacer uses the backslash as a quote, and the $
    	// character is used to reference groups of characters, so it
    	// must be escaped. So the 1st two are turned into one, and the
    	// 3rd one escapes the $. So the end result is:
    	// \$
    	// We need this \$ because the replacement below is going to
    	// parse the $ otherwise
    	replacement = replacement.replaceAll("\\$", "\\\\\\$");
    
    	return replacement;
    }
	
	public static void printObject(OTObject otObject)
	{
		OTClass otClass = otObject.otClass();
		 ArrayList<OTClassProperty> allClassProperties = otClass.getOTAllClassProperties();
		for(int i=0; i<allClassProperties.size(); i++){
			OTClassProperty property = allClassProperties.get(i);
			Object value = otObject.otGet(property);
			System.out.println("  " + property.getName() + "=" + value);
		}		
	}
	
	
	/**
	 * Compare the content of the 2 objects.  This isn't complete, it will probably return
	 * false in some cases when the objects are the same.  So use with caution.
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static boolean compareObjects(OTObject obj1, OTObject obj2)
	{	
		return compareObjects(obj1, obj2, false);
	}
	
	public static boolean compareObjects(OTObject obj1, OTObject obj2, boolean compareXMLStrings)
	{
		OTClass otClass = obj1.otClass();
		if(!otClass.equals(obj2.otClass())){
			logger.fine("Object classes don't match: " + otClass + " != " + obj2.otClass());
			return false;
		}
		
		ArrayList<OTClassProperty> allClassProperties = otClass.getOTAllClassProperties();
		for(int i=0; i<allClassProperties.size(); i++){
			OTClassProperty property = allClassProperties.get(i);

			// skip local id properties
			if("localId".equals(property.getName())){
				continue;
			}
			
			boolean isSet = obj1.otIsSet(property);
			if(isSet != obj2.otIsSet(property)){
				logFiner(property, (isSet ? "is" : "is not") + " set on obj1, but " + (isSet ? "is not" : "is") + " set on obj2");
				return false;
			}
			
			if(!isSet){
				continue;
			}
			
			// skip otxml strings because the will have changed and we
			// need special parsing code to handle them
			if(property.getType().getInstanceClass() == OTXMLString.class ){
				if (! compareXMLStrings) {
					continue;
				}
			}
			
			Object value1 = obj1.otGet(property);
			Object value2 = obj2.otGet(property);

			if(value1 instanceof OTObject && value2 instanceof OTObject){
				if(!compareObjects((OTObject)value1, (OTObject)value2)){
					logFiner(property, "Child objects are not the same");
					return false;
				}
			} else if (value1 instanceof OTResourceList){
				OTResourceList list1 = (OTResourceList) value1;
				OTResourceList list2 = (OTResourceList) value2;
				if(list1.size() != list2.size()){
					logFiner(property, "resource lists have different sizes -- " + list1.size() + " != " + list2.size());
					return false;
				}
				
				for(int j=0; j<list1.size(); j++){
					if(!list1.get(j).equals(list2.get(j))){
						logFiner(property, "resource list item " + j + " is not the same: '" + list1.get(j) + "' != '" + list2.get(j) + "'");
						return false;
					}
				}
				
			} else if (value1 instanceof OTObjectList){
				OTObjectList list1 = (OTObjectList) value1;
				OTObjectList list2 = (OTObjectList) value2;
				if(list1.size() != list2.size()){
					logFiner(property, "object lists have different sizes -- " + list1.size() + " != " + list2.size());
					return false;
				}
				
				for(int j=0; j<list1.size(); j++){
					if(!compareObjects(list1.get(j), list2.get(j))){
						logFiner(property, "object list item " + j + " is not the same");
						return false;
					}
				}
				
			} else if (value1 instanceof OTResourceMap){
				OTResourceMap map1 = (OTResourceMap) value1;
				OTResourceMap map2 = (OTResourceMap) value2;
				if(map1.size() != map2.size()){
					logFiner(property, "resource maps have different sizes -- " + map1.size() + " != " + map2.size());
					return false;
				}
				
				String[] objectKeys = map1.getKeys();
				for(int j=0; j<objectKeys.length; j++){
					String key = objectKeys[j];
					if(! map1.get(key).equals(map2.get(key))){
						logFiner(property, "resource map item with key '" + key + "' is not the same: '" + map1.get(key) + "' != '" + map2.get(key) + "'");
						return false;
					}
					
				}				
			} else if (value1 instanceof OTObjectMap){
				OTObjectMap map1 = (OTObjectMap) value1;
				OTObjectMap map2 = (OTObjectMap) value2;
				if(map1.size() != map2.size()){
					logFiner(property, "object maps have different sizes -- " + map1.size() + " != " + map2.size());
					return false;
				}
				
				Vector<String> objectKeys = map1.getObjectKeys();
				for(int j=0; j<objectKeys.size(); j++){
					String key = objectKeys.get(j);
					if(!compareObjects(map1.getObject(key), map2.getObject(key))){
						logFiner(property, "object map item with key '" + key + "' is not the same");
						return false;
					}
					
				}				
			} else if(value1 instanceof BlobResource && value2 instanceof BlobResource){
				BlobResource blob1 = (BlobResource) value1;
				BlobResource blob2 = (BlobResource) value2;
				byte[] bytes1 = blob1.getBytes();
				byte[] bytes2 = blob2.getBytes();
				
				if(bytes1.length != bytes2.length){
					logFiner(property, "blobs have different byte[] lengths");
					return false;
				}
				
				for(int j=0;j<bytes1.length;j++){
					if(bytes1[j] != bytes2[j]){
						logFiner(property, "byte at index " + j + " doesn't match: " + bytes1[j] + " != " + bytes2[j]);
						return false;
					}
				}
			} else if (value1 instanceof OTXMLString) {
				
				// FIXME parse for object references and be sure to compare those objects
				String string1 = ((OTXMLString) value1).getContent();
				String string2 = ((OTXMLString) value1).getContent();
				
				// ignore whitespace
				string1 = string1.replaceAll("[ ]+", " ");
				string2 = string2.replaceAll("[ ]+", " ");
				
				if (!string1.equals(string2)) {
					logFiner(property, "xmls strings don't match: '" + string1 + "' != '" + string2 + "'");
				}
			} else {
				if(!value1.equals(value2)){
					logFiner(property, "values don't match: '" + value1 + "' != '" + value2 + "'");
					return false;
				}
			}
		}
		return true;
	}
	
	private static void logFiner(OTClassProperty property, String msg) {
		logger.finer("'" + property.getName() + "': " + msg);
	}
	
	public static boolean listEquals(OTObjectList list1, OTObjectList list2)
	{
		if(list1.size() != list2.size()){
			return false;
		}
		for(int j=0; j<list1.size(); j++){
			if(list1.get(j) == null){
				if(list2.get(j) == null){
					continue;
				} else {
					return false;
				}
			}
			
			if(!list1.get(j).equals(list2.get(j))){
				return false;
			}
		}
		return true;
	}
}
