/**
 * 
 */
package org.concord.otrunk;

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
import org.concord.framework.otrunk.OTXMLString;
import org.concord.framework.otrunk.otcore.OTClassProperty;
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

			Class paramType = property.getType().getInstanceClass();
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
}
