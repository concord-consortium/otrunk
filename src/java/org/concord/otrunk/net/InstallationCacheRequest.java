package org.concord.otrunk.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InstallationCacheRequest extends CacheRequest
{
	private static final Logger logger =
        Logger.getLogger(InstallationCacheRequest.class.getCanonicalName());
	private final File localFile;
	private FileOutputStream fos;
	private File headersFile;
	private int contentLength;
	
	public InstallationCacheRequest(File file, HttpURLConnection conn) {
		this.localFile = file;
		try {
			this.fos = new FileOutputStream(file);
		} catch (FileNotFoundException ex) {
			// should not happen
			ex.printStackTrace();
		}
		
		headersFile = new File(file.getAbsolutePath() + ".hdrs");
		try {
			writeHeaders(headersFile, conn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		contentLength = conn.getContentLength();
	}

	@Override
	public void abort()
	{
		// abandon the cache attempt by closing the stream and deleting
		// the local file
		try {
			fos.close();
			localFile.delete();
			headersFile.delete();
		} catch (IOException e) {
		}
	}

	@Override
	public OutputStream getBody()
	    throws IOException
	{
		/**
		 * This is a hack to fix a bug in the java 1.5 implementation of
		 * HttpConnection which writes the returned output stream.  This
		 * implementation doesn't correctly support skip() and reset().
		 * It just writes out all the bytes that are read in. 
		 * 
		 * For example the image reader reads the first 8 bytes to figure out
		 * the type of the image and then resets the stream and reads the 
		 * entire thing.  The result of this is HttpConnection sending the 
		 * first 8 bytes twice to the returned OutputStream
		 * 
		 * So the hack solution is to use the content-length header and 
		 * only write the last content-length bytes.
		 * 
		 * This won't work in all cases and could still result in a corrupt
		 * cache.
		 */
		// FIXME This is disabled for now because Netlogo never calls close, and so the cached file ends up zero-length.
		// FIXME Is there a code snippet which illustrates the problem described above?
		ByteArrayOutputStream output = new ByteArrayOutputStream(){
			private ArrayList<Byte> firstEight = new ArrayList<Byte>(8);
			private ArrayList<Byte> secondEight = new ArrayList<Byte>(8);
			private ArrayList<Byte> overflow = new ArrayList<Byte>();
			private boolean doneWithFirstBytes = false;
			
			@Override
			public void write(byte[] bytes) {
				try {
    				int size = bytes.length;
    				if (! doneWithFirstBytes) {
    					for (int i = 0; i < size; i++) {
    						if (firstEight.size() < 8) {
    							firstEight.add(bytes[i]);
    						} else if (secondEight.size() < 8) {
    							secondEight.add(bytes[i]);
    						} else {
    							overflow.add(bytes[i]);
    						}
    					}
    					if (overflow.size() > 0) {
    						doneWithFirstBytes = true;
    						fos.write(getFirstBytes());
    						fos.write(convertToByteArray(overflow));
    					}
    				} else {
    					fos.write(bytes);
    				}
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Couldn't write bytes to cache file", e);
				}
			}
			
			@Override
			public void write(byte[] bytes, int off, int len) {
				// super.write(bytes, off, len);
				byte[] outbytes = new byte[len];
				System.arraycopy(bytes, off, outbytes, 0, len);
				write(outbytes);
			}
			
			@Override
			public void write(int bit) {
				write(new byte[] {(byte) bit});
			}
			
			private byte[] getFirstBytes() {
				// compare the two arrays
				if (! firstEight.equals(secondEight)) {
					logger.info("First 8 bytes are NOT duplicated!");
					firstEight.addAll(secondEight);
				} else {
					logger.info("First 8 bytes ARE duplicated");
				}
				return convertToByteArray(firstEight);
			}
			
			private byte[] convertToByteArray(ArrayList<Byte> list) {
				byte[] outBytes = new byte[list.size()];
				for (int i = 0; i < list.size(); i++) {
					outBytes[i] = list.get(i);
				}
				return outBytes;
			}
			
//			@Override
//			public void close() throws IOException {
//				super.close();
//				
//				int offset = 0;
//				if(contentLength != -1 && size() > contentLength){
//					offset = size() - contentLength;
//				}
//				
//				if((size() - offset ) != contentLength){
//					abort();					
//				}
//				
//				byte[] byteArray = toByteArray();
//				fos.write(byteArray, offset, contentLength);
//			}
		};
		return output;
//		return fos;
	}

	void writeHeaders(File headerFile, HttpURLConnection conn) throws IOException
	{    
		Map<String, List<String>> headerFields = conn.getHeaderFields();

		Properties properties = new Properties();
		Set<Entry<String,List<String>>> entrySet = headerFields.entrySet();
		for (Entry<String, List<String>> entry : entrySet) {
			List<String> values = entry.getValue();
			String propertyValue = "";
			for (String value : values) {
				propertyValue = value + ", ";
			}
			if(propertyValue.length() > 2){
				propertyValue = propertyValue.substring(0, propertyValue.length() - 2);
			}
			String key = entry.getKey();
			if(key == null){
				// this is http version header
				key = "_http_version";
			}
			properties.put(key, propertyValue);
		}
		
		properties.storeToXML(new FileOutputStream(headerFile), null);
	}
}
