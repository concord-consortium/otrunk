/**
 * 
 */
package org.concord.otrunk;

import java.util.HashMap;

import org.concord.framework.otrunk.OTServiceContext;

class OTServiceContextImpl implements OTServiceContext
{
	HashMap<Class<?>,Object> serviceMap = new HashMap<Class<?>, Object>();

	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTServiceContext#addService(java.lang.Class, java.lang.Object)
     */
    public <T> void addService(Class<T> serviceClass, T service)
    {
		serviceMap.put(serviceClass, service);        
    }

	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTServiceContext#getService(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass)
    {
		return (T) serviceMap.get(serviceClass);
    }
	
}