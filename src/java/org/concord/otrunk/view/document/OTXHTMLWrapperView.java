package org.concord.otrunk.view.document;

import javax.swing.JComponent;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerAware;
import org.concord.framework.otrunk.view.OTViewContext;
import org.concord.framework.otrunk.view.OTViewContextAware;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.framework.otrunk.view.OTXHTMLView;

/**
 * OTXHTMLWrapperView <br>
 * This class implements OTXHTMLView so the OTMLToXHTMLConverter can property export it.
 * That should be fixed somehow.
 * 
 * <p>
 * Date created: Jan 23, 2009
 * 
 * @author scytacki<p>
 *
 */
public class OTXHTMLWrapperView
    implements OTJComponentView, OTViewContainerAware, OTViewContextAware, OTXHTMLView
{
	private OTDocumentView view;
	private OTXHTMLWrapperDoc doc;
	private OTViewEntry viewEntry;
	private OTXHTMLView xhtmlView;
	private OTViewFactory viewFactory;
	private OTViewContainer container;
	private OTViewContext passedViewContext;

	public OTXHTMLWrapperView(OTViewFactory viewFactory, OTViewEntry viewEntry, OTXHTMLView xhtmlView)
    {
		this.viewFactory = viewFactory;
		this.viewEntry = viewEntry;
		this.xhtmlView = xhtmlView;
    }

	public JComponent getComponent(OTObject otObject)
	{
		if(doc == null){
			doc = new OTXHTMLWrapperDoc(xhtmlView, otObject, viewEntry);

			// we look up a view for the wrapper doc in the default view entries 
			OTJComponentView genericView = viewFactory.getView(doc, OTJComponentView.class, 
				OTViewFactory.NO_VIEW_MODE);
			if(genericView == null || !(genericView instanceof OTDocumentView)){
				System.err.println("No view entry found for OTDocument this is required " +
					" to display OTXHTMLViews in the JComponentView system");
			}   

			view = (OTDocumentView) genericView;
			
		}

		if(container != null){
			view.setViewContainer(container);
		}
		
		if(passedViewContext != null){
			view.setViewContext(passedViewContext);
		}
		
		return view.getComponent(doc);
	}

	public void viewClosed()
	{
		view.viewClosed();
	}

	public void setViewContainer(OTViewContainer container)
    {
		this.container = container;
    }

	public void setViewContext(OTViewContext viewContext)
    {
		this.passedViewContext = viewContext;
    }

	public boolean getEmbedXHTMLView()
    {
		return xhtmlView.getEmbedXHTMLView();
    }

	public String getXHTMLText(OTObject otObject)
    {
		return xhtmlView.getXHTMLText(otObject);
    }
}
