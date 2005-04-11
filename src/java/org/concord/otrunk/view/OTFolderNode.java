
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
 * Created on Jul 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.util.SimpleTreeNode;


public class OTFolderNode 
	implements SimpleTreeNode
{
	private OTObject object;
	private OTFolder folder;
	
	public OTFolderNode(OTObject obj)
	{
		object = obj;
		if(obj instanceof OTFolder) {
			folder = (OTFolder)obj;
		} else {
			folder = null;
		}
	}
	
	public OTFolderNode(OTFolder folder)
	{
		this.folder = folder;
		if(folder instanceof OTObject) {
			object = (OTObject)folder;
		} else {
			object = null;
		}
	}

	public OTObject getPfObject()
	{
		return object;
	}
	
	public String toString()
	{
		if(object != null) {
			return object.getName();
		} else {
			return folder.getName();
		}
	}		
	
	public int getChildCount()
	{
		if(folder != null) {
			return folder.getChildCount();
		}

		return 0;
	}
	
	public int getIndexOfChild(SimpleTreeNode child) 
	{
		if(folder != null) {
			for(int i=0; i<getChildCount(); i++) {
				if(((OTFolderNode)child).getPfObject() == folder.getChild(i)) {
					return i;
				}
			}
		}
		return -1;
	}

	public void setName(String name)
	{
		if(object != null) {
			object.setName(name);
		}
	}
	
	public Object getObject()
	{
		return getPfObject();
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.browser.PfTreeNode#getChild(int)
	 */
	public SimpleTreeNode getChild(int index) 
	{
		if(folder != null) {
			
			Object pfChild = folder.getChild(index);
			
			if(pfChild instanceof OTObject) {
				return new OTFolderNode((OTObject)pfChild);
			} else if(pfChild instanceof OTFolder) {
				return new OTFolderNode((OTFolder)pfChild);
			}
		}
		
		return null;
	}
}