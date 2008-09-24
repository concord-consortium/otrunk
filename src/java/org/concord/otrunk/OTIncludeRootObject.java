package org.concord.otrunk;

import java.net.URL;
import java.util.logging.Logger;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.OTrunk;

public class OTIncludeRootObject extends DefaultOTObject 
{	
	public static interface ResourceSchema extends OTResourceSchema
	{
		public URL getHref();
	}
	protected ResourceSchema resources;

	OTObject refObject = null;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public OTIncludeRootObject(ResourceSchema resources)
    {
	    super(resources);
	    this.resources = resources;
    }

	public URL getHref()
	{
		return resources.getHref();
	}
	
	public OTObject getReference()
	{
		if(refObject != null){
			return refObject;
		}
		
		URL url = getHref();
		
		OTrunkImpl otrunkImpl = (OTrunkImpl)getOTObjectService().getOTrunkService(OTrunk.class);
		
		try {
			refObject = otrunkImpl.getExternalObject(url, getOTObjectService());
			logger.info("included root object: " + refObject.otExternalId());
			return refObject;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
