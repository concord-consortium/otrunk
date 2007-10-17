package org.concord.otrunk.view.document;

import java.net.URL;
import org.concord.framework.otrunk.OTObjectInterface;

public interface OTCssText
    extends OTObjectInterface
{
	public String getCssText();
	public URL getSrc();
	public void setCssText(String text);
}
