/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-07-22 16:20:38 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.util.Vector;

import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.OTView;

public interface OTMultiUserView
    extends OTView
{
    public void setUserList(OTrunk otrunk, Vector userList);
}
