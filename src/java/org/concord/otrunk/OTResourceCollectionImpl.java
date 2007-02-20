/**
 * 
 */
package org.concord.otrunk;

import org.concord.otrunk.datamodel.BlobResource;

/**
 * @author scott
 *
 */
public class OTResourceCollectionImpl 
{
	protected final static Object translateToData(Object resource)
	{
		if(resource instanceof byte[]){
			// make a BlobResource
			return new BlobResource((byte[])resource);
		}
		return resource;
	}
	
	protected final static Object translateToResource(Object data)
	{
		if(data instanceof BlobResource){
			return ((BlobResource)data).getBytes();
		}
		return data;
	}
}
