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
	protected final OTResourceSchemaHandler handler;

	/**
	 * The final keyword is so this can be optimized by the compiler
	 */
	protected final String property;
	
	public OTCollectionImpl(String property, OTResourceSchemaHandler handler)
	{
		this.property = property;
		this.handler = handler;
	}

	/**
	 * The final keyword is so this can be optimized by the compiler
	 */
	protected final void notifyOTChange(String operation, Object object)
	{
		handler.notifyOTChange(property, operation, object);
	}
}
