/**
 * 
 */
package org.concord.otrunk;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTChangeNotifying;
import org.concord.framework.otrunk.OTObject;

/**
 * @author scott
 *
 */
public class TraceListener implements OTObjectServiceListener, OTChangeListener 
{
	String label;
	
	/**
	 * @param label
	 */
	public TraceListener(String label) 
	{
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.OTObjectServiceListener#objectLoaded(org.concord.framework.otrunk.OTObject)
	 */
	public void objectLoaded(OTObject object) 
	{
		if(object instanceof OTChangeNotifying) {
			((OTChangeNotifying)object).addOTChangeListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTChangeListener#stateChanged(org.concord.framework.otrunk.OTChangeEvent)
	 */
	public void stateChanged(OTChangeEvent e) 
	{
		System.out.println("otchange(" + label + "): " + 
				e.getDescription());
	}
}
