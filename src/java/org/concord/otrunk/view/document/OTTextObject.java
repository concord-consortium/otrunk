/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*
 * Created on Jul 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view.document;

import org.concord.otrunk.view.OTFolderObject;



/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OTTextObject extends OTFolderObject
	implements OTDocument
{
	public static interface ResourceSchema extends OTFolderObject.ResourceSchema{
		public String getBodyText();
		public void setBodyText(String text);
		
		public static boolean DEFAULT_input = true;
		public boolean getInput();
		public void setInput(boolean flag);
	}
			
	private ResourceSchema resources;
	public OTTextObject(ResourceSchema resources) 
	{
		super(resources);
		this.resources = resources;		
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.PortfolioObject#getBodyText()
	 */
	public String getBodyText() {	    
		return resources.getBodyText();
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.PortfolioObject#setBodyText()
	 */
	public void setBodyText(String bodyText) {
		resources.setBodyText(bodyText);
	}
	
	public boolean getInput()
	{
		// How do we set default values for primitives...
		return resources.getInput();
	}
	
	public void setInput(boolean flag)
	{
		resources.setInput(flag);
	}
	
	public String getDocumentText()
	{
		return getBodyText();
	}
	
	public void setDocumentText(String text)
	{
		setBodyText(text);		
	}
	
	public String getMarkupLanguage()
	{
		return OTDocument.MARKUP_PLAIN;
	}
}
