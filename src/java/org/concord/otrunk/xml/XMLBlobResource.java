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
 * $Revision: 1.8 $
 * $Date: 2007-02-20 00:16:40 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.net.URL;

import org.concord.otrunk.datamodel.BlobResource;


/**
 * XMLBlobResource
 * Class name and description
 *
 * Date created: Jan 22, 2005
 *
 * @author scott<p>
 *
 */
public class XMLBlobResource extends BlobResource
{
	String gzb64;
	
	public XMLBlobResource(URL url)
	{
		super(url);
	}
	
	public XMLBlobResource(String gzippedBase64Str)
	{
		super();
		gzb64 = gzippedBase64Str;
	}
	
	public byte [] getBytes()
	{
	    if(bytes != null) return bytes;

	    if(blobURL != null) {
	    	return getURLBytes();
	    } else if(gzb64 != null) {
	    	// just decode on the fly instead of saving the bytes, 
	    	// this should save memory
	    	return Base64.decode(gzb64);
	    }

		return null;
	}	
}
