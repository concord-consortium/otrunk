/**
 * 
 */
package org.concord.otrunk.test;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;

import org.concord.otrunk.xml.ExporterJDOM;
import org.concord.otrunk.xml.XMLDatabase;

/**
 * @author scott
 *
 */
public class PreserveFormattingSaveTest {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) 
		throws MalformedURLException, Exception 
	{
		// load in the first argument as an otml file
		// save it to the second argument
				
		File input = new File(args[0]);
		XMLDatabase mainDb = new XMLDatabase(input.toURL(), System.err);
		mainDb.setTrackResourceInfo(true);
		mainDb.loadObjects();
		
		long startTime = System.currentTimeMillis();
		FileOutputStream output = new FileOutputStream(args[1]);
		ExporterJDOM.useFullClassNames = false;
		ExporterJDOM.export(output, mainDb.getRoot(), mainDb);
		output.close();
		System.err.println("JDOM export time: " + (System.currentTimeMillis() - startTime));
		
		System.exit(0);
	}

}
