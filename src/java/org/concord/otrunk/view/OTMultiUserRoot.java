/*
 *  Copyright (C) 2008  The Concord Consortium, Inc.,
 *  25 Love Lane, Concord, MA 01742
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


package org.concord.otrunk.view;


import java.net.URL;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;


public interface OTMultiUserRoot extends OTObjectInterface 
{
	OTObject getUserList();
	
	OTObject getReportTemplate();
	
	OTObject getReportTemplateViewEntry();
	
	URL getGroupOverlayURL();
}
