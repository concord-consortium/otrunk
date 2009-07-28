package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTXMLString;

public interface OTXMLStringTestObject
    extends OTObjectInterface
{
	public OTXMLString getXmlString();

	public void setXmlString(OTXMLString text);
}
