/*
 * Created on Aug 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view;

import java.util.Vector;

import org.concord.framework.otrunk.OTObject;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface OTFolder
{
	public Object getChild(int index);
	
	public void addChild(int index, OTObject pfObject);
	
	public void addChild(OTObject pfObject);
	
	public void removeAllChildren();
	
	public int getChildCount();
	
	public Vector getChildVector();
	
	public void setChildVector(Vector children);
	
	public String getName();
}
