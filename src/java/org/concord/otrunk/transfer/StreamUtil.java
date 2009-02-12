package org.concord.otrunk.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class StreamUtil
{

	public static void printFromStream(String label, InputStream stream)
	{
		printFromStream(label, stream, null);
	}

	public static void printFromStream(String label, InputStream stream, String encoding)
	{
		if (stream != null && encoding != null && encoding.toLowerCase().equals("gzip")) {
			try {
				stream = new GZIPInputStream(stream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String message = StreamUtil.getStringFromStream(stream);
		if (message != null) {
			System.err.println("===== " + label + " =====");
			System.err.println(message);
			System.err.println("=========================");
		}
	}

	public static String getStringFromStream(InputStream stream)
    {
    	try {
    		if(stream != null){
    			InputStreamReader reader = new InputStreamReader(stream);
    			StringBuffer errorBodyBuf = new StringBuffer();
    			char [] chars = new char[100]; 
    			while(reader.ready()){
    				int len = reader.read(chars);
    				errorBodyBuf.append(chars, 0, len);
    			}
    
    			return errorBodyBuf.toString();
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return null;
    }

}
