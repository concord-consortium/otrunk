/*
 * Created on Aug 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface OTResourceList extends OTResourceCollection
{	
	public Object get(int index);
	
	public void add(Object object);
	
	public void add(int index, Object object);		
}
