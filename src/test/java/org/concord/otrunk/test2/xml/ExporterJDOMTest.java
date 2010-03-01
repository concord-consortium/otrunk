package org.concord.otrunk.test2.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.xml.ExporterJDOM;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

public class ExporterJDOMTest
{
	private static final Logger logger = Logger.getLogger(ExporterJDOMTest.class.getCanonicalName());

	private static final URL authoredContent = ExporterJDOMTest.class.getResource("/overlay-copy-test-authored.otml");
	private static final URL learnerContent = ExporterJDOMTest.class.getResource("/overlay-copy-test-learner.otml");
	private static final String documentUUID = "9d4f759c-3166-4c54-a6ab-416e546d9f62";
	private final OTrunkHelper otHelper = new OTrunkHelper();
	
	@After
	public void afterTest() {
		// have to reset this or it can mess up other tests when run in Continuum
		ExporterJDOM.skipOTrunkWrapping = false;
	}
	
	@Test
	public void testNormalXMLExport() throws Exception {
		String expected = loadExpectedResult("/exporter-jdom-expected-results/test-normal-xml-export.xml");
		otHelper.initOtrunk(authoredContent);
		
		StringWriter writer = new StringWriter();
		ExporterJDOM.exportWithUnixSep(writer, otHelper.getDataObject(documentUUID, "primitive_some_changes", false), otHelper.getMainDb());
		
		writer.flush();
		String actual = writer.toString();
		
		logger.info("Actual:\n'" + actual + "'");
		
		Assert.assertEquals(expected, actual);
	}
	
	@Ignore
	public void testStudentXMLExport() throws Exception {
		String expected = loadExpectedResult("/exporter-jdom-expected-results/test-student-xml-export.xml");
		otHelper.initOtrunk(authoredContent, learnerContent);
		
		StringWriter writer = new StringWriter();
		OTDataObject dataObj = otHelper.getDataObject(documentUUID, "primitive_some_changes", true);
		Assert.assertNotNull(dataObj);
		ExporterJDOM.exportWithUnixSep(writer, dataObj, otHelper.getMainDb());
		
		writer.flush();
		String actual = writer.toString();
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testNormalXMLExportWithReferences() throws Exception {
		String expected = loadExpectedResult("/exporter-jdom-expected-results/test-normal-xml-export-with-references.xml");
		otHelper.initOtrunk(authoredContent);
		
		StringWriter writer = new StringWriter();
		ExporterJDOM.exportWithUnixSep(writer, otHelper.getDataObject(documentUUID, "object_list_some_change", false), otHelper.getMainDb());
		
		writer.flush();
		String actual = writer.toString();
		Assert.assertEquals(expected, actual);
	}
	
	@Ignore
	public void testStudentXMLExportWithReferences() throws Exception {
		String expected = loadExpectedResult("/exporter-jdom-expected-results/test-student-xml-export-with-references.xml");
		otHelper.initOtrunk(authoredContent, learnerContent);
		
		StringWriter writer = new StringWriter();
		ExporterJDOM.exportWithUnixSep(writer, otHelper.getDataObject(documentUUID, "object_list_some_change", true), otHelper.getMainDb());
		
		writer.flush();
		String actual = writer.toString();
		logger.info("Actual:\n'" + actual + "'");
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testNormalXMLExportWithoutOtrunkWrapping() throws Exception {
		String expected = loadExpectedResult("/exporter-jdom-expected-results/test-normal-xml-export-without-otrunk-wrapping.xml");
		otHelper.initOtrunk(authoredContent);
		
		StringWriter writer = new StringWriter();
		ExporterJDOM.skipOTrunkWrapping = true;
		ExporterJDOM.exportWithUnixSep(writer, otHelper.getDataObject(documentUUID, "primitive_some_changes", false), otHelper.getMainDb());
		
		writer.flush();
		String actual = writer.toString();
		
		logger.info("Actual:\n'" + actual + "'");
		
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testNormalXMLExportWithReferencesWithoutOtrunkWrapping() throws Exception {
		String expected = loadExpectedResult("/exporter-jdom-expected-results/test-normal-xml-export-with-references-without-otrunk-wrapping.xml");
		otHelper.initOtrunk(authoredContent);
		
		StringWriter writer = new StringWriter();
		ExporterJDOM.skipOTrunkWrapping = true;
		ExporterJDOM.exportWithUnixSep(writer, otHelper.getDataObject(documentUUID, "object_list_some_change", false), otHelper.getMainDb());
		
		writer.flush();
		String actual = writer.toString();
		Assert.assertEquals(expected, actual);
	}
	
	private String loadExpectedResult(String resourceName) throws IOException {
		URL expectedUrl = ExporterJDOMTest.class.getResource(resourceName);
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(expectedUrl.openStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}
		return sb.toString();
	}
	


}
