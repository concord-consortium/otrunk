/**
 * 
 */
package org.concord.otrunk;


/**
 * @author scott
 *
 */
public abstract class OTCollectionImpl 
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
	 */
	protected final void notifyOTChange(String operation, Object object)
	{
		objectInternal.notifyOTChange(property, operation, object);
	}
			
}
