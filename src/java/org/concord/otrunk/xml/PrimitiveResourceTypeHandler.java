/*
 * Created on Jan 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk.xml;

import java.util.Properties;

import org.jdom.Element;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class PrimitiveResourceTypeHandler extends ResourceTypeHandler 
{
	public PrimitiveResourceTypeHandler(String primitiveName)
	{
		super(primitiveName);
	}
	
	abstract public Object handleElement(String value, Properties elementProps)
		throws HandleElementException;
	
	/**
	 * You must override this method if this resource needs more than a string
	 * so objects, lists, and maps need to override this method.
	 * 
	 * @param element
	 * @param elementProps
	 * @return
	 */
	public Object handleElement(Element element, Properties elementProps)
	throws HandleElementException
	{
		return handleElement(element.getTextTrim(), elementProps);
	}
}
