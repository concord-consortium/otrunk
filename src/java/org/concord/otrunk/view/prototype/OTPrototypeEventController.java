/**
 * 
 */
package org.concord.otrunk.view.prototype;

import java.util.WeakHashMap;

import javax.swing.JComponent;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTViewFactory;

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

		/**
		 * For each object this view is used for, a copy is made of the prototype
		 * object. That copied object is the one modified by the view, these 
		 * copies are saved in the viewInstances variable, so if they have 
		 * properties that are not mapped to the model object those properties
		 * will still be saved.    
		 * 
		 * @return
		 */
		public OTObjectMap getPrototypeCopies();			
	}
	private ResourceSchema resources;
	
	protected WeakHashMap referenceMap;
	
	public OTPrototypeEventController(ResourceSchema resources) {
		super(resources);
		this.resources = (ResourceSchema) resources;
		
		referenceMap = new WeakHashMap();
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTPrototypeViewController#getComponent(org.concord.otrunk.view.OTPrototypeViewConfig, org.concord.framework.otrunk.view.OTViewFactory)
	 */
	public JComponent getComponent(OTObject model, OTPrototypeViewEntry config,
			OTViewFactory otViewFactory) 
	{
		// check if there already is a copy of this prototype for this otObject
		String modelId = model.getGlobalId().toString();
		OTObject prototypeCopy = resources.getPrototypeCopies().getObject(modelId);
		
		// make one if there isn't
		if(prototypeCopy == null) {			
			try {
				prototypeCopy =
					getOTObjectService().copyObject(config.getPrototype(), -1);
				resources.getPrototypeCopies().putObject(modelId, prototypeCopy);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		OTPrototypeEventMappingHelper helper = 
			new OTPrototypeEventMappingHelper(model, prototypeCopy, 
					resources);
		
		// return the component for the view 
		OTJComponentView view = (OTJComponentView)
			otViewFactory.getView(prototypeCopy, config.getViewEntry());
		
		JComponent component = view.getComponent(prototypeCopy, false);

		
		// we have a problem with this helper.  It isn't going to be 
		// strongly referenced by anything.  It should stay around as long
		// as the view returned here stays around
		referenceMap.put(component, helper);

		return component;
	}
}
