/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-04-26 15:41:41 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

/**
 * OTXMLString
 * Class name and description
 *
 * Date created: Apr 25, 2005
 *
 * @author scott<p>
 *
 */
public class OTXMLString
{
    String content;
    
    public OTXMLString(String content)
    {
        this.content = content;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public String toString()
    {
        return getContent();
    }
}
