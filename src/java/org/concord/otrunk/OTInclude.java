package org.concord.otrunk;

import java.net.URL;

import org.concord.framework.otrunk.OTObjectInterface;

public interface OTInclude
    extends OTObjectInterface
{
	public URL getHref();
	public void setHref(URL href);
	
	public String getOtmlId();
	public void setOtmlId(String otmlId);
}
