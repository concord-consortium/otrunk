/*
 * Created on Mar 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk.xml.jdom;

import java.io.InputStream;

import org.concord.otrunk.xml.OTXMLElement;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JDOMDocument
{
    Document doc;
    public JDOMDocument(InputStream xmlStream)
    	throws Exception
    {
        SAXBuilder builder = new SAXBuilder();
        Document documentJDOM = builder.build(xmlStream);
                
        this.doc = documentJDOM;
    }
    
    public OTXMLElement getRootElement()
    {
        Element root = doc.getRootElement();
        return new JDOMElement(root);
    }
}
