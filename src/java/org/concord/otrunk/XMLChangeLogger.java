/**
 * 
 */
package org.concord.otrunk;

import java.io.OutputStream;
import java.io.PrintWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTChangeNotifying;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This extends TraceListener so it will be ignored in all the same places that
 * tracelistener is ignored.
 * 
 * @author scott
 *
 */
public class XMLChangeLogger  
implements OTObjectServiceListener, OTChangeListener, InternalListener	
{
	private ContentHandler saxContentHandler;
	private PrintWriter writer;
	
	/**
	 * @param label
	 * @throws TransformerConfigurationException 
	 * @throws SAXException 
	 */
	public XMLChangeLogger(OutputStream out) throws TransformerConfigurationException, SAXException 
	{		
		writer = new PrintWriter(out);
		StreamResult streamResult = new StreamResult(writer);
		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();

		
		
		TransformerHandler transformerHandler2 = tf.newTransformerHandler();
		Transformer serializer = transformerHandler2.getTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
		serializer.setOutputProperty(OutputKeys.INDENT,"yes");
		transformerHandler2.setResult(streamResult);
		
		saxContentHandler = transformerHandler2;
		
		start();
		
	}

	public XMLChangeLogger(ContentHandler contentHandler) throws SAXException 
	{
		this.saxContentHandler = contentHandler;
		start();
	}
	
	protected void start() throws SAXException
	{
		saxContentHandler.startDocument();
		AttributesImpl atts = new AttributesImpl();
		saxContentHandler.startElement("", "log", "log", atts);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTObjectServiceListener#objectLoaded(org.concord.framework.otrunk.OTObject)
	 */
	public void objectLoaded(OTObject object) 
	{
		if(object instanceof OTChangeNotifying) {
			((OTChangeNotifying)object).addOTChangeListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTChangeListener#stateChanged(org.concord.framework.otrunk.OTChangeEvent)
	 */
	public void stateChanged(OTChangeEvent e) 
	{
		OTObject source = (OTObject) e.getSource();
		OTClass otClass = source.otClass();
		String className = otClass.getName();
	    int lastDot = className.lastIndexOf(".");
	    String localClassName = className.substring(lastDot+1,className.length());

	    String propertyStr = e.getProperty();
	    OTClassProperty otClassProperty = otClass.getProperty(propertyStr);
	    AttributesImpl atts = new AttributesImpl();

	    try {

		    atts.addAttribute("", "id", "id", "CDATA", source.otExternalId());

		    saxContentHandler.startElement("", localClassName, localClassName, atts);
		    
		    atts.clear();
	    	atts.addAttribute("", "op", "op", "CDATA", e.getOperation());
	    	
		    atts.addAttribute("", "time", "time", "CDATA", "" + System.currentTimeMillis());
	
		    saxContentHandler.startElement("",propertyStr,propertyStr, atts);
	        String value = null;
		    if(otClassProperty.isPrimitive()){
		    	value = e.getValue().toString();
		    } else {
		    	Object eventValue = e.getValue();
		    	if(eventValue instanceof OTObject){
		    		atts.clear();
		    		atts.addAttribute("", "refid", "refid", "CDATA", ((OTObject)eventValue).otExternalId());
		    		saxContentHandler.startElement("","object","object", atts);
		    		saxContentHandler.endElement("", "object", "object");
		    	} else if(eventValue != null){
		    		value = "unknown_value: " + eventValue.toString();
		    	} 
		    }
		    
		    if(value != null){
		    	saxContentHandler.characters(value.toCharArray(), 0, value.length());
		    }
	    	atts.addAttribute("", propertyStr, propertyStr, "CDATA", value);
		    
		    saxContentHandler.endElement("",propertyStr,propertyStr);
		    
	        saxContentHandler.endElement("", localClassName, localClassName);
        } catch (SAXException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }
        if(writer != null){
        	writer.flush();
        }
	}
}
