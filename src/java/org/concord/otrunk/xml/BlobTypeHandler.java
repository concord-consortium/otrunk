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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
public class BlobTypeHandler extends ResourceTypeHandler
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
	public Object handleElement(Element element, Properties elementProps)
	{
		String urlStr = element.getTextTrim();
		
		// TODO add relative url to type service so relative urls can
		// be used.
		try {
			URL url = new URL(contextURL, urlStr);
			InputStream urlStream = url.openStream();
			BufferedInputStream inStream = new BufferedInputStream(urlStream);
			Transfer trans = new Transfer();
			
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			
			trans.transfer(inStream, outStream, true);
			
			return outStream.toByteArray();
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		
		return null;
	}

}
