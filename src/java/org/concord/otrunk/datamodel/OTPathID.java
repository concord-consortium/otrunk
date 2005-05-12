
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
 * $Revision: 1.2 $
 * $Date: 2005-05-12 15:33:49 $
 * $Author: maven $
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
    
    public String toString()
    {
        return path;
    }
    
    public boolean equals(Object other)
    {
        if(!(other instanceof OTPathID)) return false;
        
        return ((OTPathID)other).path.equals(path);
    }
    
    public int hashCode()
    {
        return toString().hashCode();
    }
}
