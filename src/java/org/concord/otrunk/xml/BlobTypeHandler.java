/*
 * Last modification information:
 * $Revision: 1.6 $
 * $Date: 2005-03-14 05:05:43 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.net.URL;
import java.util.Properties;

/**
 * BlobTypeHandler
 * Class name and description
 *
 * Date created: Oct 22, 2004
 *
 * @author scott<p>
 *
 */
public class BlobTypeHandler extends PrimitiveResourceTypeHandler
{
	URL contextURL;
	
	public BlobTypeHandler(URL context)
	{
		super("blob");
		contextURL = context;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.xml.ResourceTypeHandler#handleElement(org.jdom.Element, java.util.Properties)
	 */	
	public Object handleElement(String urlStr, Properties elementProps)
		throws HandleElementException
	{		
		try {
			URL url = new URL(contextURL, urlStr);
			return new XMLBlobResource(url);			
		} catch(Exception e) {
			throw new HandleElementException("malformed url for blob");
		} 		
	}

}
