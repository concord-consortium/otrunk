
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
 * $Date: 2005-05-19 17:18:40 $
 * $Author: maven $
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
