/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-10-25 05:33:57 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.concord.otrunk.xml.dod.DoDescription;
import org.concord.otrunk.xml.dod.Pfobjects;
import org.concord.otrunk.xml.dod.ResourceType;
import org.jdom.Element;

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
	Pfobjects pfObjects = null;
	Hashtable handlerMap = new Hashtable();
	URL contextURL;
	Vector dataObjects = new Vector();
	
	public TypeService(Pfobjects pfObjects, URL contextURL)
	{
		this.pfObjects = pfObjects;
		this.contextURL = contextURL;
		
		// This should be done automatically so we don't need to 
		// define them here.  The name can be looked up when it
		// is requested.
		handlerMap.put("boolean", new BooleanTypeHandler(this));
		handlerMap.put("list", new ListTypeHandler(this));
		handlerMap.put("string", new StringTypeHandler(this));
		handlerMap.put("blob", new BlobTypeHandler(this));
		handlerMap.put("object", new ObjectTypeHandler(this));
	}
	
	public ResourceTypeHandler getHandler(String typeName) 
	{
		return (ResourceTypeHandler)handlerMap.get(typeName);
	}
	
	public URL getContextURL()
	{
		return contextURL;
	}
	
	public String getElementType(String nodeName)
	{
		ResourceTypeHandler typeHandler = (ResourceTypeHandler) handlerMap.get(nodeName);
		String type = null;
		
		// Is this a predefined type?
		if(typeHandler != null){
			return nodeName;
		} 
		
		
		// This isn't a predefined type so check for it in the user types
		DoDescription dod = getDod(nodeName);
		if(dod != null) {
			return "object";
		}
		
		// this isn't a valid type
		return null;		
	}
		
	public DoDescription getDod(String name)
	{
		DoDescription [] dods = (DoDescription [])pfObjects.getDoDescriptionArray();
		for(int i=0; i<dods.length; i++) {
			if(dods[i].getName().equals(name)) {
				return dods[i];
			}
		}
		
		return null;
	}	

	public ResourceType getResourceDefinition(DoDescription dod, 
			String name)
	{
		ResourceType [] resources = (ResourceType [])dod.getResourceArray();
		for(int i=0; i<resources.length; i++) {
			if(resources[i].getName().equals(name)) {
				return resources[i];
			}
		}

		String parentName = dod.getExtends();
		if(parentName != null && parentName.length() > 0) {
			DoDescription parentDod = getDod(parentName);
			if(parentDod == null) {
				System.err.println("can't find parent: " + parentName +
						" of: " + dod.getName());
			}
			return getResourceDefinition(parentDod, name);
		}
		
		return null;
	}
	
	public Properties getResourceProperties(ResourceType resType)
	{
		// TODO need to go through the resource type object and make a properties
		// object that descriptes the type both the type of the resource and
		// any extra paramaters of the type
		Properties props = new Properties();
		props.setProperty("type", resType.getType());
		ResourceType.Param []  params = resType.getParamArray();
		for(int i=0; i<params.length; i++){
			ResourceType.Param param = params[i];
			props.setProperty(param.getName(), param.getValue());
		}
		return props;		
	}
	
	public String getClassName(DoDescription dod)
	{
		return "org.concord.portfolio.objects." + dod.getName();
	}
	
	/**
	 * This method handles xml elements.  The elements can come from
	 * two places so far: resources in a data object or items in
	 * a list.  
	 * If the element is a resource in a data object than there
	 * will be more information about it based on the definition of the
	 * data object type.  This "type" is the DoDescription that is passed
	 * to this method.
	 * If the element is an item in a list then there is no information
	 * about the element.  So in this case the element must be treated 
	 * literally. 
	 * @param parentType
	 * @param child
	 * @return
	 */
	protected Object handleElement(Element child, Properties elementProps)
	{
		String typeName = elementProps.getProperty("type");
		ResourceTypeHandler handler = getHandler(typeName);
		return handler.handleElement(child, elementProps);		
	}
	/**
	 * There is no information about the element.  So in this case the 
	 * element is treated literally.  The name of the element is used
	 * to figure out its type. 
	 * @param child
	 * @return
	 */
	public Object handleLiteralElement(Element child)
	{
		String childName = child.getName();
		Properties elementProps;

		String childTypeName = getElementType(childName);
		
		if(childTypeName == null) {
			System.err.println("unknown element: " + childTypeName);
			return null;
		}
		
		elementProps = new Properties();
		elementProps.setProperty("type", childTypeName);

		return handleElement(child, elementProps);
	}
	
	/**
	 * This element comes with extra information.  The name of 
	 * the element identifies it as a resource in the parentType
	 * The resource in the parent type has type information.  So
	 * if the element is "&lt;myText>hi this is my text&lt;/myText>"
	 * and myText is defined as a "string" in the parentType then
	 * this element will be turned into a string.
	 * 
	 * @param parentType
	 * @param child
	 * @return
	 */
	public Object handleChildResource(DoDescription parentType,
			Element child)
	{
		String childName = child.getName();
		Properties elementProps;

		ResourceType resourceDef = getResourceDefinition(parentType, childName);
		
		if(resourceDef == null) {
			System.err.println("error reading childName: " + childName +
					" in type: " + parentType.getName());
		}
		elementProps = getResourceProperties(resourceDef);
		if(elementProps.getProperty("type","").equals("object")) {
			String childRefId = child.getAttributeValue("refid");
			
			// If this element doesn't have a reference id then 
			// then the first child of this element is object 
			if(childRefId == null) {
				child = (Element)(child.getContent(0));
			}
		}

		return handleElement(child, elementProps);
	}
	
	public void addDataObject(XMLDataObject object)
	{
		dataObjects.add(object);
	}
	
	public Vector getDataObjects()
	{
		return dataObjects;
	}
}
