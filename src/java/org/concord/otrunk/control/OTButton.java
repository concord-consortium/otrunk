/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2006-05-05 16:00:32 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.control;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.view.OTAction;

public interface OTButton
    extends OTObjectInterface
{
    public String getText();
    
    public OTAction getAction();
}
