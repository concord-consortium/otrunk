package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObject;

public interface RoundTripHelper
{

	public abstract void initOTrunk(Class<? extends OTObject> otClass)
	    throws Exception;

	public abstract void reload()
	    throws Exception;

	public abstract OTObject getRootObject()
	    throws Exception;

}