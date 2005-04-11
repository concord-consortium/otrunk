
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
 * Created on Aug 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view;

import org.concord.framework.util.SimpleTreeNode;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OTJavaObjectNode implements SimpleTreeNode 
{
	Object object;
	String name;
	
	public OTJavaObjectNode(String name, Object object)
	{
		this.name = name;
		this.object = object;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#getChildCount()
	 */
	public int getChildCount() {
		// This is a leaf
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#getIndexOfChild(org.concord.portfolio.browser.PfTreeNode)
	 */
	public int getIndexOfChild(SimpleTreeNode child) {
		// This is a leaf
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#getChild(int)
	 */
	public SimpleTreeNode getChild(int index) {
		// This is a leaf
		return null;
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#setName(java.lang.String)
	 */
	public void setName(String name) 
	{
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#getObject()
	 */
	public Object getObject() 
	{
		return object;
	}

	public String toString()
	{
		return name;
	}

}
