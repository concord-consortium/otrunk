/**
 * 
 */
package org.concord.otrunk;

import org.concord.otrunk.datamodel.BlobResource;

/**
 * @author scott
 *
 */
public abstract class OTResourceCollectionImpl extends OTCollectionImpl
{
	public OTResourceCollectionImpl(String property, OTObjectInternal handler)
	{
		super(property, handler);
	}

	protected Object translateExternalToStored(Object resource)
	{
		if(resource instanceof byte[]){
			// make a BlobResource
			return new BlobResource((byte[])resource);
		}
		return resource;
	}
	
	protected Object translateStoredToExternal(Object data)
	{
		if(data instanceof BlobResource){
			return ((BlobResource)data).getBytes();
		}
		return data;
	}
}
