/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2004-12-17 20:09:18 $
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
		InputStream urlStream = null;
		try {
			URL url = new URL(contextURL, urlStr);
			urlStream = url.openStream();
			BufferedInputStream inStream = new BufferedInputStream(urlStream);
			Transfer trans = new Transfer();
			
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			
			trans.transfer(inStream, outStream, true);

			urlStream = null;
			
			return outStream.toByteArray();
		} catch (SocketException sockExcp)
		{
			System.err.println(sockExcp.toString());
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			
			if(urlStream != null) try{
				urlStream.close();
			}
			catch (IOException e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
		return null;
	}

}
