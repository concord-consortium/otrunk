/**
 * 
 */
package org.concord.otrunk.view.prototype;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;

/**
 * @author scott
 *
 */
public class OTPrototypeEventController extends DefaultOTObject implements
		OTPrototypeController
{
	public static interface ResourceSchema extends OTResourceSchema
	{		
		public OTObjectList getMapping();
	}
	private ResourceSchema resources;
	
	OTPrototypeEventMappingHelper helper;
	
	public OTPrototypeEventController(ResourceSchema resources) {
		super(resources);
		this.resources = (ResourceSchema) resources;		
	}
		
	public PrototypeControllerInstance createControllerInstance()
	{
		return new PrototypeEventControllerInstance(resources);
	}
	
}
