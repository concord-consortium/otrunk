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
	protected TypeService typeService = null;
	
	abstract public Object handleElement(Element element, Properties elementProps);

	public ResourceTypeHandler(TypeService dots)
	{
		typeService = dots;
	}
}
