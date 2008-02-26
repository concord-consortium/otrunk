/**
 * 
 */
package org.concord.otrunk.view;

import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTJComponentService;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTJComponentViewContext;
import org.concord.framework.otrunk.view.OTJComponentViewContextAware;
import org.concord.framework.otrunk.view.OTView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerAware;
import org.concord.framework.otrunk.view.OTViewContext;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.framework.otrunk.view.OTXHTMLView;

/**
 * @author scott
 *
 */
public class OTJComponentServiceImpl implements OTJComponentService 
{
	OTViewFactory viewFactory;
	
	// For now we'll keep these in a regular hashtable we might need to do
	// some weak referenceing here
	HashMap objToView = new HashMap();
	HashMap objToComponent = new HashMap();
	
	public OTJComponentServiceImpl(OTViewFactory viewFactory)
	{
		this.viewFactory = viewFactory;
	}
	
	public JComponent getComponent(OTObject otObject,
		OTViewContainer container) 
	{
        OTJComponentView view = getObjectView(otObject, container);

        if(view == null) {
            return new JLabel("No view for object: " + otObject);
        }

        return getComponent(otObject, view);
	}
	
	public JComponent getComponent(OTObject otObject, OTJComponentView view)
	{
		JComponent component = view.getComponent(otObject);
		objToComponent.put(otObject, component);
		return component;
	}

	public OTJComponentView getObjectView(OTObject otObject,
			OTViewContainer container) 
	{
		return getObjectView(otObject, container, null, null);
	}
	
	public OTJComponentView getObjectView(OTObject otObject, OTViewContainer container, 
	                                      String mode) 
	{
		return getObjectView(otObject, container, mode, null);
	}
	
	public OTJComponentView getObjectView(OTObject otObject, OTViewContainer container, 
        String mode, OTViewEntry viewEntry)
    {
    		return getObjectView(otObject, container, mode, viewEntry, null);
    }

	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.view.OTJComponentService#getObjectView(org.concord.framework.otrunk.OTObject, org.concord.framework.otrunk.view.OTViewContainer, java.lang.String, org.concord.framework.otrunk.view.OTViewEntry)
     */
    public OTJComponentView getObjectView(OTObject otObject, OTViewContainer container, 
                                          String mode, OTViewEntry viewEntry, OTViewContext passedViewContext)
    {
    	OTView genericView = null;
    	if(viewEntry != null) {
    		genericView = viewFactory.getView(otObject, viewEntry, mode);
    	} else {
    		genericView = viewFactory.getView(otObject, OTJComponentView.class, mode);
        	if(genericView == null) {
        		// we couldn't find the OTJComponentView, look for any type of view
        		System.err.println("No OTJComponentView for object, will try for any view");
        		System.err.println("  obj: " + otObject);
        		System.err.println("  mode: " + mode);
        		genericView = viewFactory.getView(otObject, OTView.class, mode);
        	}
    	}

    	if(genericView == null) {
    		System.err.println("Cannot find view for object");
    		System.err.println("  obj: " + otObject);
    		System.err.println("  mode: " + mode);
    		System.err.println("  viewEntry: " + viewEntry);
    		return null;
    	}

    	OTJComponentView view = null;

    	if(genericView instanceof OTJComponentView){
    		view = (OTJComponentView) genericView;
    	} else {
    		// FIXME this should abstracted so this code isn't dependent on a particular
    		// XHTML component.  Also it should be abstracted so new translations can
    		// be plugged in for example a SWT translation.
    		if(genericView instanceof OTXHTMLView){
    			// make an OTDocumentView with this as the text
    			// but to maintain the correct lifecycle order this can't
    			// happen until the getComponent is called on the view
    			// so a wrapper view is used which does this on the getComponent method    			
    			OTXHTMLView xhtmlView = (OTXHTMLView) genericView;

    			view = new OTXHTMLWrapperView(xhtmlView, otObject);
    			
    			if (passedViewContext == null){
        			// Because we are making this view ourselves we need to do the
        			// initialization normally done by the viewFactory
        			((OTXHTMLWrapperView)view).setViewContext(
        					viewFactory.getViewContext());		
    			} else {
    				((OTXHTMLWrapperView)view).setViewContext(passedViewContext);		
    			}
    			
    		}

    	}
    	
    	
    	if(view == null){
    		// We could not translate the genericView to a OTJComponentView
    		System.err.println("Could not translate view to OTJComponentView");
    		System.err.println("  obj: " + otObject);
    		System.err.println("  mode: " + mode);
    		System.err.println("  viewEntry: " + viewEntry);
    		return null;
    	}
    	
        if(view instanceof OTViewContainerAware){
        	((OTViewContainerAware)view).setViewContainer(container);
        }
        
        if(view instanceof OTJComponentViewContextAware){
        	((OTJComponentViewContextAware)view).setOTJComponentViewContext(viewContext);
        }        	
        
        objToView.put(otObject, view);
        
        return view;
    }
    
    OTJComponentViewContext viewContext = new OTJComponentViewContext()
    {

		public JComponent getComponentByObject(OTObject obj)
        {
			return (JComponent)objToComponent.get(obj);
        }

		public OTView getViewByObject(OTObject obj)
        {
			return (OTView)objToView.get(obj);
        }
    	
    };

    /**
     * @see org.concord.framework.otrunk.view.OTJComponentService#getJComponentViewContext()
     */
	public OTJComponentViewContext getJComponentViewContext()
    {
	    return viewContext;
    }
	
	public OTViewFactory getViewFactory(){
		return viewFactory;
	}

}
