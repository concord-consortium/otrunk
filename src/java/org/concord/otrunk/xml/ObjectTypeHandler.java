/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2004-11-12 02:02:51 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.concord.otrunk.OTrunk;
import org.concord.otrunk.xml.dod.DoDescription;
import org.jdom.Element;

/**
 * ObjectTypeHandler
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class ObjectTypeHandler extends ResourceTypeHandler
{
	public ObjectTypeHandler(TypeService dots)
	{
		super(dots);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.xml.ResourceTypeHandler#handleElement(org.w3c.dom.Element, java.util.Properties)
	 */
	public Object handleElement(Element element, Properties elementProps)
	{
		String refid = element.getAttributeValue("refid");
		if(refid != null && refid.length() > 0){
			return new XMLDataObjectRef(refid, element);
		}
		XMLDataObject obj = new XMLDataObject(element);
				
		DoDescription type = typeService.getDod(element.getName());
		
		String idStr = element.getAttributeValue("id");
		if(idStr != null && idStr.length() > 0) {
			obj.setGlobalId(idStr);
		}
		
		String localIdStr = element.getAttributeValue("local_id");
		if(localIdStr != null && localIdStr.length() > 0) {
			obj.setLocalId(localIdStr);
		}
		
		obj.setResource(OTrunk.RES_CLASS_NAME, typeService.getClassName(type));
		List children = element.getChildren();
		
		for(Iterator childIter = children.iterator(); childIter.hasNext(); ) {
			Element child = (Element)childIter.next();
			Object resValue = typeService.handleChildResource(type, child);
			obj.setResource(child.getName(),resValue);
		}
		typeService.addDataObject(obj);
		return obj;
	}

}
