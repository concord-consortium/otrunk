package org.concord.otrunk.util;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTBundle;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.OTServiceContext;
import org.concord.framework.otrunk.view.OTLabbookManager;
import org.concord.framework.otrunk.view.OTViewContext;
import org.concord.framework.otrunk.view.OTViewFactory;

public class OTLabbookBundle extends DefaultOTObject
    implements OTBundle
{

	public static interface ResourceSchema extends OTResourceSchema {
		
		/**
		 * @return Snapshots taken of models or other objects that don't fit into
		 * the other categories
		 */
        public OTObjectList getEntries();
    }
	
	ResourceSchema resources;
	OTLabbookManager labbookManager;
	
	public OTLabbookBundle(ResourceSchema resources)
    {
	    super(resources);
	    this.resources = resources;
    }

	public void initializeBundle(OTServiceContext serviceContext)
	{
		OTViewFactory viewFactory = 
    		(OTViewFactory) serviceContext.getService(OTViewFactory.class);
    	
    	OTViewContext factoryContext = viewFactory.getViewContext();    	
    	factoryContext.addViewService(OTLabbookManager.class, labbookManager);
	}

	public void registerServices(OTServiceContext serviceContext)
	{
		labbookManager = new OTLabbookManagerImpl(resources);
		serviceContext.addService(OTLabbookManager.class, labbookManager);
	}
	
	/**
	 * @return Snapshots taken of models or other objects that don't fit into
	 * the other categories
	 */
    public OTObjectList getEntries(){
    	return resources.getEntries();
    }
}
