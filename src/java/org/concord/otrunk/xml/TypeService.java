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
 * $Revision: 1.28 $
 * $Date: 2007-10-10 03:09:05 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.framework.otrunk.OTXMLString;
import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTEnum;
import org.concord.framework.otrunk.otcore.OTType;
import org.concord.otrunk.datamodel.BlobResource;
import org.concord.otrunk.datamodel.OTDataList;
import org.concord.otrunk.datamodel.OTDataMap;
import org.concord.otrunk.otcore.impl.OTCorePackage;

/**
 * DOTypeService
 * Class name and description
 *
 * Date created: Oct 5, 2004
 *
 * @author scott<p>
 *
 */
public class TypeService
{
	private static final Logger logger = Logger.getLogger(TypeService.class.getCanonicalName());
	
    public final static String STRING = "string";
    public final static String XML_STRING = "xmlstring";
    public final static String BOOLEAN = "boolean";
    public final static String INTEGER = "int";
    public final static String LONG = "long";
    public final static String FLOAT = "float";
    public final static String DOUBLE = "double";
    public final static String BLOB = "blob";
    public final static String LIST = "list";
    public final static String MAP = "map";
    public final static String ENUM = "enum";
    public final static String OBJECT = "object";

    /**
     * These types are the same for OTObjects and OTDataObjects
     * 
     * @param klass
     * @return
     */
	public static String getPrimitiveType(Class<?> klass)
	{
		if(String.class.isAssignableFrom(klass)) {
			return STRING;
		} else if(OTXMLString.class.isAssignableFrom(klass)) {
		    return XML_STRING;
		} else if(Boolean.class.isAssignableFrom(klass) ||
				Boolean.TYPE.equals(klass)) {
			return BOOLEAN;
		} else if(Integer.class.isAssignableFrom(klass) ||
				Integer.TYPE.equals(klass)) {
			return INTEGER;
		} else if(Long.class.isAssignableFrom(klass) ||
				Long.TYPE.equals(klass)) {
			return LONG;
		} else if(Float.class.isAssignableFrom(klass) ||
				Float.TYPE.equals(klass)) {
			return FLOAT;
		} else if(Double.class.isAssignableFrom(klass) ||
				Double.TYPE.equals(klass)) {
			return DOUBLE;
		} 
	
		return null;
	}
	
	/**
	 * This will return the type of the allowable classes or interfaces for
	 * OTObjectS
	 * 
	 * @param klass
	 * @return
	 */
	public static String getObjectPrimitiveType(Class<?> klass)
	{
		String type = getPrimitiveType(klass);
		
		if(type != null){
			return type;
		}

		if(klass.isArray() && 
				klass.getComponentType().equals(Byte.TYPE)) {
			return BLOB;
		} else if(URL.class.isAssignableFrom(klass)){
			return BLOB;
		} else if(OTResourceList.class.isAssignableFrom(klass) ||
				OTObjectList.class.isAssignableFrom(klass)) {
			return LIST;
		} else if(OTResourceMap.class.isAssignableFrom(klass) ||
				OTObjectMap.class.isAssignableFrom(klass)) {
			return MAP;
		} else if(OTObject.class.isAssignableFrom(klass) ) {
			// OTIDs used to be allowed here.
			// If an OTID is the type of a parameter, I think the code which
			// translates these will get messed up.  So they are not allowed
			// now
			return OBJECT;
		}
		return null;
	}

	/**
	 * This will return the type of the allowable classes or interfaces for
	 * resources in OTDataObjectS
	 * 
	 * @param klass
	 * @return
	 */
	public static String getDataPrimitiveType(Class<?> klass)
	{
		String type = getPrimitiveType(klass);
		
		if(type != null){
			return type;
		}

		if(BlobResource.class.isAssignableFrom(klass)) {
			return BLOB;
		} else if(OTDataList.class.isAssignableFrom(klass)) {
			return LIST;
		} else if(OTDataMap.class.isAssignableFrom(klass)) {
			return MAP;
		} else if(OTID.class.isAssignableFrom(klass) ) {
			return OBJECT;
		}
		
		return null;
	}
	
	HashMap<String, ResourceTypeHandler> handlerMap = new HashMap<String, ResourceTypeHandler>();
	private HashMap<String, OTClass> shortcutMap = new HashMap<String, OTClass>();
	private HashMap<OTType, ResourceTypeHandler> handlerByOTTypeMap = 
		new HashMap<OTType, ResourceTypeHandler>();

	public TypeService(URL contextURL)
	{	
		// Remember the indexes matter because of the list down below
		ResourceTypeHandler [] handlers = {
				new BooleanTypeHandler(),
				new IntegerTypeHandler(),
				new LongTypeHandler(),
				new FloatTypeHandler(),
				new DoubleTypeHandler(),
				new StringTypeHandler(),
				new XMLStringTypeHandler(),
				new BlobTypeHandler(contextURL),
				new ListTypeHandler(this),
				new MapTypeHandler(this),	
				new NullHandler(),
		};
		
		for(int i=0; i<handlers.length; i++){
			handlerMap.put(handlers[i].getPrimitiveName(), handlers[i]);
		}
		
		handlerByOTTypeMap.put(OTCorePackage.BOOLEAN_TYPE,       handlers[0]);
		handlerByOTTypeMap.put(OTCorePackage.INTEGER_TYPE,       handlers[1]);
		handlerByOTTypeMap.put(OTCorePackage.LONG_TYPE,          handlers[2]);
		handlerByOTTypeMap.put(OTCorePackage.FLOAT_TYPE,         handlers[3]);
		handlerByOTTypeMap.put(OTCorePackage.DOUBLE_TYPE,        handlers[4]);
		handlerByOTTypeMap.put(OTCorePackage.STRING_TYPE,        handlers[5]);
		handlerByOTTypeMap.put(OTCorePackage.XML_STRING_TYPE,    handlers[6]);
		handlerByOTTypeMap.put(OTCorePackage.BLOB_TYPE,          handlers[7]);
		handlerByOTTypeMap.put(OTCorePackage.OBJECT_LIST_TYPE,   handlers[8]);
		handlerByOTTypeMap.put(OTCorePackage.RESOURCE_LIST_TYPE, handlers[8]);
		handlerByOTTypeMap.put(OTCorePackage.OBJECT_MAP_TYPE,    handlers[9]);
		handlerByOTTypeMap.put(OTCorePackage.RESOURCE_MAP_TYPE,  handlers[9]);
	}
	
	public void registerUserType(String name, ResourceTypeHandler handler)
	{
		handlerMap.put(name, handler);
	}
	
	public void registerUserType(OTClass otClass, ResourceTypeHandler handler)
	{
		handlerByOTTypeMap.put(otClass, handler);
	}	
	
	public void registerShortcutName(String name, OTClass otClass)
	{
		shortcutMap.put(name, otClass);
	}
	
	public OTClass getClassByShortcut(String shortcut)
	{
		return shortcutMap.get(shortcut);
	}
	
	/**
	 * look for the primitive type handler
	 * 
	 * @param nodeName
	 * @return
	 */
	public ResourceTypeHandler getElementHandler(String nodeName)
	{
		ResourceTypeHandler handler = handlerMap.get(nodeName);
		
		return handler;
	}

	public ResourceTypeHandler getElementHandler(OTType otType)
	{
		ResourceTypeHandler handler = (ResourceTypeHandler) handlerByOTTypeMap.get(otType);
		
		if(handler == null && otType instanceof OTEnum) {
			handler = new EnumTypeHandler((OTEnum) otType);
			handlerByOTTypeMap.put(otType, handler);
		}
		
		return handler;
	}
	

	public static String elementPath(OTXMLElement element)
	{
		String path = element.getName();
		OTXMLElement parent = element.getParentElement();
		while(parent != null) {
			String elementString = parent.getName();
			
			String idStr = parent.getAttributeValue("local_id");
			if(idStr != null && idStr.length() > 0) {
				elementString += "@" + idStr;
			}
			
			path = elementString + "/" + path;
			parent = parent.getParentElement();			
		}
		
		return path;
	}

	public static String attributePath(OTXMLAttribute attribute)
	{
	    OTXMLElement parent = attribute.getParent();
		String parentPath = elementPath(parent);
		return parentPath + "@" + attribute.getName();
	}

	
	/**
	 * There is no information about the element.  So in this case the 
	 * element is treated literally.  The name of the element is used
	 * to figure out its type. 
	 * @param child
	 * @return
	 */
	public Object handleLiteralElement(OTXMLElement child, String relativePath, XMLDataObject parent, String propertyName)
	{
		String childName = child.getName();

		ResourceTypeHandler handler = getElementHandler(childName);
		if(handler == null) {
			logger.warning("Invalid OTType: " + childName  + "\n" +
				"   located at: " + elementPath(child) + "\n" +
				"   check the imports and classpath");
			return null;
		}
		String childTypeName = handler.getPrimitiveName();
		
		if(childTypeName == null) {
			logger.warning("unknown element: " + childTypeName);
			return null;
		}
		
		try {
			return handler.handleElement(child, relativePath, parent, propertyName);
		} catch (HandlerException e) {
			logger.warning("Error reading element: " + TypeService.elementPath(child));
			return null;
		}
		
	}	
}
