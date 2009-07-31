package org.concord.otrunk.test2;

import java.io.StringReader;
import java.io.StringWriter;

import org.concord.framework.otrunk.OTXMLString;
import org.concord.otrunk.test.OTXMLStringTestObject;
import org.concord.otrunk.test.RoundTrip;
import org.concord.otrunk.test.RoundTripHelperAuthoring;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XmlStringTest extends RoundTrip
{
	public XmlStringTest()
    {
		setHelper(new RoundTripHelperAuthoring());
    }
	
	public void testBasicRoundTrip() throws Exception
	{
		initOTrunk(OTXMLStringTestObject.class);
		
		
        OTXMLStringTestObject root = (OTXMLStringTestObject) getRootObject(); 
        
        // The weird formating is so this matches exactly the output after the round trip.
        String testXml = "Outside Text\n<el attr=\"attribute text\">inside text</el>";
        
        OTXMLString xmlString = new OTXMLString(testXml);
        root.setXmlString(xmlString);
        
        assertEquals(testXml, root.getXmlString().getContent());
        
        reload();
        
		root = (OTXMLStringTestObject) getRootObject();
		
		String originalNormalized = xmlNormalizing(testXml);
		String newNormalized = xmlNormalizing(root.getXmlString().getContent());
				
        assertEquals(originalNormalized, newNormalized);
	}
	
	public String xmlNormalizing(String originalString) throws Exception
	{
		SAXBuilder builder = new SAXBuilder();
		String xmlString = "<root>" + originalString + "</root>";
		StringReader reader = new StringReader(xmlString);
		Document xmlStringDoc = builder.build(reader, "test-xml");
		Element rootXMLStringEl = xmlStringDoc.getRootElement();
		
		// get string from inside of this element and return it
		XMLOutputter outputer = new XMLOutputter(Format.getPrettyFormat());
		
		// need a buffer output stream to make a string out of the content
		StringWriter stringWriter = new StringWriter();
		
		outputer.outputElementContent(rootXMLStringEl, stringWriter);
		return stringWriter.toString().trim();		
	}
}
