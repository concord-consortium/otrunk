/*
 * Created on Aug 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view;

import org.concord.swing.tree.SimpleTreeNode;

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
