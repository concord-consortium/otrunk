/**
 * 
 */
package org.concord.otrunk.view;

import java.util.Hashtable;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTView;
import org.concord.framework.otrunk.view.OTViewContext;
import org.concord.framework.otrunk.view.OTViewFactory;

class OTViewContextImpl implements OTViewContext
{
	/**
     * 
     */
    Hashtable services = new Hashtable();

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
	 */
	public OTView getViewByObject(OTObject obj)
    {
		throw new UnsupportedOperationException("not implemented yet");
    }		
}