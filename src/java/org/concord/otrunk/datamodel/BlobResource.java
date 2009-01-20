/**
 * 
 */
package org.concord.otrunk.datamodel;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import org.concord.otrunk.transfer.Transfer;
import org.concord.otrunk.xml.Base64;

/**
 * @author scott
 *
 */
public class BlobResource 
{
	protected URL blobURL;
	protected byte [] bytes;
	protected String gzb64;

	protected BlobResource()
	{		
	}
	
	public BlobResource(URL url)
	{
		blobURL = url;
	}
	
	public BlobResource(byte [] bytes)
	{
		this.bytes = bytes;
	}

	public BlobResource(String gzippedBase64Str)
	{
		this.gzb64 = gzippedBase64Str;
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

	public URL getBlobURL()
	{
		return blobURL;
	}
	
	/**
	 * This has the side effect of setting the bytes field to the 
	 * bytes returned by the url
	 * @return
	 */
	protected byte [] getURLBytes()
	{
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
    		System.err.println("error loading xml resource: " + blobURL);
    		System.err.println("   " + sockExcp.toString());
    	} catch (FileNotFoundException e){
    		System.err.println("error loading xml resource: " + blobURL);
    		System.err.println("   " + e.toString());
    	} catch(UnknownHostException e) {
    		System.err.println("error loading xml resource: " + blobURL);
    		System.err.println("   " + e.toString());
    	} catch(Exception e) {
    		System.err.println("error loading xml resource: " + blobURL);
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
