/*
 * Created on Jul 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.swing.tree.SimpleTreeNode;

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
		// TODO: we need a nicer string representation here
		// it should be the name of the resource in the parent object
		// how we handle resouce lists will have to be different
		return name + ": " + object.getGlobalId().toString();
	}		
	
	public int getChildCount()
	{
		if(object != null) {
			String [] keys = object.getResourceKeys();
			return keys.length;
		}

		return 0;
	}
	
	public SimpleTreeNode getChild(int index)
	{
		OTDataObject pfParent = getPfDataObject();
		
		String [] keys = pfParent.getResourceKeys();
		String key = keys[index];
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
			if(child.getObject().equals(testChild)) {
				return i;
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