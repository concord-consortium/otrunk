/**
 * 
 */
package org.concord.otrunk.view;

import java.util.HashMap;
import java.util.logging.Logger;

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
import org.concord.framework.otrunk.view.OTViewContextAware;
import org.concord.framework.otrunk.view.OTViewConversionService;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewFactory;

/**
 * @author scott
 *
 */
public class OTJComponentServiceImpl implements OTJComponentService 
{
	private static final Logger logger =
        Logger.getLogger(OTJComponentServiceImpl.class.getCanonicalName());
	
	OTViewFactory viewFactory;
	
	// For now we'll keep these in a regular hashtable we might need to do
	// some weak referencing here
	HashMap<OTObject, OTJComponentView> objToView = new HashMap<OTObject, OTJComponentView>();
	HashMap<OTObject, JComponent> objToComponent = new HashMap<OTObject, JComponent>();

	private boolean maintainViewMap;
	
	public OTJComponentServiceImpl(OTViewFactory viewFactory, boolean maintainViewMap)
	{
		this.viewFactory = viewFactory;
		this.maintainViewMap = maintainViewMap;
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
		
		if(maintainViewMap){
			objToComponent.put(otObject, component);
		}
		
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
    		return getObjectView(otObject, container, mode, viewEntry, null, null);
    }

	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.view.OTJComponentService#getObjectView(org.concord.framework.otrunk.OTObject, org.concord.framework.otrunk.view.OTViewContainer, java.lang.String, org.concord.framework.otrunk.view.OTViewEntry)
     */
    public OTJComponentView getObjectView(OTObject otObject, OTViewContainer container, 
                                          String mode, OTViewEntry viewEntry, OTViewContext passedViewContext,
                                          OTJComponentViewContext passedJComponentViewContext)
    {
    	OTView genericView = null;
    	if(viewEntry != null) {
    		genericView = viewFactory.getView(otObject, viewEntry, mode);
    	} else {
    		genericView = viewFactory.getView(otObject, OTJComponentView.class, mode);
        	if(genericView == null) {
        		genericView = viewFactory.getView(otObject, OTView.class, mode);
        		
        		OTViewContext viewContext2 = viewFactory.getViewContext();
        		OTViewConversionService conversionService = 
        			viewContext2.getViewService(OTViewConversionService.class);
        		
        		// check if we can handle translating this to a OTJComponentView
        		// currently only OTXHTMLViews can be translated
        		if(conversionService == null ||
        				(conversionService != null && 
        				!conversionService.canConvert(genericView, OTJComponentView.class))){
        			if(conversionService == null){
        				logger.warning("No OTViewConversionService available");
        			}
        			logger.warning("No OTJComponentView or compatible view for the object\n" +
        			               "  obj: " + otObject + "\n" +
        			               "  mode: " + mode);
        		} 
        	}
    	}

    	if(genericView == null) {
    		logger.warning("Cannot find view for object\n" +
    		               "  obj: " + otObject + "\n" +
    		               "  mode: " + mode + "\n" + 
    		               "  viewEntry: " + viewEntry);
    		return null;
    	}

    	OTJComponentView view = null;

    	if(genericView instanceof OTJComponentView){
    		view = (OTJComponentView) genericView;
    	} else {
    		OTViewContext viewContext2 = viewFactory.getViewContext();
    		OTViewConversionService conversionService = 
    			viewContext2.getViewService(OTViewConversionService.class);

    		if(conversionService == null){
				logger.warning("No OTViewConversionService available");
    		} else {    			
    			view = conversionService.convert(genericView, OTJComponentView.class,
    				viewFactory, viewEntry);
    		} 
    	}
    	
    	
    	if(view == null){
    		// We could not translate the genericView to a OTJComponentView
    		logger.warning("Could not convert genericView to OTJComponentView\n" +
    		               "  obj: " + otObject + "\n" +
    		               "  genericView: " + genericView + "\n" +
    		               "  mode: " + mode + "\n" +
    		               "  viewEntry: " + viewEntry);
    		return null;
    	}
    	
        if(view instanceof OTViewContainerAware){
        	((OTViewContainerAware)view).setViewContainer(container);
        }
        
        if(view instanceof OTJComponentViewContextAware){
        	if (passedJComponentViewContext == null){
        		((OTJComponentViewContextAware)view).setOTJComponentViewContext(viewContext);
        	} else {
        		((OTJComponentViewContextAware)view).setOTJComponentViewContext(passedJComponentViewContext);
        	}
        }
        
        // This will actually override the viewContext that was set by the view factory.
        if (view instanceof OTViewContextAware){
        	if (passedViewContext != null){
        		((OTViewContextAware)view).setViewContext(passedViewContext);
        	}
        }
        
        if(maintainViewMap){
        	objToView.put(otObject, view);
        }
        
        return view;
    }
    
    OTJComponentViewContext viewContext = new OTJComponentViewContext()
    {

		public JComponent getComponentByObject(OTObject obj)
        {
			return objToComponent.get(obj);
        }

		public OTJComponentView getViewByObject(OTObject obj)
        {
			return objToView.get(obj);
        }

		public Object[] getAllObjects()
        {
	        return  objToView.keySet().toArray();
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
