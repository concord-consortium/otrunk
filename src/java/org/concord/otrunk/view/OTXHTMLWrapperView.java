package org.concord.otrunk.view;

import javax.swing.JComponent;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerAware;
import org.concord.framework.otrunk.view.OTViewContext;
import org.concord.framework.otrunk.view.OTViewContextAware;
import org.concord.framework.otrunk.view.OTXHTMLView;
import org.concord.otrunk.view.document.OTDocumentView;

public class OTXHTMLWrapperView
    implements OTJComponentView, OTXHTMLView, OTViewContainerAware, OTViewContextAware
{
	private OTDocumentView view;
	private OTXHTMLWrapperDoc doc;

	public OTXHTMLWrapperView(OTDocumentView view, OTXHTMLWrapperDoc doc)
    {
		this.view = view;
		this.doc = doc;
    }
	
	public JComponent getComponent(OTObject otObject)
	{
		return view.getComponent(doc);
	}

	public void viewClosed()
	{
		view.viewClosed();
	}

	public boolean getEmbedXHTMLView()
	{
		return view.getEmbedXHTMLView();
	}

	public String getXHTMLText(OTObject otObject)
	{
		return view.getXHTMLText(doc);
	}

	public void setViewContainer(OTViewContainer container)
    {
		view.setViewContainer(container);	    
    }

	public void setViewContext(OTViewContext viewContext)
    {
		view.setViewContext(viewContext);
    }
}
