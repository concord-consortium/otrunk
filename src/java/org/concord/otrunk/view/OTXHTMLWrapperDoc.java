/**
 * 
 */
package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.concord.framework.otrunk.view.OTXHTMLView;
import org.concord.otrunk.view.document.OTDocument;

/**
 * @author scott
 *
 */
public class OTXHTMLWrapperDoc implements OTDocument 
{
	private OTXHTMLView xhtmlView;
	private OTObject xhtmlObject;
	
	public OTXHTMLWrapperDoc(OTXHTMLView view, OTObject object)
	{
		if(view == null) {
			throw new IllegalArgumentException("view can't be null");
		}
		this.xhtmlView = view;
		
		if(object == null) {
			throw new IllegalArgumentException("object can't be null");
		}
		this.xhtmlObject =object;
	}
	
	public String getDocumentText() {
		return xhtmlView.getXHTMLText(xhtmlObject);
	}

	public boolean getInput() {
		return false;
	}

	public String getMarkupLanguage() {
		return MARKUP_PFHTML;
	}

	public void setDocumentText(String text) {
		throw new UnsupportedOperationException();
	}

	public OTID getGlobalId() {
		return xhtmlObject.getGlobalId();
	}

	public String getName() {
		return xhtmlObject.getName();
	}

	public OTObjectService getOTObjectService() {
		return xhtmlObject.getOTObjectService();
	}

	public String otExternalId()
	{
		return xhtmlObject.otExternalId();
	}			

	public void init() {
		// This should not be called because this is a lifecycle
		// method on an OTDocument.  And this object isn't
		// a real OTObject
		throw new IllegalStateException();
	}

	public void setName(String name) {
		throw new UnsupportedOperationException();
	}

	public OTClass otClass()
	{
		throw new UnsupportedOperationException();
	}

	public Object otGet(OTClassProperty property)
	{
		throw new UnsupportedOperationException();
	}

	public boolean otIsSet(OTClassProperty property)
	{
		throw new UnsupportedOperationException();
	}

	public void otSet(OTClassProperty property, Object newValue)
	{
		throw new UnsupportedOperationException();
	}

	public void otUnSet(OTClassProperty property)
	{
		throw new UnsupportedOperationException();
	}			
}
