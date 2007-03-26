/**
 * 
 */
package org.concord.otrunk.view.prototype;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTViewEntry;

public interface OTPrototypeViewEntry 
	extends OTViewEntry
{
	public OTObject getPrototype();
	public void setPrototype(OTObject otObject);
	
	public OTViewEntry getViewEntry();
	public void setViewEntry(OTViewEntry viewEntry);

	public OTPrototypeController getController();
	public void setController(OTPrototypeController controller);
}