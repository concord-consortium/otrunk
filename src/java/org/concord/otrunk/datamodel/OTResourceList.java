/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-11-12 02:02:51 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
 */
package org.concord.otrunk.datamodel;


/**
 * OTResourceList
 * Class name and description
 *
 * Date created: Nov 8, 2004
 *
 * @author scott<p>
 *
 */
public interface OTResourceList extends OTResourceCollection
{

	public abstract Object get(int index);

	public abstract void add(Object object);

	public abstract void add(int index, Object object);
}