/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2005-01-27 16:45:29 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Properties;

import org.jdom.Element;

/**
 * ResourceTypeHandler
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public abstract class ResourceTypeHandler
{
	protected String primitiveName = null;
	
	public ResourceTypeHandler(String primitiveName)
	{
		this.primitiveName = primitiveName;
	}
	
	/**
	 * You must override this method if this resource needs more than a string
	 * so objects, lists, and maps need to override this method.
	 * 
	 * @param element
	 * @param elementProps
	 * @return
	 */
	public abstract Object handleElement(Element element, Properties elementProps)
		throws HandleElementException;

	public String getPrimitiveName()
	{
		return primitiveName;
	}
}
