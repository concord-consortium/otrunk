/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2005-03-14 05:05:43 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Properties;

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
	public abstract Object handleElement(OTXMLElement element, Properties elementProps)
		throws HandleElementException;

	public String getPrimitiveName()
	{
		return primitiveName;
	}
}
