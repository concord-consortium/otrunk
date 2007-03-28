/**
 * 
 */
package org.concord.otrunk.view.prototype;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTChangeNotifying;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.otrunk.OTrunkUtil;

/**
 * @author scott
 *
 */
public class OTPrototypeEventMappingHelper 
{
	OTObject model;
	OTObject prototypeCopy;
	OTPrototypeEventController.ResourceSchema resources;
	OTChangeListener prototypeCopyListener;
	OTChangeListener modelListener;
	
	public OTPrototypeEventMappingHelper(OTObject model, OTObject prototypeCopy,
			OTPrototypeEventController.ResourceSchema resources)
	{
		this.model = model;
		this.prototypeCopy = prototypeCopy;
		this.resources = resources;
		
		OTObjectList mapping = resources.getMapping();
		
		// update the prototypeCopy with values from the model
		for(int i=0; i<mapping.size(); i++){
			OTPrototypeMapEntry entry = (OTPrototypeMapEntry) mapping.get(i);
			
			updateValue(model, entry.getModelProperty(), prototypeCopy,
					entry.getPrototypeProperty(),null);
		}
		
		setupListeners();
	}
	
	protected void setupListeners()
	{
		// create our listeners
		prototypeCopyListener = new OTChangeListener() {
			public void stateChanged(OTChangeEvent e) {
				// currently this updates all the mappings on each
				// change event, it should look at the source of the event
				// and the property.
				OTObjectList mapping = resources.getMapping();
				
				for(int i=0; i<mapping.size(); i++){
					OTPrototypeMapEntry entry = 
						(OTPrototypeMapEntry) mapping.get(i);
					
					updateValue(prototypeCopy, entry.getPrototypeProperty(),
							model, entry.getModelProperty(),
							modelListener);					
				}
				
			}			
		};
		
		modelListener = new OTChangeListener(){
			public void stateChanged(OTChangeEvent e) {

				// currently this updates all the mappings on each
				// change event, it should look at the source of the event
				// and the property.
				OTObjectList mapping = resources.getMapping();

				for(int i=0; i<mapping.size(); i++){
					OTPrototypeMapEntry entry = 
						(OTPrototypeMapEntry) mapping.get(i);
					
					updateValue(model, entry.getModelProperty(),
							prototypeCopy, entry.getPrototypeProperty(),
							prototypeCopyListener);					
				}
			}			
		};

		// add listeners to the model and to the copy of the prototype
		// FIXME this needs to be recursive based on the properties being mapped.
		((OTChangeNotifying)model).addOTChangeListener(modelListener);
		((OTChangeNotifying)prototypeCopy).addOTChangeListener(prototypeCopyListener);
	}
	
	/**
	 * Update a value from a source object and set it in the destination
	 * object.  
	 * 
	 * @param src
	 * @param srcProperty
	 * @param dest
	 * @param destProperty
	 * @param destListener
	 */
	protected final static void updateValue(OTObject src, String srcProperty,
			OTObject dest, String destProperty, OTChangeListener destListener)
	{
		try {
			Object propertyValue = 
				OTrunkUtil.getPropertyValue(srcProperty, src);
			// remove ourselves as a listener on the relavent object 
			// argh!!
			// FIXME this removes us on the root object which might not
			// be the one being changed
			if(destListener != null) {
				((OTChangeNotifying)dest).removeOTChangeListener(destListener);
			}
			OTrunkUtil.setPropertyValue(destProperty, dest, propertyValue);
			if(destListener != null){
				((OTChangeNotifying)dest).addOTChangeListener(destListener);
			}
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * 
	 */
	public void removeAllListeners()
	{
		((OTChangeNotifying)model).removeOTChangeListener(modelListener);
		((OTChangeNotifying)prototypeCopy).removeOTChangeListener(prototypeCopyListener);		
	}
}
