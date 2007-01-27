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
 * $Revision: 1.7 $
 * $Date: 2007-01-27 23:46:22 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import org.concord.loader.util.Transfer;


/**
 * XMLBlobResource
 * Class name and description
 *
 * Date created: Jan 22, 2005
 *
 * @author scott<p>
 *
 */
public class XMLBlobResource
{
	URL blobURL;
	byte [] bytes;
	String gzb64;
	
	public XMLBlobResource(URL url)
	{
		blobURL = url;
	}
	
	public XMLBlobResource(String gzippedBase64Str)
	{
		gzb64 = gzippedBase64Str;
	}
	
	public byte [] getBytes()
	{
	    if(bytes != null) return bytes;

	    if(blobURL != null) {
	    	InputStream urlStream = null;
	    	try {
	    		urlStream = blobURL.openStream();
	    		BufferedInputStream inStream = new BufferedInputStream(urlStream);
	    		Transfer trans = new Transfer();

	    		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

	    		trans.transfer(inStream, outStream, true);

	    		urlStream = null;

	    		bytes = outStream.toByteArray();
	    		return bytes;
	    	} catch (SocketException sockExcp){
	    		System.err.println(sockExcp.toString());
	    	} catch (FileNotFoundException e){
	    		System.err.println("error loading xml resource: ");
	    		System.err.println("   " + e.toString());
	    	} catch(UnknownHostException e) {
	    		System.err.println(e.toString());
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
	    } else if(gzb64 != null) {
	    	// just decode on the fly instead of saving the bytes, 
	    	// this should save memory
	    	return Base64.decode(gzb64);
	    }

		return null;
	}
	
	public URL getBlobURL()
	{
	    return blobURL;
	}
}
