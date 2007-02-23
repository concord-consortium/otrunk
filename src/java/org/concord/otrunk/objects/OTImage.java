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
 * $Date: 2007-02-23 04:16:05 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.objects;

import org.concord.framework.otrunk.OTObjectInterface;


/**
 * OT Image Object
 * (Copied from portfolio pfImage)
 *
 * Date created: Sep 10, 2004
 *
 * @author imoncada<p>
 *
 */
public interface OTImage extends OTObjectInterface
{
	/**
	 * @return Returns the imageURL.
	 */
	public String getImageURL();
	
	/**
	 * @param imageURL The imageURL to set.
	 */
	public void setImageURL(String imageURL);
	
	public byte [] getImageBytes();	
}
