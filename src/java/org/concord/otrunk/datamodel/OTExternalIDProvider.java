package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;

public interface OTExternalIDProvider
{
	public String getExternalID(OTObject object);
	
	public String getExternalID(OTID otid);
}
