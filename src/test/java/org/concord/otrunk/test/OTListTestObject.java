package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceList;

public interface OTListTestObject
    extends OTObjectInterface
{
	public OTResourceList getResourceList();
	
	public OTObjectList getObjectList();
}
