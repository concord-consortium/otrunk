package org.concord.otrunk.util;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTBundle;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.OTServiceContext;
import org.concord.framework.otrunk.view.OTLabbookManagerProvider;
import org.concord.framework.otrunk.view.OTViewContext;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.otrunk.util.OTLabbookBundle.ResourceSchema.ImageFiletype;

public class OTLabbookBundle extends DefaultOTObject
    implements OTBundle
{

	public static interface ResourceSchema extends OTResourceSchema {
		
		/**
		 * @return Snapshots taken of models or entries on drawings or graphs
		 */
        public OTObjectList getEntries();
        
        /**
         * Whether the labbook should embed snapshots in a draw tool or not
         * @return
         */
        public boolean getEmbedInDrawTool();
        public static boolean DEFAULT_embedInDrawTool = true;
        

        public enum ImageFiletype {PNG, JPG};
        public ImageFiletype getSnapshotFiletype();
        public void setSnapshotFiletype(ImageFiletype imageFiletype);
        public ImageFiletype DEFAULT_snapshotFiletype = ImageFiletype.PNG;
        
        public boolean getLimitEntries();
        public void setLimitEntries(boolean limitEntries);
        public static boolean DEFAULT_limitEntries = false;
        
        public int getLimit();
        public void setLimit(int limit);
        public static int DEFAULT_limit = 10;
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
	 * @return All lab book entries
	 */
    public OTObjectList getEntries(){
    	return resources.getEntries();
    }
    
    public ImageFiletype getSnapshotFiletype(){
    	return resources.getSnapshotFiletype();
    }
    
    public void setSnapshotFiletype(ImageFiletype imageFiletype){
    	resources.setSnapshotFiletype(imageFiletype);
    }
    
}
