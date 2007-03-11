/**
 * 
 */
package org.concord.otrunk.view;

import javax.swing.JComponent;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.view.OTXHTMLView;
import org.concord.otrunk.view.document.OTDocument;
import org.concord.otrunk.view.document.OTDocumentView;

/**
 * @author scott
 *
 */
public class OTXHTMLWrapperView extends OTDocumentView 
{
	private OTXHTMLView xhtmlView;
	private OTObject xhtmlObject;

	public OTXHTMLWrapperView(OTXHTMLView view, OTObject object)
	{
		this.xhtmlView = view;
		this.xhtmlObject =object;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.document.OTDocumentView#getComponent(org.concord.framework.otrunk.OTObject, boolean)
	 */
	public JComponent getComponent(OTObject otObject, boolean editable) 
	{
		
		OTDocument doc = new OTDocument() {

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

			public void init() {
				// This should not be called because this is a lifecycle
				// method on an OTDocument.  And this isn't object isn't
				// a real OTObject
				throw new IllegalStateException();
			}

			public void setName(String name) {
				throw new UnsupportedOperationException();
			}			
		};
		
		return super.getComponent(doc, editable);
	}
}
