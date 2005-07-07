
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
 * $Date: 2005-07-07 16:21:07 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.util.Vector;

import org.concord.framework.otrunk.OTObject;
import org.concord.otrunk.view.OTFolder;


/**
 * VirtualFolder
 * Class name and description
 *
 * Date created: Sep 10, 2004
 *
 * @author scott<p>
 *
 */
public class VirtualFolder 
	implements OTFolder
{
	Vector children = new Vector();
	String name = "virtual folder";
	
	public void addVirtualChild(Object child)
	{
		children.add(child);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#getChild(int)
	 */
	public Object getChild(int index)
	{
		return children.get(index);
	}

	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#getChildVector()
	 */
	public Vector getChildVector()
	{
		// TODO Auto-generated method stub
		return (Vector) (children.clone());
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#setChildVector(java.util.Vector)
	 */
	public void setChildVector(Vector children)
	{
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#addChild(int, org.concord.portfolio.PortfolioObject)
	 */
	public void addChild(int index, OTObject pfObject)
	{
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#addChild(org.concord.portfolio.PortfolioObject)
	 */
	public void addChild(OTObject pfObject)
	{
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#removeAllChildren()
	 */
	public void removeAllChildren()
	{
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#getChildCount()
	 */
	public int getChildCount()
	{
		return children.size();
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#getName()
	 */
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
}
