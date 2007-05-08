/**
 * 
 */
package org.concord.otrunk;

import java.util.HashMap;

import org.concord.framework.otrunk.OTServiceContext;

class OTServiceContextImpl implements OTServiceContext
{
	HashMap serviceMap = new HashMap();

	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTServiceContext#addService(java.lang.Class, java.lang.Object)
     */
    public void addService(Class serviceClass, Object service)
    {
		serviceMap.put(serviceClass, service);        
    }

	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTServiceContext#getService(java.lang.Class)
     */
    public Object getService(Class serviceClass)
    {
		return serviceMap.get(serviceClass);
    }
	
}