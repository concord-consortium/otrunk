/**
 * 
 */
package org.concord.otrunk.overlay;

import org.concord.otrunk.datamodel.OTDataCollection;
import org.concord.otrunk.datamodel.OTDataObject;

/**
 * @author scott
 *
 */
public abstract class CompositeDataCollection 
	implements OTDataCollection
{
	private Class<? extends OTDataCollection> collectionType;
	private CompositeDataObject parent;
	private OTDataCollection baseCollection;
	private String resourceName;
	private boolean composite;
	
	
	public CompositeDataCollection(Class<? extends OTDataCollection> collectionType, 
			CompositeDataObject parent,
			OTDataCollection baseCollection,
			String resourceName, boolean composite)
	{
		this.collectionType = collectionType;
	    this.parent = parent;
		this.baseCollection = baseCollection;
		this.resourceName = resourceName;
		this.composite = composite;
	}
	
	protected OTDataCollection getActiveDeltaCollection()
	{
	    OTDataObject activeDelta = parent.getActiveDeltaObject();
	    if(activeDelta == null) {
	        return null;
	    }
	    
	    Object oldCollection = activeDelta.getResource(resourceName);
        return (OTDataCollection)oldCollection;	    
	}
	
	protected OTDataCollection getCollectionForWrite()
	{
		if(!composite){
			OTDataObject baseObject = parent.getBaseObject();
			return baseObject.getResourceCollection(resourceName, collectionType);
		}
		
	    OTDataObject activeDelta = parent.getOrCreateActiveDeltaObject();
	    Object oldCollection = activeDelta.getResource(resourceName);
	    if(oldCollection != null) {
	        return (OTDataCollection)oldCollection;
	    }

	    OTDataCollection userCollection = 
	    	(OTDataCollection)activeDelta.getResourceCollection(
	    			resourceName, collectionType);
	    if(baseCollection != null) {
	    	copyInto(userCollection, baseCollection);
	    	
	    }
	    return userCollection;
	}
	
	protected OTDataCollection getCollectionForRead()
	{		
		if(!composite){
			OTDataObject baseObject = parent.getBaseObject();
			return baseObject.getResourceCollection(resourceName, collectionType);
		}
		
		OTDataCollection activeDeltaCollection = getActiveDeltaCollection();
	    if(activeDeltaCollection != null) {
	        return activeDeltaCollection;
	    }
	    
	    if(baseCollection == null) return null;
	    
		return baseCollection;
	}
			

	protected Object resolveIDResource(Object object)
	{
		return parent.resolveIDResource(object);
	}
	
	protected abstract void copyInto(OTDataCollection userCollection,
			OTDataCollection authoredCollection);
	
	public int size()
	{
		OTDataCollection collectionForRead = getCollectionForRead();

		if(collectionForRead == null) return 0;

		return collectionForRead.size();		
	}

	public void removeAll()
	{
	    getCollectionForWrite().removeAll();
	}

}
