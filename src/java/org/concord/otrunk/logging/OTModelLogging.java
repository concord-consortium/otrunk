package org.concord.otrunk.logging;

import org.concord.framework.otrunk.OTObjectList;

public interface OTModelLogging
{
	/**
	 * Sequential list of OTModelEvent objects
	 * @return
	 */
	public OTObjectList getLog();
}
