/*
 * Created on Aug 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view;

import java.util.Vector;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OTFolderObject extends DefaultOTObject
	implements OTFolder
{
	public static interface ResourceSchema extends OTResourceSchema{
		public OTObjectList getChildren();
		public void setChildren(OTObjectList childrent);
	}

	private ResourceSchema resources;
	public OTFolderObject(ResourceSchema resources) 
	{
		super(resources);
		this.resources = resources;		
	}
		
	public Object getChild(int index)
	{
		OTObjectList children = resources.getChildren();
		return children.get(index);			
	}
	
	public void addChild(int index, OTObject pfObject)
	{
		OTObjectList children = resources.getChildren();
		children.add(index, pfObject);		
	}
	
	public void addChild(OTObject pfObject)
	{
		OTObjectList children = resources.getChildren();
		children.add(pfObject);
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#removeAllChildren()
	 */
	public void removeAllChildren()
	{
		OTObjectList children = resources.getChildren();
		children.removeAll();
	}
	
	public Vector getChildVector()
	{
		OTObjectList children = resources.getChildren();
		return children.getVector();		
	}
	
	public void setChildVector(Vector childVector)
	{
		OTObjectList children = resources.getChildren();
		
		// TODO should compare to see if the list has changed
		
		children.removeAll();
		for(int i=0; i<childVector.size(); i++) {
			OTObject otObject = (OTObject)childVector.get(i);
			children.add(otObject);
		}
	}
	
	public int getChildCount()
	{
		OTObjectList children = resources.getChildren();
		return children.size();		
	}
}
