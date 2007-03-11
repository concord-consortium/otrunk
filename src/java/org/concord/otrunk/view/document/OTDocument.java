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
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2007-03-11 23:38:44 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view.document;

import org.concord.framework.otrunk.OTObject;


/**
 * OTDocument
 * Class name and description
 *
 * Date created: Dec 17, 2004
 *
 * @author scott<p>
 *
 */
public interface OTDocument
	extends OTObject
{
	public static final String MARKUP_PLAIN = "plain";
	public static final String MARKUP_PFXHTML = "xhtml";
	public static final String MARKUP_PFHTML = "html";

	public String getDocumentText();
	public void setDocumentText(String text);
	
	public boolean getInput();
	
	public String getMarkupLanguage();
}
