/**
 * 
 */
package org.concord.otrunk.view.prototype;

import javax.swing.JComponent;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTViewFactory;

/**
 * @author scott
 *
 */
public interface OTPrototypeController extends OTObject 
{

	/**
	 * @param model 
	 * @param prototypeCopyKey this is a key that can be used to store the prototypeCopy and look it up 
	 *   again.  Normally it is the globalId of the model object, but in some cases the model object is wrapped
	 *   with another object like OTPropertyReference so the prototpeCopy should be keyed to that.  This should
	 *   go away when OTPrototypeView starts doing the copying instead of leaving it up to the OTPrototypeController
	 *   to do it. 
	 * @param defaultModelProperty this can be null in which case the properties of the 
	 *    model should be pulled from info in the config
	 * @param config
	 * @param otViewFactory
	 * @return
	 */
	JComponent getComponent(OTObject model, String prototypeCopyKey, String defaultModelProperty, 
		OTPrototypeViewEntry config, OTViewFactory otViewFactory);

	/**
	 * 
	 */
	void close();

}
