/*
 * Last modification information:
 * $Revision: 1.5 $
 * $Date: 2005-01-27 16:45:29 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URL;
import java.util.Properties;

import org.concord.domain.Transfer;
import org.jdom.Element;

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
