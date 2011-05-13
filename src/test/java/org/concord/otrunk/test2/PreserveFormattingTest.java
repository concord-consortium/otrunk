package org.concord.otrunk.test2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.view.OTConfig;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.ExporterJDOM;

public class PreserveFormattingTest extends TestCase
{
	private static final Logger logger = Logger.getLogger(PreserveFormattingTest.class.getCanonicalName());
	private OTViewerHelper viewerHelper;
	private OTDatabase mainDb;
	private OTrunkImpl otrunk;
	private void initOtrunk(URL authoredContent)
	throws Exception
	{
		logger.finer("loading otrunk");
		System.setProperty(OTConfig.NO_USER_PROP, "true");

		viewerHelper = new OTViewerHelper();
		mainDb = viewerHelper.loadOTDatabase(
			authoredContent.openStream(), authoredContent, System.err, true);
		viewerHelper.loadOTrunk(mainDb, null);

		otrunk = (OTrunkImpl) viewerHelper.getOtrunk();
	}

	public void testContainmentFormatting() throws Exception
	{
		formattingHelper("containment-test.otml");
	}
	
	public void testStringPropertyFormatting() throws Exception
	{
		formattingHelper("string-property-formatting.otml");
	}

	public void testMapKeyIdFormatting() throws Exception
	{
		formattingHelper("map-key-id-test.otml");
	}

	public void testEnumFormatting() throws Exception
	{
		formattingHelper("enum-test.otml");
	}
	
	public void testIntValueFormatting() throws Exception
	{
		formattingHelper("intvalue-test.otml");
	}

	public void formattingHelper(String fileName) throws Exception
	{
		URL otmlUrl = 
			PreserveFormattingTest.class.getResource("/" + fileName);
		initOtrunk(otmlUrl);
		
		ExporterJDOM.useFullClassNames = false;

		InputStreamReader reader = new InputStreamReader(otmlUrl.openStream());
		BufferedReader bufReader = new BufferedReader(reader);
		StringBuffer strBuf = new StringBuffer();
		String line;
		while((line = bufReader.readLine()) != null){
			strBuf.append(line).append("\n");
		}

		StringWriter writer = new StringWriter();
		ExporterJDOM.exportWithUnixSep(writer, mainDb.getRoot(), mainDb);

		// If this fails JUnit does point at the differences between the strings
		// but it is a bit hard to read so it might be easier to save the output
		// as a file and compare it to the input
		/*
		FileOutputStream output = new FileOutputStream("src/test/resources/output/" + fileName);
		ExporterJDOM.useFullClassNames = false;
		OutputStreamWriter fileWriter = new OutputStreamWriter(output, "UTF-8");

		ExporterJDOM.exportWithUnixSep(fileWriter, mainDb.getRoot(), mainDb);
		writer.close();
        */
		
		// FIXME this should be replaced with better differencing 
		assertEquals(strBuf.toString(), writer.toString());				
	}
}
