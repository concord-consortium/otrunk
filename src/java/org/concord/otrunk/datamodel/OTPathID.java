/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-05-12 15:27:19 $
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
