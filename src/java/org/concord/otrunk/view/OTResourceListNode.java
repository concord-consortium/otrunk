/*
 * Created on Aug 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.util.SimpleTreeNode;


/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OTResourceListNode implements SimpleTreeNode 
{
	private OTResourceList list;
	private String name;
	private OTDataObjectNode owner;
	
	public OTResourceListNode(String name, OTResourceList list, OTDataObjectNode owner)
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
		String name = "anon " + index;
		Object child = list.get(index);
		try {
			return owner.getNodeFromObject(name, child);
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
		return name;
	}
}
