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
 * $Revision: 1.12 $
 * $Date: 2007-09-07 02:04:11 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.net.URL;

import org.concord.otrunk.datamodel.BlobResource;

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
	public Object handleElement(String urlStr)
		throws HandleElementException
	{		
		try {
			// check if the urlStr is a gzipped base64.
			// gzb64:
			if(urlStr != null && urlStr.startsWith(GZIPPED_BASE64_PROTOCOL)) {
				String b64 = urlStr.substring(GZIPPED_BASE64_PROTOCOL.length());
				return new BlobResource(b64);
			}
			
			if(urlStr.length() == 0){
				return null;
			}
			
			URL url = new URL(contextURL, urlStr);
			return new BlobResource(url);			
		} catch(Exception e) {
		//	e.printStackTrace();
			System.err.println("Could not load "+urlStr);
			throw new HandleElementException("malformed url for blob");
		} 		
	}

	public static String base64(byte[] bytes)
	{
		return GZIPPED_BASE64_PROTOCOL + Base64.encodeBytes(bytes, Base64.GZIP);
	}
}
