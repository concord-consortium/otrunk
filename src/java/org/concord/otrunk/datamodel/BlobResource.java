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

import org.concord.loader.util.Transfer;

/**
 * @author scott
 *
 */
public class BlobResource 
{
	protected URL blobURL;
	protected byte [] bytes;

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
	
	public byte [] getBytes()
	{
	    if(bytes != null) return bytes;

	    if(blobURL != null) {
	    	return getURLBytes();
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
    	
    	return null;
	}
}
