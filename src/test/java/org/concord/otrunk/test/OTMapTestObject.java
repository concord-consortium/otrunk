package org.concord.otrunk.test;
import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTResourceMap;


public interface OTMapTestObject extends OTObjectInterface 
{
	public OTResourceMap getResourceMap();
	
	public OTObjectMap getObjectMap();
}