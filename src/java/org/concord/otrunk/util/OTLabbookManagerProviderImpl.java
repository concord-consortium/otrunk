package org.concord.otrunk.util;

import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.view.OTLabbookManager;
import org.concord.framework.otrunk.view.OTLabbookManagerProvider;
import org.concord.otrunk.util.OTLabbookBundle.ResourceSchema;

public class OTLabbookManagerProviderImpl
    implements OTLabbookManagerProvider
{
	private ResourceSchema resources;

	public OTLabbookManagerProviderImpl(OTLabbookBundle.ResourceSchema resources)
    {
	    this.resources = resources;
    }

	public OTLabbookManager getLabbookManager(OTObjectService objectService)
	{
        try {
        	OTLabbookBundle bundle = (OTLabbookBundle) objectService.getOTObject(resources.getGlobalId());
        	this.resources = bundle.resources;
    		return new OTLabbookManagerImpl(resources);
        } catch (Exception e) {
	        e.printStackTrace();
        }
        System.err.println("Cannot create OTLabbookBundle from object service");
        return null;
	}

}
