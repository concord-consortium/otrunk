package org.concord.otrunk.test2;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;

import junit.framework.TestCase;

import org.concord.utilities.InstallationResponseCache;


public class CacheTest extends TestCase {
	private class Cacher {
    	Object object;
    
    	public void fetchImageReader(String url) throws IOException {
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
    	
    	public void fetchImageIcon(String url) throws Exception {
    		ImageIcon icon = new ImageIcon(new URL(url));
//    		while (icon.getImageLoadStatus() == MediaTracker.LOADING) {
//    			Thread.sleep(100);
//    		}
//    		JFrame frame = new JFrame();
//    		JButton button = new JButton(icon);
//    		frame.getContentPane().add(button);
//    		frame.pack();
//    		frame.setVisible(true);
//    		Thread.sleep(2000);
    		object = new ImageIcon(url).getImage();
//    		frame.dispose();
    	}
    	
    	public Object getObject() {
    		return object;
    	}
	}

	public void testImageReaderEqual() throws Exception {
		InstallationResponseCache.installResponseCache();
		InstallationResponseCache.clearCache();
		
		System.err.flush();
		System.out.println("Running Image Reader Test");
		System.out.flush();
		
		Cacher test1 = new Cacher();
		test1.fetchImageReader("http://udl.concord.org/share/models/netlogo/aspenleaftrans.png");
		Cacher test2 = new Cacher();
		test2.fetchImageReader("http://udl.concord.org/share/models/netlogo/aspenleaftrans.png");
		
		// XXX These should equal each other, but apparently BufferedImage doesn't implement the equal() method well...
		assertEquals(false, test1.getObject().equals(test2.getObject()));
	}
	
	public void testUrlReaderEqual() throws Exception {
		InstallationResponseCache.installResponseCache();
		InstallationResponseCache.clearCache();
		
		System.err.flush();
		System.out.println("Running Direct Download Test");
		System.out.flush();
		
		Cacher test1 = new Cacher();
		test1.fetchUrl("http://udl.concord.org/share/models/netlogo/aspenleaftrans.png");
		Cacher test2 = new Cacher();
		test2.fetchUrl("http://udl.concord.org/share/models/netlogo/aspenleaftrans.png");
		
		assertEquals(true, test1.getObject().equals(test2.getObject()));
	}
	
	public void testUrlImageIconEqual() throws Exception {
		InstallationResponseCache.installResponseCache();
		InstallationResponseCache.clearCache();
		System.err.flush();
		System.out.println("Running Image Icon Test");
		System.out.flush();
		
		Cacher test1 = new Cacher();
		test1.fetchImageIcon("http://udl.concord.org/share/models/netlogo/aspenleaftrans.png");
		Cacher test2 = new Cacher();
		test2.fetchImageIcon("http://udl.concord.org/share/models/netlogo/aspenleaftrans.png");
		
		assertEquals(true, test1.getObject().equals(test2.getObject()));
	}

}
