/**
 * 
 */
package org.concord.otrunk.view;

import javax.swing.JComponent;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;
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
		if(view == null) {
			throw new IllegalArgumentException("view can't be null");
		}
		this.xhtmlView = view;
		
		if(object == null) {
			throw new IllegalArgumentException("object can't be null");
		}
		this.xhtmlObject =object;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.document.OTDocumentView#getComponent(org.concord.framework.otrunk.OTObject, boolean)
	 */
	public JComponent getComponent(OTObject otObject) 
	{
		// FIXME this should be replaced with something like the prototype view setup
		// An actual OTCompoundDoc should be created and its properties set, and
		// then listeners can be added to it, to monitor changes in both directions
		// This concept of wrapping one view type with another should also be more generically
		// controlled, so new view types can be added and wrappers can be made.		
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

			public String otExternalId()
            {
				return xhtmlObject.otExternalId();
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
		};
		
		return super.getComponent(doc);
	}
}
