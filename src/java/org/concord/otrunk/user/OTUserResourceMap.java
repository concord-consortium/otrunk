
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
 * $Revision: 1.3 $
 * $Date: 2005-04-26 15:41:41 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.user;

import org.concord.framework.otrunk.OTResourceMap;


final class OTUserResourceMap
	implements OTResourceMap
{
    OTUserDataObject parent;
    OTResourceMap authoredMap;
    
    public OTUserResourceMap(OTUserDataObject parent, OTResourceMap authoredMap)
    {
        this.parent = parent;
        this.authoredMap = authoredMap;
    }
    
    /* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTResourceMap#get(java.lang.String)
     */
    public Object get(String key)
    {
        // TODO Handle user generated maps
        return authoredMap.get(key);
    }
    
    /* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTResourceMap#getKeys()
     */
    public String[] getKeys()
    {
        // TODO Handle user generated maps
        return authoredMap.getKeys();
    }
    
    /* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTResourceMap#put(java.lang.String, java.lang.Object)
     */
    public void put(String key, Object resource)
    {
        throw new UnsupportedOperationException("OTUserResourceMap does not support put");

    }
    
    /* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTResourceCollection#removeAll()
     */
    public void removeAll()
    {
        throw new UnsupportedOperationException("OTUserResourceMap does not support removeAll");
    }
    
    /* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTResourceCollection#size()
     */
    public int size()
    {
        // TODO Handle user generatored maps
        return authoredMap.size();
    }
}