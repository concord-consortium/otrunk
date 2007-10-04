package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTXHTMLView;

public class OTXHTMLTestView
    implements OTXHTMLView
{

	public String getXHTMLText(OTObject otObject)
    {
		String text = 
			"Test XHTMLView<br/>\n" +
			"Name of object: " + otObject.getName() + "<br/>\n" + 
			"The following Link should refresh this page<br/>\n" +
			"<a href=\"" + otObject.getOTObjectService().getExternalID(otObject) + "\">Link to ourselves<a/>";  
	    return text;
    }

}
