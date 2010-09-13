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
 * $Revision: 1.4 $
 * $Date: 2007-10-04 21:28:21 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTID;

/**
 * OTPathID
 * Class name and description
 *
 * Date created: May 11, 2005
 *
 * @author scott<p>
 *
 */
public class OTPathID
    implements OTID
{
    String path;
    
    public OTPathID(String path)
    {
        this.path = path;
    }
    
    /**
     * This returns a unique string for this id.  This is not the actual id.<p>
     * 
     * The actual id is not returned because using the toString method on an OTID
     * cannot always return the correct thing.  The method OTObjectService.getExternalID 
     * should be used instead. 
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "%" + path;
    }
    
    public boolean equals(Object other)
    {
        if(!(other instanceof OTPathID)) return false;
        
        return ((OTPathID)other).path.equals(path);
    }
    
    public int hashCode()
    {
        return path.hashCode();
    }

	public String toExternalForm()
    {
		return path;
    }

	public OTID getMappedId()
    {
	    return this;
    }
}
