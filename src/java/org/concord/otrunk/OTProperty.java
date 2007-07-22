/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2007-07-22 03:56:23 $
 * $Author: imoncada $
 *
 * Licence Information
 * Copyright 2007 The Concord Consortium 
*/
package org.concord.otrunk;

import org.concord.framework.otrunk.OTObjectInterface;


/**
 * OTProperty
 * A simple base interface to specify properties as 
 * a couple of strings: name and value
 *
 * Date created: Jul 21, 2007
 *
 * @author Ingrid Moncada<p>
 *
 */
public interface OTProperty extends OTObjectInterface
{
	public String getName();
	public void setName(String name);
	
	public String getValue();
	public void setValue(String value);	
}
