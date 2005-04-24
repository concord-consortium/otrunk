/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-04-24 15:44:55 $
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
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTResourceMap#getKeys()
     */
    public String[] getKeys()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTResourceMap#put(java.lang.String, java.lang.Object)
     */
    public void put(String key, Object resource)
    {
        // TODO Auto-generated method stub

    }
    
    /* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTResourceCollection#removeAll()
     */
    public void removeAll()
    {
        // TODO Auto-generated method stub

    }
    
    /* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTResourceCollection#size()
     */
    public int size()
    {
        // TODO Auto-generated method stub
        return 0;
    }
}