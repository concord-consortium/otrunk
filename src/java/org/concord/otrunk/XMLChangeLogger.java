/**
 * 
 */
package org.concord.otrunk;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTChangeLogger;
import org.concord.framework.otrunk.OTChangeNotifying;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXHandler;
import org.jdom.xpath.XPath;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This extends TraceListener so it will be ignored in all the same places that
 * tracelistener is ignored.
 * 
 * To reduce duplicate information this could just store the old value
 * not the new one.  The newest value will be in the learner data.
 * However this will require storing the lastModified information in the learner data.
 * Except for the fact that we won't know which property changed in that last modification
 * so then the log won't reflect that.  So in conclusion it is better to log everything.
 * 
 * Another issue with this log approach is trying to find an old version of an object.  If you
 * want to look at an old version of a graph you need to access the whole collection of graph
 * objects. With the log this could be done by replaying the log up to the point that is 
 * necessary.  But this would be slow if done a lot.  And there are issues of having 2 versions
 * of the same object available at the same time.  I guess it could be done as an overlay.
 * 
 * @author scott
 *
 */
public class XMLChangeLogger  
implements OTObjectServiceListener, OTChangeListener, InternalListener	
{
	private ContentHandler saxContentHandler;
	private PrintWriter writer;
	private SAXHandler saxHandler;
	
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
	
	/**
	 * This is a temporary test method.  Using its implementation of OTChangeLogger requires Jaxen on 
	 * the classpath. 
	 */
	public OTChangeLogger tryQueriableLog(OTObjectService objectService)
	{
		saxHandler = new SAXHandler();
		try {
	        XMLChangeLogger changeLogger = new XMLChangeLogger(saxHandler);
	        ((OTObjectServiceImpl)objectService).addObjectServiceListener(changeLogger);
        } catch (SAXException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }

        OTChangeLogger changeLoggerService = new OTChangeLogger()
        {

        	/**
        	 * FIXME This method is not complete it always returns null
        	 * 
        	 * @see org.concord.framework.otrunk.OTChangeLogger#getPreviousValues(org.concord.framework.otrunk.OTObject, org.concord.framework.otrunk.otcore.OTClassProperty)
        	 */
			@SuppressWarnings("unchecked")
            public Iterator getPreviousValues(OTObject otObject, OTClassProperty property)
            {
				Document document = saxHandler.getDocument();
				XPath xpath;
                try {
                	// /log/*[@id='33754150-b594-11d9-9669-0800200c9a66!/normal_choice']/currentChoice[@op='set']/node()
                	// this code is not complete has not been tested, 
                    xpath = XPath.newInstance("/log/*[@id='"+ otObject.otExternalId() + "']/" + 
                    	property.getName() + "[@op='set']/node()");
	                List selectNodes = xpath.selectNodes(document);
	                for(Iterator it = selectNodes.iterator();it.hasNext();){
	                	@SuppressWarnings("unused")
                        Object node = it.next();
	                }
                } catch (JDOMException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                }				
                
	            // TODO Auto-generated method stub
	            return null;
            }
        	
        };
		return changeLoggerService;
	}
	
	/**
	 * This is a temporary test method.  It is specific to a particular otml file.   Using
	 * it requires jaxen on the classapth.
	 */
	@SuppressWarnings("unchecked")
    public void testXPathQuery()
	{
		Document document = saxHandler.getDocument();
		try {
            XPath xpath  = XPath.newInstance("/log/*[@id='33754150-b594-11d9-9669-0800200c9a66!/normal_choice']/" + 
            	"currentChoice[@op='set']/node()");
            List selectNodes = xpath.selectNodes(document);
            System.out.println("found:" + selectNodes);
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

}
