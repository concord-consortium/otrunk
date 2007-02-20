/**
 * 
 */
package org.concord.otrunk.user;

import org.concord.otrunk.datamodel.OTDataCollection;
import org.concord.otrunk.datamodel.OTDataObject;

/**
 * @author scott
 *
 */
public abstract class OTUserDataCollection 
	implements OTDataCollection
{
	private Class collectionType;
	private OTUserDataObject parent;
	private OTDataCollection authoredCollection;
	private String resourceName;
	
	public OTUserDataCollection(Class collectionType, 
			OTUserDataObject parent,
			OTDataCollection authoredCollection,
			String resourceName)
	{
		this.collectionType = collectionType;
	    this.parent = parent;
		this.authoredCollection = authoredCollection;
		this.resourceName = resourceName;		
	}
	
	protected OTDataCollection getExistingUserCollection()
	{
	    OTDataObject userState = parent.getExistingUserObject();
	    if(userState == null) {
	        return null;
	    }
	    
	    Object oldCollection = userState.getResource(resourceName);
        return (OTDataCollection)oldCollection;	    
	}
	
	protected OTDataCollection getUserCollection()
	{
	    OTDataObject userState = parent.getUserObject();
	    Object oldCollection = userState.getResource(resourceName);
	    if(oldCollection != null) {
	        return (OTDataCollection)oldCollection;
	    }

	    OTDataCollection userCollection = 
	    	(OTDataCollection)userState.getResourceCollection(
	    			resourceName, collectionType);
	    if(authoredCollection != null) {
	    	copyInto(userCollection, authoredCollection);
	    	
	    }
	    return userCollection;
	}
	
	protected OTDataCollection getCollectionForRead()
	{
		OTDataCollection userCollection = getExistingUserCollection();
	    if(userCollection != null) {
	        return userCollection;
	    }
	    
	    if(authoredCollection == null) return null;
	    
		return authoredCollection;
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
	    getUserCollection().removeAll();
	}

}
