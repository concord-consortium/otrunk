/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
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
 * END LICENSE */

/*
 * Created on Jan 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTObjectList;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface OTSystem extends OTObjectInterface 
{
	public OTObject getRoot();
	public void setRoot(OTObject root);
	
	public OTObjectList getBundles();	

	/**
	 * This list should be deprecated because these objects are really bundles
	 * not services.  The bundles provide services.  For backwards compatibility
	 * this list is processed first and then the bundles list. 
	 * 
	 * @return
	 */
	public OTObjectList getServices();	

	public OTObjectList getLibrary();

	/**
	 * This is a list of the active global overlays in the system.  In the future there might
	 * be the concept of scoped overlays.  In that case they will have to be defined in another
	 * manner.
	 *  
	 * @return
	 */
	public OTObjectList getOverlays();
	
	public OTObject getFirstObject();
	
	public OTObject getFirstObjectNoUserData();
}
