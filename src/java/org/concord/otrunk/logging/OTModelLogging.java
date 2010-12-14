package org.concord.otrunk.logging;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTObjectList;

public interface OTModelLogging extends OTObjectInterface
{
	/**
	 * Sequential list of OTModelEvent objects
	 * @return
	 */
	public OTObjectList getLog();
}
