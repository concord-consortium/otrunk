package org.concord.otrunk;

import java.net.URL;

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
		URL url = getHref();
		
		OTrunkImpl otrunkImpl = (OTrunkImpl)getOTObjectService().getOTrunkService(OTrunk.class);
		
		try {
			OTObject refObject = otrunkImpl.getExternalObject(url, getOTObjectService());			
			return refObject;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
