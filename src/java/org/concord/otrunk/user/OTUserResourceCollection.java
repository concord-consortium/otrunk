/**
 * 
 */
package org.concord.otrunk.user;

import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.otrunk.datamodel.OTDataObject;

/**
 * @author scott
 *
 */
public abstract class OTUserResourceCollection 
	implements OTResourceCollection
{
	private Class collectionType;
	private OTUserDataObject parent;
	private OTResourceCollection authoredCollection;
	private String resourceName;
	
	public OTUserResourceCollection(Class collectionType, 
			OTUserDataObject parent,
			OTResourceCollection authoredCollection,
			String resourceName)
	{
		this.collectionType = collectionType;
	    this.parent = parent;
		this.authoredCollection = authoredCollection;
		this.resourceName = resourceName;		
	}
	
	protected OTResourceCollection getExistingUserCollection()
	{
	    OTDataObject userState = parent.getExistingUserObject();
	    if(userState == null) {
	        return null;
	    }
	    
	    Object oldCollection = userState.getResource(resourceName);
        return (OTResourceCollection)oldCollection;	    
	}
	
	protected OTResourceCollection getUserCollection()
	{
	    OTDataObject userState = parent.getUserObject();
	    Object oldCollection = userState.getResource(resourceName);
	    if(oldCollection != null) {
	        return (OTResourceCollection)oldCollection;
	    }

	    OTResourceCollection userCollection = 
	    	(OTResourceCollection)userState.getResourceCollection(
	    			resourceName, collectionType);
	    if(authoredCollection != null) {
	    	copyInto(userCollection, authoredCollection);
	    	
	    }
	    return userCollection;
	}
	
	protected OTResourceCollection getCollectionForRead()
	{
	    OTResourceCollection userCollection = getExistingUserCollection();
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
	
	protected abstract void copyInto(OTResourceCollection userCollection,
			OTResourceCollection authoredCollection);
	
	public int size()
	{
		OTResourceCollection collectionForRead = getCollectionForRead();

		if(collectionForRead == null) return 0;

		return collectionForRead.size();		
	}

	public void removeAll()
	{
	    getUserCollection().removeAll();
	}

}
