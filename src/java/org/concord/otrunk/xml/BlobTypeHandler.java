/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*
 * Last modification information:
 * $Revision: 1.9 $
 * $Date: 2007-01-27 23:46:22 $
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
	public final static String GZIPPED_BASE64_PROTOCOL = "gzb64:";
	URL contextURL;
	
	public BlobTypeHandler(URL context)
	{
		super("blob", byte[].class);
		contextURL = context;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.xml.ResourceTypeHandler#handleElement(org.jdom.Element, java.util.Properties)
	 */	
	public Object handleElement(String urlStr, Properties elementProps)
		throws HandleElementException
	{		
		try {
			// check if the urlStr is a gzipped base64.
			// gzb64:
			if(urlStr != null && urlStr.startsWith(GZIPPED_BASE64_PROTOCOL)) {
				String b64 = urlStr.substring(GZIPPED_BASE64_PROTOCOL.length());
				return new XMLBlobResource(b64);
			}
			
			URL url = new URL(contextURL, urlStr);
			return new XMLBlobResource(url);			
		} catch(Exception e) {
			throw new HandleElementException("malformed url for blob");
		} 		
	}

	public static String base64(byte[] bytes)
	{
		return GZIPPED_BASE64_PROTOCOL + Base64.encodeBytes(bytes, Base64.GZIP);
	}
}
