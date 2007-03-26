/**
 * 
 */
package org.concord.otrunk.view.prototype;

import org.concord.framework.otrunk.OTObjectInterface;

/**
 * This is used by a OTPrototypeView to map a property in
 * the model to a property in the prototype.
 * 
 * @author scott
 *
 */
public interface OTPrototypeMapEntry extends OTObjectInterface 
{
	public String getModelProperty();
	public void setModelProperty(String property);
	
	public String getPrototypeProperty();
	public void setPrototypeProperty(String property);
}
