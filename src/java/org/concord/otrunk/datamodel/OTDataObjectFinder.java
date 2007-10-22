package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;

public interface OTDataObjectFinder
{
	OTDataObject findDataObject(OTID id) throws Exception;
}
