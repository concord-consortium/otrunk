package org.concord.otrunk.view.document;

import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTView;
import org.concord.framework.otrunk.view.OTViewConverter;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.framework.otrunk.view.OTXHTMLView;

public class OTXHTMLViewToJComponentConverter implements OTViewConverter {

	public OTView convert(OTView original, OTViewFactory factory,
			OTViewEntry viewEntry) {
		// make an OTDocumentView with this as the text
		// but to maintain the correct lifecycle order this can't
		// happen until the getComponent is called on the view
		// so a wrapper view is used which does this on the getComponent method    			
		OTXHTMLView xhtmlView = (OTXHTMLView) original;
				
		OTView view = new OTXHTMLWrapperView(factory, viewEntry, xhtmlView);    			
		
		// TODO Auto-generated method stub
		return view;
	}

	public Class<?> getFromType() {
		return OTXHTMLView.class;
	}

	public Class<?> getToType() {
		return OTJComponentView.class;
	}


}
