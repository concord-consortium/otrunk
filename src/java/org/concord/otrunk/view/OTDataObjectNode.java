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
 * Created on Jul 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.util.SimpleTreeNode;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDataObject;

public class OTDataObjectNode 
	implements SimpleTreeNode
{
	private OTDataObject object;
	private String name;
	OTrunkImpl pfDatabase;
	
	public OTDataObjectNode(String name, OTDataObject obj, OTrunkImpl db)
	{
		object = obj;
		this.name = name;
		pfDatabase = db;
	}
	
	public OTDataObject getPfDataObject()
	{
		return object;
	}
	
	public String toString()
	{
		String nodeName = "";
		
		String otObjectClass = (String)object.getResource(OTrunkImpl.RES_CLASS_NAME);
		String otObjectName = (String)object.getResource("name");
		
		if(name != null) {
			if(nodeName.length() > 0) nodeName += " ";
			nodeName += name;			
		}		
		
		if(otObjectClass != null) {
			int lastPeriod = otObjectClass.lastIndexOf('.');
			otObjectClass = otObjectClass.substring(lastPeriod+1);
			nodeName += "<" + otObjectClass + ">";
		}
		
		if(otObjectName != null) {
			if(nodeName.length() > 0) nodeName += " ";
			nodeName += otObjectName;
		}
		
		if(nodeName.length() == 0) {
			nodeName += object.getGlobalId();
		}
		
		return nodeName;
	}		
	
	public int getChildCount()
	{
		if(object != null) {
			String [] keys = object.getResourceKeys();
			return keys.length + 1;
		}

		return 0;
	}
	
	public SimpleTreeNode getChild(int index)
	{
		OTDataObject pfParent = getPfDataObject();
		
		if(index == 0) {
			// make a leaf node that displays the toString of
			return new OTJavaObjectNode("id", "" + pfParent.getGlobalId());
		}
		
		String [] keys = pfParent.getResourceKeys();
		String key = keys[index - 1];
		Object child = pfParent.getResource(key);
			
		try {		
			return getNodeFromObject(key, child);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

	public SimpleTreeNode getNodeFromObject(String key, Object child)
	throws Exception
	{
		OTDataObject pfParent = getPfDataObject();

		if(child instanceof OTID) {
			// get the dataobject for this id
			OTDataObject pfChild = pfDatabase.getOTDataObject(pfParent, (OTID)child);
			return new OTDataObjectNode(key, pfChild, pfDatabase);
		} else if(child instanceof OTResourceList) {
			// make node for list
			return new OTResourceListNode(key, (OTResourceList) child, this);
		} else if(child instanceof OTResourceMap) {
			// make node for list
			return new OTResourceMapNode(key, (OTResourceMap) child, this);		    
		} else  {
			// make a leaf node that displays the toString of
			return new OTJavaObjectNode(key, child);
		}		
	}

	public int getIndexOfChild(SimpleTreeNode child)
	{
		OTDataObject dataObject = getPfDataObject();
		
		if(child.getObject().equals(dataObject.getGlobalId().toString())) {
			return 0;
		}
		
		String [] keys = dataObject.getResourceKeys();
		
		for(int i=0; i<keys.length; i++) {
			Object testChild = dataObject.getResource(keys[i]);
			if(child.getObject().equals(testChild)) {
				return i+1;
			}			 
		}
		
		return -1;
	}

	public Object getObject()
	{
		return getPfDataObject();
	}
	
	public void setName(String name)
	{		
		// do nothing 
	}

}