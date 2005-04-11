
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
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
 */

/*
 * Last modification information:
 * $Revision: 1.7 $
 * $Date: 2005-04-11 15:01:08 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceCollection;


/**
 * OTDatabase
 * Class name and description
 *
 * Date created: Nov 8, 2004
 *
 * @author scott<p>
 *
 */
public interface OTDatabase
{
	public abstract void setRoot(OTID rootId) 
		throws Exception;
	
	public abstract OTDataObject getRoot() 
		throws Exception;	
	
	/**
	 * Make a brand new data object and create an id for it
	 * @return
	 * @throws Exception
	 */
	public abstract OTDataObject createDataObject() 
		throws Exception;
	
	/**
	 * Make a brand new data object and use an existing id
	 * this is required so objects can be imported into this database
	 * @return
	 * @throws Exception
	 */
	public abstract OTDataObject createDataObject(OTID id) 
		throws Exception;

	/**
	 * The dataParent must be set so the database can correctly look up the 
	 * child object.
	 * 
	 * This should only be used if you want the lowlevel view of the data
	 * it will not handle users correctly.  You must use the OTrunkImpl version
	 * of this method for that.
	 *  
	 * @param dataParent
	 * @param childID
	 * @return
	 * @throws Exception
	 */
	public abstract OTDataObject getOTDataObject(OTDataObject dataParent, OTID childID)
		throws Exception;
	
	public abstract void close();

}
