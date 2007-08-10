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
		 * The problem with this is that some of the objects referenced by the prototype
		 * might be large.  And should not changed from view instance to view instance.  Perhaps
		 * an option is to move these objects outside and map them into the view.  But this means
		 * properties have to be added to the model object to store these objects.
		 * 
		 * The boolean below turns off copying to help with this situation a little
		 * 
		 * @return
		 */
		public OTObjectMap getPrototypeCopies();			
		
		/**
		 * Turn off the copying of the prototype, see above for information about this copying.
		 * @return
		 */		
		public boolean getCopyPrototype();
		public final static boolean DEFAULT_copyPrototype = true;
	}
	private ResourceSchema resources;
	
	protected WeakHashMap referenceMap;
	OTPrototypeEventMappingHelper helper;
	
	public OTPrototypeEventController(ResourceSchema resources) {
		super(resources);
		this.resources = (ResourceSchema) resources;
		
		referenceMap = new WeakHashMap();
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTPrototypeViewController#getComponent(org.concord.otrunk.view.OTPrototypeViewConfig, org.concord.framework.otrunk.view.OTViewFactory)
	 */
	public JComponent getComponent(OTObject model, String prototypeCopyKey, 
		String defaultModelProperty, 
			OTPrototypeViewEntry config, OTViewFactory otViewFactory) 
	{
		OTObject objectToView = null;
		if(resources.getCopyPrototype()){
			OTObject prototypeCopy = resources.getPrototypeCopies().getObject(prototypeCopyKey);

			// make one if there isn't
			if(prototypeCopy == null) {			
				try {
					prototypeCopy =
						getOTObjectService().copyObject(config.getPrototype(), -1);
					resources.getPrototypeCopies().putObject(prototypeCopyKey, prototypeCopy);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
			objectToView = prototypeCopy;
		} else {
			objectToView = config.getPrototype();
		}
		
		helper = 
			new OTPrototypeEventMappingHelper(model, defaultModelProperty, objectToView, 
					resources);
		
		// return the component for the view 
		OTJComponentView view = (OTJComponentView)
			otViewFactory.getView(objectToView, config.getViewEntry());
		
		JComponent component = view.getComponent(objectToView, false);

		
		// we have a problem with this helper.  It isn't going to be 
		// strongly referenced by anything.  It should stay around as long
		// as the view returned here stays around
		referenceMap.put(component, helper);

		return component;
	}
	
	public void close()
	{
		helper.removeAllListeners();
	}
}
