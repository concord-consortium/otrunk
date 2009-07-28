package org.concord.otrunk.test2;

import org.concord.framework.otrunk.OTXMLString;
import org.concord.otrunk.test.OTXMLStringTestObject;
import org.concord.otrunk.test.RoundTrip;

public class XmlStringTest extends RoundTrip
{
	public void testBasicRoundTrip() throws Exception
	{
		initOTrunk();
		
		
        OTXMLStringTestObject root = otrunk.createObject(OTXMLStringTestObject.class);
        otrunk.setRoot(root);	        

        // The weird formating is so this matches exactly the output after the round trip.
        String testXml = "Outside Text\r\n        <el attr=\"attribute text\">inside text</el>";
        
        OTXMLString xmlString = new OTXMLString(testXml);
        root.setXmlString(xmlString);
        
        assertEquals(testXml, root.getXmlString().getContent());
        
        reload();
        
		root = (OTXMLStringTestObject) viewerHelper.getRootObject();
		
        assertEquals(testXml, root.getXmlString().getContent());
	}
}
