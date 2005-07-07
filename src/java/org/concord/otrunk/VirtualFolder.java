/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-07-07 16:18:48 $
 * $Author: swang $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.util.Vector;

import org.concord.framework.otrunk.OTObject;
import org.concord.otrunk.view.OTFolder;


/**
 * VirtualFolder
 * Class name and description
 *
 * Date created: Sep 10, 2004
 *
 * @author scott<p>
 *
 */
public class VirtualFolder 
	implements OTFolder
{
	Vector children = new Vector();
	String name = "virtual folder";
	
	public void addVirtualChild(Object child)
	{
		children.add(child);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#getChild(int)
	 */
	public Object getChild(int index)
	{
		return children.get(index);
	}

	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#getChildVector()
	 */
	public Vector getChildVector()
	{
		// TODO Auto-generated method stub
		return (Vector) (children.clone());
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#setChildVector(java.util.Vector)
	 */
	public void setChildVector(Vector children)
	{
	}
	
	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#addChild(int, org.concord.portfolio.PortfolioObject)
	 */
	public void addChild(int index, OTObject pfObject)
	{
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#addChild(org.concord.portfolio.PortfolioObject)
	 */
	public void addChild(OTObject pfObject)
	{
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#removeAllChildren()
	 */
	public void removeAllChildren()
	{
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#getChildCount()
	 */
	public int getChildCount()
	{
		return children.size();
	}

	/* (non-Javadoc)
	 * @see org.concord.portfolio.objects.PfFolder#getName()
	 */
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
}
