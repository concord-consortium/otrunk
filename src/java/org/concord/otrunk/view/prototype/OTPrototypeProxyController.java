/**
 * 
 */
package org.concord.otrunk.view.prototype;

import java.lang.reflect.Proxy;

import javax.swing.JComponent;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTViewFactory;

/**
 * @author scott
 *
 */
public class OTPrototypeProxyController extends DefaultOTObject implements
		OTPrototypeController, OTPrototypeProxyMapping
{
	
	public OTPrototypeProxyController(OTResourceSchema resources) {
		super(resources);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTPrototypeViewController#getComponent(org.concord.otrunk.view.OTPrototypeViewConfig, org.concord.framework.otrunk.view.OTViewFactory)
	 */
	public JComponent getComponent(OTObject otObject, String prototypeCopyKey, 
		String defaultModelProperty, 
		OTPrototypeViewEntry config, OTViewFactory otViewFactory) {
		OTPrototypeProxyInvocationHandler invokeHandler =
			new OTPrototypeProxyInvocationHandler(config.getPrototype(), 
					otObject, this);

		OTObject proxy = (OTObject)Proxy.newProxyInstance(getClass().getClassLoader(), 
				config.getPrototype().getClass().getInterfaces(), invokeHandler);
		
		OTJComponentView currentView =
			(OTJComponentView)otViewFactory.getView(proxy, config.getViewEntry());
		
		/**
		 * Should set these things here
		 * but this code needs to go in a common place
		 * so we stop doing it over and over
		if(currentView instanceof OTViewContainerAware){
			((OTViewContainerAware)currentView).
			setViewContainer(viewContainer);
		}			        
			
		if(currentView instanceof OTFrameManagerAware){
			((OTFrameManagerAware)currentView).setFrameManager(frameManager);
		}
		 */

		if(currentView == null){
			return null;
		}
		return currentView.getComponent(proxy, false);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTProxyMapping#getProperty(java.lang.String)
	 */
	public String getProperty(String property) {
		// hack to test our invocation class
		if("text".equals(property)) {
			return "text";
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.prototype.OTPrototypeController#close()
	 */
	public void close()
	{
		// TODO Auto-generated method stub
		
	}

}
