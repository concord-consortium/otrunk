/**
 * 
 */
package org.concord.otrunk.view.prototype;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectMap;
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
	
	/**
	 * For each object this view is used for, a copy is made of the prototype
	 * object. That copied object is the one modified by the view, these 
	 * copies are saved in the prototypeCopies variable, so if they have 
	 * properties that are not saved in the model object those properties
	 * will still be saved in the prototype copy.
	 * 
	 * The problem with this is that some of the objects referenced by the prototype
	 * might be large.  And should not changed from view instance to view instance.  Perhaps
	 * an option is to move these objects outside and map them into the view.  But this means
	 * properties have to be added to the model object to store these objects.
	 * 
	 * The boolean below turns off copying to help with this situation a little
	 * 
	 * @return
	 */
	public OTObjectMap getPrototypeCopies();			
	
	/**
	 * Turn off the copying of the prototype, see above for information about this copying.
	 * @return
	 */		
	public boolean getCopyPrototype();
	public final static boolean DEFAULT_copyPrototype = true;

	
	public static String DEFAULT_viewClass="org.concord.otrunk.view.prototype.OTPrototypeView";
}