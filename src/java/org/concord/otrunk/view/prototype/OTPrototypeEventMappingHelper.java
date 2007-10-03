/**
 * 
 */
package org.concord.otrunk.view.prototype;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTChangeNotifying;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
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
	String defaultModelProperty;
	
	public OTPrototypeEventMappingHelper(OTObject model, String defaultModelProperty, 
			OTObject prototypeCopy,
			OTPrototypeEventController.ResourceSchema resources)
	{
		this.model = model;
		this.prototypeCopy = prototypeCopy;
		this.resources = resources;
		this.defaultModelProperty = defaultModelProperty;
		
		OTObjectList mapping = resources.getMapping();
		
		// update the prototypeCopy with values from the model
		for(int i=0; i<mapping.size(); i++){
			OTPrototypeMapEntry entry = (OTPrototypeMapEntry) mapping.get(i);
			
			String modelProperty = entry.getModelProperty();
			if(modelProperty == null){
				modelProperty = defaultModelProperty;
			}
			
			updateValue(model, modelProperty, prototypeCopy,
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
					
					String modelProperty = entry.getModelProperty();
					if(modelProperty == null){
						modelProperty = defaultModelProperty;
					}
					
					updateValue(prototypeCopy, entry.getPrototypeProperty(),
							model, modelProperty,
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
					
					String modelProperty = entry.getModelProperty();
					if(modelProperty == null){
						modelProperty = defaultModelProperty;
					}
					
					updateValue(model, modelProperty,
							prototypeCopy, entry.getPrototypeProperty(),
							prototypeCopyListener);					
				}
			}			
		};

		// add listeners to the model and to the copy of the prototype
		// FIXME this needs to be recursive based on the properties being mapped.
		if(model instanceof OTChangeNotifying){
			((OTChangeNotifying)model).addOTChangeListener(modelListener);
		} else {
			System.err.println("Warning: " + model.otClass().getName() +
			"can't notify about change events");
		}

		if(prototypeCopy instanceof OTChangeNotifying){
			((OTChangeNotifying)prototypeCopy).addOTChangeListener(prototypeCopyListener);			
		} else {
			System.err.println("Warning: " + prototypeCopy.otClass().getName() +
			"can't notify about change events");			
		}
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

			if(propertyValue instanceof OTObjectList){
				OTObjectList srcList = (OTObjectList) propertyValue;
				OTObjectList destList = 
					(OTObjectList) OTrunkUtil.getPropertyValue(destProperty, dest);
				destList.removeAll();
				for(int i=0; i<srcList.size(); i++){
					destList.add(srcList.get(i));
				}					
			} else if(propertyValue instanceof OTObjectMap){
				throw new UnsupportedOperationException("maps aren't handled yet");
			} else {
				OTrunkUtil.setPropertyValue(destProperty, dest, propertyValue);
			}
			if(destListener != null){
				((OTChangeNotifying)dest).addOTChangeListener(destListener);
			}
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			System.err.println("Cannot update property: " + destProperty + " on object: " + dest);
			System.err.println("   from property: " + srcProperty + " on object: " + src);
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
