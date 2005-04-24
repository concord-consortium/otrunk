/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-04-24 15:44:55 $
 * $Author: scytacki $
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
