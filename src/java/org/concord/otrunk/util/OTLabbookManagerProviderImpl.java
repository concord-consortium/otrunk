package org.concord.otrunk.util;

import java.util.HashMap;

import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.view.OTLabbookManager;
import org.concord.framework.otrunk.view.OTLabbookManagerProvider;
import org.concord.otrunk.util.OTLabbookBundle.ResourceSchema;

public class OTLabbookManagerProviderImpl
    implements OTLabbookManagerProvider
{
	private ResourceSchema resources;
	private HashMap managerMap;

	public OTLabbookManagerProviderImpl(OTLabbookBundle.ResourceSchema resources)
    {
	    this.resources = resources;
    }

	public OTLabbookManager getLabbookManager(OTObjectService objectService)
	{
		if (managerMap == null){
			managerMap = new HashMap();
		}
		
		if (managerMap.get(objectService) != null){
			return (OTLabbookManager) managerMap.get(objectService);
		}
		
        try {
        	OTLabbookBundle bundle = (OTLabbookBundle) objectService.getOTObject(resources.getGlobalId());
        	this.resources = bundle.resources;
        	OTLabbookManager manager = new OTLabbookManagerImpl(resources);
        	managerMap.put(objectService, manager);
    		return manager;
        } catch (Exception e) {
	        e.printStackTrace();
        }
        System.err.println("Cannot create OTLabbookBundle from object service");
        return null;
	}

}
