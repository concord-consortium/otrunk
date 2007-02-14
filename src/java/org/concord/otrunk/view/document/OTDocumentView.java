/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*
 * Created on Aug 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view.document;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.framework.otrunk.view.OTFrameManagerAware;
import org.concord.framework.otrunk.view.OTXHTMLHelper;
import org.concord.framework.otrunk.view.OTXHTMLView;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OTDocumentView extends AbstractOTDocumentView
	implements ChangeListener, HyperlinkListener, OTXHTMLView, 
		OTFrameManagerAware
{
	public static boolean addedCustomLayout = false;
	
	OTDocument pfDocument;		
    
	JTabbedPane tabbedPane = null;
	JComponent previewComponent = null;
	
	JEditorPane editorPane = null;
	
    DocumentBuilderFactory xmlDocumentFactory = null;
    DocumentBuilder xmlDocumentBuilder = null;   
    
    JTextArea parsedTextArea = null;
    
	public final static String XHTML_PREFIX = 
		"<?xml version='1.0' encoding='UTF-8'?>\n" +
		"<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en' lang='en'>\n" +
		"\n" +
		"<head>\n" +
		"<title>default</title>\n" +
		"</head>\n" +
		"<body style=\"background:#FFFFFF\">\n";
	public final static String XHTML_SUFFIX = 
		"</body>\n" +
		"</html>";

    
	protected void setup(OTObject doc)
	{
		//Don't call super.setup() to avoid listening to the ot object inneccesarily
		pfObject = (OTDocument)doc;
		pfDocument = (OTDocument)doc;
	}

	public JComponent getComponent(OTObject otObject, boolean editable)
	{
		setup(otObject);
		
		initTextAreaModel();
		
		if (tabbedPane != null) {
			tabbedPane.removeChangeListener(this);
		}
			
		// need to use the PfCDEditorKit 
		updateFormatedView();

		if(!editable ) {
			return previewComponent;
		}

		//JScrollPane renderedScrollPane = new JScrollPane(previewComponent);
		//renderedScrollPane.getViewport().setViewPosition(new Point(0,0));
		
		if(System.getProperty("otrunk.view.debug","").equals("true")){
		    tabbedPane = new JTabbedPane();

		    // need to add a listener so we can update the view pane
		    tabbedPane.add("View", previewComponent);
		
		    textArea = new JTextArea(textAreaModel);
		    JScrollPane scrollPane = new JScrollPane(textArea);
		    tabbedPane.add("Edit", scrollPane);
		    
		    parsedTextArea.setEnabled(false);
		    scrollPane = new JScrollPane(parsedTextArea);
		    tabbedPane.add("Parsed", scrollPane);
		    
		    tabbedPane.addChangeListener(this);
		    
		    return tabbedPane;
		} else {
		    return previewComponent;
		}
	}

	public String updateFormatedView()
	{		
        if(pfDocument == null) return null;
        
        //System.out.println(this+" updateFormatedView");
        
		String markupLanguage = pfDocument.getMarkupLanguage();
		if(markupLanguage == null) {
			markupLanguage = System.getProperty("org.concord.portfolio.markup", null);
		}
		
		String bodyText = pfDocument.getDocumentText();
		bodyText = substituteIncludables(bodyText);
		
		// default to html viewer for now
		// FIXME the handling of the plain markup is to test the new view entry code
		// it isn't quite valid because plain text might have html chars in it
		// so it will get parsed incorrectly.
		if(markupLanguage == null ||  
				markupLanguage.equals(OTDocument.MARKUP_PFHTML) || 
				markupLanguage.equals(OTDocument.MARKUP_PLAIN)) {
			if(editorPane == null) {
				editorPane = new JEditorPane();
				OTHTMLFactory kitViewFactory =
					new OTHTMLFactory(pfDocument, this);
                OTDocumentEditorKit editorKit = 
                    new OTDocumentEditorKit(pfDocument, kitViewFactory);
				editorPane.setEditorKit(editorKit);
				editorPane.setEditable(false);
				editorPane.addHyperlinkListener(this);
			}
			bodyText = htmlizeText(bodyText);

			// when this text is set it will recreate all the 
			// OTDocumentObjectViews, so we need to clear and 
			// close all the old panels first
			removeAllSubViews();
			
			editorPane.setText(bodyText);
			
			
			previewComponent = editorPane;
			editorPane.setCaretPosition(0);
		} else {
		    System.err.println("xhtml markup not supported");
		}

		if(parsedTextArea == null) {
			parsedTextArea = new JTextArea();
		}
		parsedTextArea.setText(bodyText);
		
		return bodyText;
	}
	
    public String escapeReplacement(String replacement)
    {
        if(replacement == null) {
            return null;            
        }
        
        // escape $ and \ incase these are used in the text
        // we need 8 backslashes here because
        // first java compiler strips off half so it is now
        // "\\\\"
        // then regex replacer strips off half so it is now
        // "\\"
        // and that is what we want in the replacement so the 
        // the next replacer turns it into a "\" again. :)
        replacement = replacement.replaceAll("\\\\", "\\\\\\\\");

        // We need 6 backslashes because
        // first the java compiler strips off half of them so the sting
        // becomes:  \\\$
        // then the replacer uses the backslash as a quote, and the $
        // character is used to reference groups of characters, so it 
        // must be escaped.  So the 1st two are turned into one, and the
        // 3rd one escapes the $.  So the end result is:
        // \$
        // We need this \$ because the replacement below is going to
        // parse the $ otherwise
        replacement = replacement.replaceAll("\\$", "\\\\\\$");
        
        return replacement;        
    }
    
	public String getIncludableReplacement(String idStr)
	{
		
		// lookup the object at this id	    	   
		OTObject referencedObject = pfDocument.getReferencedObject(idStr);
		if(referencedObject == null) {
			return "$0";
		}
		
        // see if it has an OTXHTMLView 
        OTXHTMLView xhtmlView = (OTXHTMLView)
            getViewFactory().getView(referencedObject, OTXHTMLView.class);
        if(xhtmlView != null) {
            String replacement = xhtmlView.getXHTMLText(referencedObject);
            if(replacement == null) {
                // this is an empty embedded object
                System.err.println("empty embedd obj: " + idStr);
                return "";
            } 
            return escapeReplacement(replacement);
        }
        
        return "$0";
        /*
        // this will be deprecated
		// see if is one of the incluables (PfQuery)
		// replace the object reference with the appropriate stuff
		if(!(referencedObject instanceof PfQuery)){
			return "$0";
		}
		
		String replacement = ((PfQuery)referencedObject).getDocumentText();
		
        return escapeReplacement(replacement);
        */        
	}
	
	public String substituteIncludables(String inText)
	{
		if(inText == null) {
			return null;
		}
		
		
		Pattern editablePattern = Pattern.compile("editable=\"([^\"]*)\"");
		
		Pattern p = Pattern.compile("<object refid=\"([^\"]*)\"[^>]*>");
		Matcher m = p.matcher(inText);
		StringBuffer parsed = new StringBuffer();
		while(m.find()) {
			String id = m.group(1);
			
			// FIXME
			// check if it has editable attribute
			String tag = m.group(0);
			
			
			String replacement = getIncludableReplacement(id);
						
			try {
				m.appendReplacement(parsed, replacement);
			} catch (IllegalArgumentException e) {
				System.err.println("bad replacement: " + replacement);
				e.printStackTrace();
			}
		}
		m.appendTail(parsed);
		return parsed.toString();		
	}
	
	public String htmlizeText(String inText)
	{
		if(inText == null) {
			return null;
		}

		inText = inText.replaceAll("<p[ ]*/>", "<p></p>");
		return inText.replaceAll("<([^>]*)/>", "<$1>");
		
		/*
		Pattern p = Pattern.compile("<([^>]*)/>");
		Matcher m = p.matcher(inText);
		StringBuffer parsed = new StringBuffer();
		while(m.find()) {
			String tagBody = m.group(1);
			
			// We need 6 backslashes because
			// first the java compiler strips off half of them so the sting
			// becomes:  \\\$
			// then the replacer uses the backslash as a quote, and the $
			// character is used to reference groups of characters, so it 
			// must be escaped.  So the 1st two are turned into one, and the
			// 3rd one escapes the $.  So the end result is:
			// \$
			// We need this \$ because the replacement below is going to
			// parse the $ otherwise
			tagBody = tagBody.replaceAll("\\$", "\\\\\\$");
			try {
				m.appendReplacement(parsed, "<$1>" + tagBody + ">");
			} catch (IllegalArgumentException e) {
				System.err.println("bad tag: " + tagBody);
				e.printStackTrace();
			}
		}
		m.appendTail(parsed);
		return parsed.toString();
		*/
	}
	
	public Document parseString(String text, String systemId)
	{
		try {
			if(xmlDocumentFactory == null) {
				xmlDocumentFactory = DocumentBuilderFactory.newInstance();
				xmlDocumentFactory.setValidating(true);
				xmlDocumentBuilder = xmlDocumentFactory.newDocumentBuilder();
				
				// TODO Fix this
				xmlDocumentBuilder.setErrorHandler(new DefaultHandler());
			}
	
			text = XHTML_PREFIX + text + XHTML_SUFFIX;
			
			StringReader stringReader = new StringReader(text);
			InputSource inputSource = new InputSource(stringReader);
			inputSource.setSystemId(systemId);
			return xmlDocumentBuilder.parse(inputSource);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent event) 
	{
		//System.out.println(this+" -- TABS stateChanged");
		
		updateFormatedView();		
	}
	
	public void hyperlinkUpdate(HyperlinkEvent e) 
	{
		
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				String linkTarget = e.getDescription();
				if(linkTarget.startsWith("http") || linkTarget.startsWith("file")){
					try {
						// FIXME this should be changed to be a service 
						// so external links can work in both a jnlp 
						// env and a regular application env
					    Class serviceManager = Class.forName("javax.jnlp.ServiceManager");
					    Method lookupMethod = serviceManager.getMethod("lookup", new Class[]{String.class});					    
					    Object basicService = lookupMethod.invoke(null, new Object[]{"javax.jnlp.BasicService"});
					    Method showDocument = basicService.getClass().getMethod("showDocument", new Class[]{URL.class});
					    showDocument.invoke(basicService, new Object[]{new URL(linkTarget)});
					    return;
					} catch (Exception exp) {
					    System.err.println("Can't open external link.");
					    exp.printStackTrace();
					}				    
				}
				
				
			    OTObject linkObj = pfDocument.getReferencedObject(linkTarget);
				if(linkObj == null) {
					System.err.println("Invalid link: " + e.getDescription());
					return;
				}
				
				Element aElement = e.getSourceElement();
				AttributeSet attribs = aElement.getAttributes();

                // this is a hack because i don't really know what is going on here
				AttributeSet tagAttribs = (AttributeSet)attribs.getAttribute(HTML.Tag.A);
				String target = (String)tagAttribs.getAttribute(HTML.Attribute.TARGET);

				if(target == null) {
					getViewContainer().setCurrentObject(linkObj);
					
				} else {
					// they want to use a frame
					OTFrame targetFrame = null;
					
					// get the frame object
					// modify setCurrentObject to take a frame object
					// then at the top level view container deal with this object
					targetFrame = (OTFrame)pfDocument.getReferencedObject(target);

					if(targetFrame == null) {
						System.err.println("Invalid link target attrib: " + target);
						return;
					}					
					
					getFrameManager().putObjectInFrame(linkObj, targetFrame);
				}
				
				
			} catch (Throwable t) {
				t.printStackTrace();
			}
		} 
	}

    public String getXHTMLText(OTObject otObject) 
    {
        // this is a bit of a hack
        // the sequence of initializing the object should be the same
        // if this being used xhtmlText view or as a swing component 
        pfDocument = (OTDocument)otObject;
        return updateFormatedView();
    }

    public String getXHTMLImageText(OTXHTMLHelper helper, float containerDisplayWidth, float containerHeight) 
    {
        // TODO Auto-generated method stub
        return null;
    }

}
