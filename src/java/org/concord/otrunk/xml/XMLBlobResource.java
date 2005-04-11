
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
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
 */

/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2005-04-11 15:01:08 $
 * $Author: maven $
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

import org.concord.domain.Transfer;


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
	
	public XMLBlobResource(URL url)
	{
		blobURL = url;
	}
	
	public byte [] getBytes()
	{
	    if(bytes != null) return bytes;
	    
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
		} catch (SocketException sockExcp)
		{
			System.err.println(sockExcp.toString());
		} catch (FileNotFoundException e){
		    System.err.println("error loading xml resource: ");
		    System.err.println("   " + e.toString());
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
	
	public URL getBlobURL()
	{
	    return blobURL;
	}
}
