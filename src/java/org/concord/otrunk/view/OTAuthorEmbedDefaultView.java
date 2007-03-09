/**
 * 
 */
package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTRequestedViewEntryAware;
import org.concord.framework.otrunk.view.OTViewConfigAware;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTXHTMLView;

/**
 * @author scott
 *
 */
public class OTAuthorEmbedDefaultView 
	implements OTXHTMLView, OTViewConfigAware, OTRequestedViewEntryAware
{
	private OTAuthorEmbedDefaultViewConfig viewConfig;
	private OTViewEntry requestedViewEntry;

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTXHTMLView#getXHTMLText(org.concord.framework.otrunk.OTObject)
	 */
	public String getXHTMLText(OTObject otObject) 
	{
		// Need another aware interface to pass the original
		// view entry to this new view entry if the mode was changed
		// that way the view entry can be passed on if this view needs
		// to access other modes.
		
		// we need to handle substituting the object if it is a OTXHTMLView itself
		// the code to implement this should be extracted from OTDocumentView
		// so views like this can use it.
				
		// the problem with just putting in a object reference is that this object
		// reference need to point to the regular viewEntry not one with the mode
		// of the document.  I can think up 2 solutions:
		//  - add a mode attribute to the object element so this code can explicitly
		//    set the mode to be nothing
		//  - add some inner document formating.  So this chunk of text remains associated
		//    with this view, so when objects are referenced in this chunk of text
		//    this view is used to get the references.  
		// the first one seems the most easy to debug so we'll start there.
		
		String linkViewIdStr = "";
		if(requestedViewEntry != null){
			linkViewIdStr = "viewid=\"" + requestedViewEntry.getGlobalId() + "\" ";
		}
		
		String text = "<table>" +
				"<tr><td><object refid=\"" + otObject.getGlobalId() + "\" " +
						 "mode=\"\" />" +
				"</tr></td>" +
				"<tr><td>" +
				"<a href=\"" + otObject.getGlobalId() + "\" + " +
						"target=\"" + viewConfig.getFrame().getGlobalId() + "\" " +
						linkViewIdStr + 
						"mode=\"" + viewConfig.getPopupViewMode() + "\" >edit</a>" +
						"</td></tr></table>";
		// System.out.println(text);
		return text;
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTViewConfigAware#setViewConfig(org.concord.framework.otrunk.OTObject)
	 */
	public void setViewConfig(OTObject viewConfig) 
	{
		this.viewConfig = (OTAuthorEmbedDefaultViewConfig) viewConfig;
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTRequestedViewEntryAware#setRequestedViewEntry(org.concord.framework.otrunk.view.OTViewEntry)
	 */
	public void setRequestedViewEntry(OTViewEntry viewEntry) 
	{
		requestedViewEntry = viewEntry;
	}

}
