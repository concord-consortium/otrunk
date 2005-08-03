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

import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.framework.util.SimpleTreeNode;


/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OTResourceMapNode implements SimpleTreeNode 
{
	private OTResourceMap map;
	private String name;
	private OTDataObjectNode owner;
	
	public OTResourceMapNode(String name, OTResourceMap map, 
	        OTDataObjectNode owner)
	{
		this.name = name;
		this.map = map;
		this.owner = owner;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#getChildCount()
	 */
	public int getChildCount() 
	{
	    return map.size();
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#getIndexOfChild(org.concord.portfolio.browser.PfTreeNode)
	 */
	public int getIndexOfChild(SimpleTreeNode child) 
	{
	    // check the key of the child node
	    
	    String [] keys = map.getKeys();
	    
	    for(int i=0; i < keys.length; i++) {
			Object element = map.get(keys[i]); 
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
	    String [] keys = map.getKeys();

	    Object child = map.get(keys[index]);
		try {
			return owner.getNodeFromObject("'" + keys[index] + "'",
			        child);
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
		return map;
	}

	public String toString()
	{
		return name + " <map>";
	}
}
