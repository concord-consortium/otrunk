/**
 * 
 */
package org.concord.otrunk.view;

import java.util.HashMap;
import java.util.Map;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTView;
import org.concord.framework.otrunk.view.OTViewContext;
import org.concord.framework.otrunk.view.OTViewFactory;

class OTViewContextImpl implements OTViewContext
{
    Map services = new HashMap();
    HashMap properties = new HashMap();
    
    private final OTViewFactoryImpl factory;
	private OTViewContext parent;

	/**
     * @param impl
	 * @param parent 
     */
    OTViewContextImpl(OTViewFactoryImpl impl, OTViewContext parent)
    {
        factory = impl;
        this.parent = parent;
    }

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTViewServiceProvider#getViewService(java.lang.Class)
	 */
	public Object getViewService(Class serviceClass) 
	{
		Object service = services.get(serviceClass);
		if(service != null){
			return service;
		}
		
		// We now look in the parent factory
		if(parent == null){
			return null;
			
		}

		return parent.getViewService(serviceClass);
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTViewFactory#addService(java.lang.Object)
	 */
	public void addViewService(Class serviceClass, Object service) 
	{
		services.put(serviceClass, service);
	}

	public OTViewFactory createChildViewFactory()
    {
		return factory.createChildViewFactory();
    }

	/** 
	 * FIXME
	 * Although this is not implemented here yet, you can probably get around this by 
	 * making your View implement OTJComponentViewContextAware, and then using
	 * OTJComponentViewContext.getViewByObject(OTObject) instead.
	 */
	public OTView getViewByObject(OTObject obj)
    {
		throw new UnsupportedOperationException("not implemented yet");
    }

	public String getProperty(String propertyName)
    {
		String value = (String) properties.get(propertyName);
		if(parent != null && value == null && !properties.containsKey(propertyName)){
			return parent.getProperty(propertyName);
		}
		
		return value;
    }

	public void setProperty(String propertyName, String propertyValue)
    {
		properties.put(propertyName, propertyValue);
    }

	public void unsetProperty(String propertyName)
    {
		properties.remove(propertyName);	    
    }		
}