package org.concord.otrunk.xml.jdom;

import org.concord.otrunk.xml.OTXMLText;
import org.jdom.Text;

public class JDOMText extends JDOMContent
    implements OTXMLText
{
	Text text;
	
	public JDOMText(Text text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text.getText();
	}
	
	public void setText(String str)
	{
		text.setText(str);
	}
}
