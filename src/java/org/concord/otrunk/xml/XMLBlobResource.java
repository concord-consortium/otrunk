/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2005-03-31 21:07:26 $
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
