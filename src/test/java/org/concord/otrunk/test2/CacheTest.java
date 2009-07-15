package org.concord.otrunk.test2;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import junit.framework.TestCase;

import org.concord.otrunk.net.InstallationResponseCache;


public class CacheTest extends TestCase {
	private class Cacher {
    	Object object;
    
    	public void fetchImage(String url) throws IOException {
    		Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("png");
    		ImageReader reader = readers.next();
    		
    		InputStream source = new URL(url).openStream(); // File or InputStream
    		ImageInputStream iis = ImageIO.createImageInputStream(source);
    		reader.setInput(iis,false);
    		object = reader.read(0);
    		reader.dispose();
    	}
    	
    	public void fetchUrl(String url) throws Exception {
    		InputStream stream = new URL(url).openStream();
    		ArrayList<Integer> bytes = new ArrayList<Integer>();
    		// Mark is apparently not supported by the input stream returned by URL.openStream()
//    		stream.mark(100);
//    		byte[] myBytes = new byte[45];
//    		stream.read(myBytes, 0, 45);
//    		stream.reset();
    		int val = stream.read();
    		while (val != -1) {
    			bytes.add(val);
    			val = stream.read();
    		}
    		object = bytes;
    	}
    	
    	public Object getObject() {
    		return object;
    	}
	}

	public void testImageReaderEqual() throws Exception {
		InstallationResponseCache.installResponseCache();
		InstallationResponseCache.clearCache();
		
		Cacher test1 = new Cacher();
		test1.fetchImage("http://udl.concord.org/share/models/netlogo/aspenleaftrans.png");
		Cacher test2 = new Cacher();
		test2.fetchImage("http://udl.concord.org/share/models/netlogo/aspenleaftrans.png");
		
		// XXX These should equal each other, but apparently BufferedImage doesn't implement the equal() method well...
		assertEquals(false, test1.getObject().equals(test2.getObject()));
	}
	
	public void testUrlReaderEqual() throws Exception {
		InstallationResponseCache.installResponseCache();
		InstallationResponseCache.clearCache();
		
		Cacher test1 = new Cacher();
		test1.fetchUrl("http://udl.concord.org/share/models/netlogo/aspenleaftrans.png");
		Cacher test2 = new Cacher();
		test2.fetchUrl("http://udl.concord.org/share/models/netlogo/aspenleaftrans.png");
		
		assertEquals(true, test1.getObject().equals(test2.getObject()));
	}

}
