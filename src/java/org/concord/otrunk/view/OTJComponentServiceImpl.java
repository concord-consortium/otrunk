/**
 * 
 */
package org.concord.otrunk.view;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTJComponentService;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerAware;
import org.concord.framework.otrunk.view.OTViewFactory;

/**
 * @author scott
 *
 */
public class OTJComponentServiceImpl implements OTJComponentService 
{
	OTViewFactory viewFactory;
	
	public OTJComponentServiceImpl(OTViewFactory viewFactory)
	{
		this.viewFactory = viewFactory;
	}
	
	public JComponent getComponent(OTObject otObject,
			OTViewContainer container, boolean editable) 
	{
        OTJComponentView view = getObjectView(otObject, container);

        if(view == null) {
            return new JLabel("No view for object: " + otObject);
        }
        
        return view.getComponent(otObject, editable);
	}

	public OTJComponentView getObjectView(OTObject otObject,
			OTViewContainer container) 
	{
        OTJComponentView view = 
        	(OTJComponentView)viewFactory.getView(otObject, OTJComponentView.class);
        
        if(view == null) {
            return null;
        }
        
        if(view instanceof OTViewContainerAware){
        	((OTViewContainerAware)view).setViewContainer(container);
        }
        
        return view;
	}
	
	public OTJComponentView getObjectView(OTObject otObject, OTViewContainer container, String mode) {
		OTJComponentView view = 
        	(OTJComponentView)viewFactory.getView(otObject, OTJComponentView.class, mode);
		if(view == null) {
            return null;
        }
        
        if(view instanceof OTViewContainerAware){
        	((OTViewContainerAware)view).setViewContainer(container);
        }
        
        return view;
	}
}
