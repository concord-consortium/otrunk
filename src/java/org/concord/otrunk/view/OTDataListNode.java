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
 * Created on Aug 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view;

import org.concord.framework.util.SimpleTreeNode;
import org.concord.otrunk.datamodel.OTDataList;


/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OTDataListNode implements SimpleTreeNode 
{
	private OTDataList list;
	private String name;
	private OTDataObjectNode owner;
	
	public OTDataListNode(String name, OTDataList list, OTDataObjectNode owner)
	{
		this.name = name;
		this.list = list;
		this.owner = owner;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#getChildCount()
	 */
	public int getChildCount() 
	{
		return list.size();
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#getIndexOfChild(org.concord.portfolio.browser.PfTreeNode)
	 */
	public int getIndexOfChild(SimpleTreeNode child) 
	{
		for(int i=0; i<list.size(); i++) {
			Object element = list.get(i);
			if(child.getObject().equals(element)) {
				return i;
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#getChild(int)
	 */
	public SimpleTreeNode getChild(int index) 
	{
		Object child = list.get(index);
		try {
			return owner.getNodeFromObject(null, child);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#setName(java.lang.String)
	 */
	public void setName(String name) {
		// can't change the name of the resource list at this time
		// this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#getObject()
	 */
	public Object getObject() {
		return list;
	}

	public String toString()
	{
		return name + " <list>";
	}
}
