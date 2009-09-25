package org.concord.otrunk.util;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTBundle;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.OTServiceContext;
import org.concord.framework.otrunk.view.OTLabbookManagerProvider;
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
        
        /**
         * Whether the labbook should embed snapshots in a draw tool or not
         * @return
         */
        public boolean getEmbedInDrawTool();
        public static boolean DEFAULT_embedInDrawTool = true;
    }
	
	ResourceSchema resources;
	OTLabbookManagerProvider labbookManagerProvider;
	
	public OTLabbookBundle(ResourceSchema resources)
    {
	    super(resources);
	    this.resources = resources;
    }

	public void initializeBundle(OTServiceContext serviceContext)
	{
		OTViewFactory viewFactory = 
    		serviceContext.getService(OTViewFactory.class);
    	
    	OTViewContext factoryContext = viewFactory.getViewContext();    	
    	factoryContext.addViewService(OTLabbookManagerProvider.class, labbookManagerProvider);
	}

	public void registerServices(OTServiceContext serviceContext)
	{
		labbookManagerProvider = new OTLabbookManagerProviderImpl(this);
		serviceContext.addService(OTLabbookManagerProvider.class, labbookManagerProvider);
	}
	
	public ResourceSchema getResources(){
		return resources;
	}
	/**
	 * @return Snapshots taken of models or other objects that don't fit into
	 * the other categories
	 */
    public OTObjectList getEntries(){
    	return resources.getEntries();
    }
}
