/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2004-11-22 23:05:40 $
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
	
	abstract public Object handleElement(Element element, Properties elementProps);

	public ResourceTypeHandler(String primitiveName)
	{
		this.primitiveName = primitiveName;
	}
	
	public String getPrimitiveName()
	{
		return primitiveName;
	}
}
