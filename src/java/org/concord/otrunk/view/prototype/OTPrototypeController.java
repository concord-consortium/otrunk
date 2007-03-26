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
	 * @param otObject 
	 * @param config
	 * @param otViewFactory
	 * @return
	 */
	JComponent getComponent(OTObject model, OTPrototypeViewEntry config, 
			OTViewFactory otViewFactory);

}
