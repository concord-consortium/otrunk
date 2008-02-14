package org.concord.otrunk.util;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTBundle;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.OTServiceContext;
import org.concord.framework.otrunk.view.OTViewContext;
import org.concord.framework.otrunk.view.OTViewFactory;

public class OTSharingBundle extends DefaultOTObject
    implements OTBundle
{

	 public static interface ResourceSchema extends OTResourceSchema {
	    	
	        public OTObjectList getSharedObjects();
	    }
	    
	    ResourceSchema resources;
	    OTSharingManager sharingManager;
	    
	    public OTSharingBundle(ResourceSchema resources)
	    {
	        super(resources);
	        this.resources = resources;
	    }

	    public OTObjectList getSharedObjects()
	    {
	    	return resources.getSharedObjects();
	    }

	    public void registerServices(OTServiceContext serviceContext)
	    {
	    	sharingManager = new OTSharingManagerImpl(resources);
	    	serviceContext.addService(OTSharingManager.class, sharingManager);
	    }
	    
		/* (non-Javadoc)
	     * @see org.concord.framework.otrunk.OTBundle#initializeBundle(org.concord.framework.otrunk.OTServiceContext)
	     */
	    public void initializeBundle(OTServiceContext serviceContext)
	    {
	    	OTViewFactory viewFactory = 
	    		(OTViewFactory) serviceContext.getService(OTViewFactory.class);
	    	
	    	OTViewContext factoryContext = viewFactory.getViewContext();    	
	    	factoryContext.addViewService(OTSharingManager.class, sharingManager);
	    }

}
