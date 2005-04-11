
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
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
 */

/*
 * Last modification information:
 * $Revision: 1.12 $
 * $Date: 2005-04-11 15:01:08 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;

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
	public static String getPrimitiveType(Class klass)
	{
		if(String.class.isAssignableFrom(klass)) {
			return "string";
		} else if(Boolean.class.isAssignableFrom(klass) ||
				Boolean.TYPE.equals(klass)) {
			return "boolean";
		} else if(Integer.class.isAssignableFrom(klass) ||
				Integer.TYPE.equals(klass)) {
			return "integer";
		} else if(Float.class.isAssignableFrom(klass) ||
				Float.TYPE.equals(klass)) {
			return "float";
		} else if(klass.isArray() && 
				klass.getComponentType().equals(Byte.TYPE)) {
			return "blob";
		} else if(OTResourceList.class.isAssignableFrom(klass) ||
				OTObjectList.class.isAssignableFrom(klass)) {
			return "list";
		} else if(OTResourceMap.class.isAssignableFrom(klass) ||
				OTObjectMap.class.isAssignableFrom(klass)) {
			return "map";
		} else if(OTID.class.isAssignableFrom(klass) ||
				OTObject.class.isAssignableFrom(klass) ) {
			return "object";
		}
	
		return null;
	}
	
	Hashtable handlerMap = new Hashtable();
	Vector dataObjects = new Vector();

	public TypeService(URL contextURL)
	{	
		// This should be done automatically so we don't need to 
		// define them here.  The name can be looked up when it
		// is requested.
		handlerMap.put("boolean", new BooleanTypeHandler());
		handlerMap.put("integer", new IntegerTypeHandler());
		handlerMap.put("float", new FloatTypeHandler());
		handlerMap.put("string", new StringTypeHandler());
		handlerMap.put("blob", new BlobTypeHandler(contextURL));
		handlerMap.put("list", new ListTypeHandler(this));
		handlerMap.put("map", new MapTypeHandler(this));
	}
	
	public void registerUserType(String name, ResourceTypeHandler handler)
	{
		handlerMap.put(name, handler);
	}
	
	/**
	 * look for the primitive type handler
	 * 
	 * @param nodeName
	 * @return
	 */
	public ResourceTypeHandler getElementHandler(String nodeName)
	{
		ResourceTypeHandler handler = (ResourceTypeHandler) handlerMap.get(nodeName);
		
		// temporary hack
		String packageName = "org.concord.portfolio.objects.";
		
		if(handler == null && nodeName.startsWith(packageName)) {
			nodeName = nodeName.substring(packageName.length(), nodeName.length());
			handler = (ResourceTypeHandler) handlerMap.get(nodeName);
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
	public Object handleLiteralElement(OTXMLElement child)
	{
		String childName = child.getName();
		Properties elementProps;

		ResourceTypeHandler handler = getElementHandler(childName);
		if(handler == null) {			
			throw new RuntimeException("can't find handler for: " + elementPath(child));
		}
		String childTypeName = handler.getPrimitiveName();
		
		if(childTypeName == null) {
			System.err.println("unknown element: " + childTypeName);
			return null;
		}
		
		try {
			return handler.handleElement(child, null);
		} catch (HandleElementException e) {
			System.err.println("Error reading element: " + TypeService.elementPath(child));
			return null;
		}
		
	}
	
	public Hashtable getHandlerMap()
	{
		return handlerMap;
	}
}
