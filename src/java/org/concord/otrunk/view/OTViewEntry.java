/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-05-19 17:09:49 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTObjectInterface;

/**
 * OTViewEntry
 * Class name and description
 *
 * Date created: May 18, 2005
 *
 * @author scott<p>
 *
 */
public interface OTViewEntry
    extends OTObjectInterface
{
    public String getObjectClass();
    
    public String getViewClass();
}
