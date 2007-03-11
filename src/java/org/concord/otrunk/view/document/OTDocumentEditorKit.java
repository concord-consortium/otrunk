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
 * Created on Aug 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view.document;

import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OTDocumentEditorKit extends HTMLEditorKit 
{
	/**
	 * Not intended to be serialized, just added remove compile warning
	 */
	private static final long serialVersionUID = 1L;
	
	private OTHTMLFactory viewFactory;
	
	public OTDocumentEditorKit(OTHTMLFactory editorKitViewFactory)
	{
		super();
	
		viewFactory = editorKitViewFactory;
	}
	
    public ViewFactory getViewFactory()
    {
    	return viewFactory;
    }
}
