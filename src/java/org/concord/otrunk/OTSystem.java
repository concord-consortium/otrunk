/*
 * Created on Jan 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface OTSystem extends OTObject 
{
	OTObject getRoot();
	
	OTObjectList getServices();	
}
