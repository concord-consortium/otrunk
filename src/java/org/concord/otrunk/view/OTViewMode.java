/**
 * 
 */
package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.view.OTViewEntry;

/**
 * @author scott
 *
 */
public interface OTViewMode extends OTObjectInterface 
{
	OTViewEntry getDefault();
	
	OTObjectMap getMap();
}
