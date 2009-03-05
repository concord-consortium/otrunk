package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTXMLString;

public interface OTDocumentTestObject
    extends OTObjectInterface
{
	public OTXMLString getBodyText();
	public OTObjectList getDocumentRefs();

}
