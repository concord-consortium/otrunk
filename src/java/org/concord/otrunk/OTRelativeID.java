
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
 * $Date: 2005-04-24 15:49:47 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import org.concord.framework.otrunk.OTID;
import org.concord.otrunk.datamodel.OTIDFactory;

/**
 * OTXMLPathID
 * Class name and description
 *
 * Date created: Apr 17, 2005
 *
 * @author scott<p>
 *
 */
public class OTRelativeID
    implements OTID
{
    String path = null;
    OTID rootId = null;
    String relativePath = null;
    
    public OTRelativeID(String path)
    {
        this.path = path;

        int endOfId = path.indexOf('/');
        
        String rootIdStr = null;
        if(endOfId == -1) {
            System.err.println("non-relative id stored in relative id");
            rootIdStr = path;
        } else {
            rootIdStr = path.substring(0,endOfId);
            relativePath = path.substring(endOfId+1, path.length());
        }        

        rootId = OTIDFactory.createOTID(rootIdStr);
    }
    
    public String toString()
    {
        return path;
    }
    
    public OTID getRootId()
    {
        return rootId;        
    }
    
    public String getRelativePath()
    {
        return relativePath;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return path.hashCode();
    }
    
    public boolean equals(Object other)
    {
        if(!(other instanceof OTRelativeID)) {
            return false;
        }
        
        return ((OTRelativeID)other).path.equals(this.path);
            
    }
}
