/**
 * 
 */
package org.concord.otrunk;

import org.concord.framework.otrunk.OTCollection;


/**
 * @author scott
 *
 */
public abstract class OTCollectionImpl implements OTCollection
{
	/**
	 * The final keyword is so this can be optimized by the compiler
	 */
	protected final OTObjectInternal objectInternal;

	/**
	 * The final keyword is so this can be optimized by the compiler
	 */
	protected final String property;

	public OTCollectionImpl(String property, OTObjectInternal objectInternal)
	{
		this.property = property;
		this.objectInternal = objectInternal;
	}

	/**
	 * The final keyword is so this can be optimized by the compiler
	 * @param previousObject TODO
	 */
	protected final void notifyOTChange(String operation, Object object, Object previousObject)
	{
		objectInternal.notifyOTChange(property, operation, object, previousObject);
	}

	public boolean isEmpty()
    {
		return size() == 0;
    }
	
	/**
	 * @deprecated use clear instead.
	 */
	public void removeAll() 
	{
		clear();
	}
}
