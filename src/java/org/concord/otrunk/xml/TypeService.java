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
 * $Revision: 1.22 $
 * $Date: 2007-01-27 23:46:22 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.OTXMLString;

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
    public final static String OBJECT = "object";
    
	public static String getPrimitiveType(Class klass)
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
		} else if(klass.isArray() && 
				klass.getComponentType().equals(Byte.TYPE)) {
			return BLOB;
		} else if(OTResourceList.class.isAssignableFrom(klass) ||
				OTObjectList.class.isAssignableFrom(klass)) {
			return LIST;
		} else if(OTResourceMap.class.isAssignableFrom(klass) ||
				OTObjectMap.class.isAssignableFrom(klass)) {
			return MAP;
		} else if(OTID.class.isAssignableFrom(klass) ||
				OTObject.class.isAssignableFrom(klass) ) {
			return OBJECT;
		}
	
		return null;
	}
	
	Hashtable handlerMap = new Hashtable();
	Vector dataObjects = new Vector();

	public TypeService(URL contextURL)
	{	
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
		};
		
		for(int i=0; i<handlers.length; i++){
			handlerMap.put(handlers[i].getPrimitiveName(), handlers[i]);
		}
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
	public Object handleLiteralElement(OTXMLElement child, String relativePath)
	{
		String childName = child.getName();

		ResourceTypeHandler handler = getElementHandler(childName);
		if(handler == null) {			
			System.err.println("can't find handler for: " + elementPath(child));
			return null;
		}
		String childTypeName = handler.getPrimitiveName();
		
		if(childTypeName == null) {
			System.err.println("unknown element: " + childTypeName);
			return null;
		}
		
		try {
			return handler.handleElement(child, null, relativePath, null);
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
