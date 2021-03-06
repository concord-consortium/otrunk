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
import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.util.SimpleTreeNode;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDataList;
import org.concord.otrunk.datamodel.OTDataMap;
import org.concord.otrunk.datamodel.OTDataObject;

public class OTDataObjectNode 
	implements SimpleTreeNode
{
	private OTDataObject object;
	private String name;
	OTrunk otDatabase;
	
	public OTDataObjectNode(String name, OTDataObject obj, OTrunk db)
	{
		object = obj;
		this.name = name;
		otDatabase = db;
	}
	
	public OTDataObject getPfDataObject()
	{
		return object;
	}
	
	public String toString()
	{
		String nodeName = "";
		
		String otObjectClass = OTrunkImpl.getClassName(object);
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

    static boolean isChildNode(Object object)
    {
        return object instanceof OTResourceCollection ||
            object instanceof OTID;
    }
    
	public int getChildCount()
    {
        if(object == null) {
            return 0;
        }
        
        String [] keys = object.getResourceKeys();
        int count = 0;
        for(int i=0; i<keys.length; i++) {
            Object child = object.getResource(keys[i]);
            if(isChildNode(child)) {
                count++;
            }
		}

		return count;
	}
	
	public SimpleTreeNode getChild(int index)
	{		
		String [] keys = object.getResourceKeys();
        int count = 0;
        String childKey = null;
        for(int i=0; i<keys.length; i++) {
            Object child = object.getResource(keys[i]);
            if(isChildNode(child)) {
                if(count == index) {
                    childKey = keys[i];
                    break;
                }
                count++;
            }
        }
        
		Object child = object.getResource(childKey);
			
		try {		
			return getNodeFromObject(childKey, child);
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
			OTDataObject pfChild = pfParent.getDatabase().getOTDataObject(pfParent, (OTID)child); 
			return new OTDataObjectNode(key, pfChild, otDatabase);
		} else if(child instanceof OTDataList) {
			// make node for list
			return new OTDataListNode(key, (OTDataList) child, this);
		} else if(child instanceof OTDataMap) {
			// make node for list
			return new OTDataMapNode(key, (OTDataMap) child, this);		    
		} else  {
			// make a leaf node that displays the toString of
			return new OTJavaObjectNode(key, child);
		}		
	}

	public int getIndexOfChild(SimpleTreeNode child)
	{
		OTDataObject dataObject = getPfDataObject();
		
		String [] keys = dataObject.getResourceKeys();
		
		for(int i=0; i<keys.length; i++) {
			Object testChild = dataObject.getResource(keys[i]);
            if(isChildNode(testChild)) {
                if(child.getObject().equals(testChild)) {
                    return i;
                }
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