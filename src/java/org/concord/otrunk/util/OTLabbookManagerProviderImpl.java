package org.concord.otrunk.util;

import java.util.HashMap;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.view.OTLabbookManager;
import org.concord.framework.otrunk.view.OTLabbookManagerProvider;
import org.concord.otrunk.datamodel.OTTransientMapID;

public class OTLabbookManagerProviderImpl
    implements OTLabbookManagerProvider
{
	private OTLabbookBundle bundle;
	private HashMap<OTObjectService, OTLabbookManager> managerMap;

	public OTLabbookManagerProviderImpl(OTLabbookBundle bundle)
    {
	    this.bundle = bundle;
    }

	public OTLabbookManager getLabbookManager(OTObjectService objectService)
	{
		if (managerMap == null){
			managerMap = new HashMap<OTObjectService, OTLabbookManager>();
		}
		
		if (managerMap.get(objectService) != null){
			return managerMap.get(objectService);
		}
		
        try {
        	OTID bundleId = bundle.getGlobalId();
        	OTLabbookManager manager;
        	if (bundleId instanceof OTTransientMapID){
        		manager = new OTLabbookManagerImpl(bundle.getResources());
        	} else {
        		OTLabbookBundle learnerBundle = (OTLabbookBundle) objectService.getOTObject(bundle.getGlobalId());
        		manager = new OTLabbookManagerImpl(learnerBundle.getResources());
        	}
        	managerMap.put(objectService, manager);
    		return manager;
        } catch (Exception e) {
	        e.printStackTrace();
        }
        System.err.println("Cannot create OTLabbookBundle from object service");
        return null;
	}

}
