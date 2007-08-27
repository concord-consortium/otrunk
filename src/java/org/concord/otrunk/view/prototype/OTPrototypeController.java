/**
 * 
 */
package org.concord.otrunk.view.prototype;

import org.concord.framework.otrunk.OTObject;

/**
 * @author scott
 *
 */
public interface OTPrototypeController extends OTObject 
{
	PrototypeControllerInstance createControllerInstance();	
}
