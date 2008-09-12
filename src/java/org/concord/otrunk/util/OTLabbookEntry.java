package org.concord.otrunk.util;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;

public interface OTLabbookEntry
    extends OTObjectInterface
{
	public OTObject getOTObject();
	public void setOTObject(OTObject object);
	
	public OTObject getOriginalObject();
	public void setOriginalObject(OTObject originalObject);
	
	public OTObject getContainer();
	public void setContainer(OTObject container);
	
	public String getTimeStamp();
	public void setTimeStamp(String timeStamp);
	
	public String getNote();
	public void setNote(String note);
	
	public String getType();
	public void setType(String type);
}
