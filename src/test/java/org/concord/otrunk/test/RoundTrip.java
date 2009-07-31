package org.concord.otrunk.test;

import junit.framework.TestCase;

import org.concord.framework.otrunk.OTObject;

/**
 * This is intended to be extended so test round tripping various OTrunk
 * properties
 * 
 * @author scytacki
 *
 */
public class RoundTrip extends TestCase
{
	private RoundTripHelper helper;
	
	public void initOTrunk(Class<? extends OTObject> otClass) throws Exception
	{
		helper.initOTrunk(otClass);
	}

	public void reload() throws Exception
	{
		helper.reload();
	}	
	
	public OTObject getRootObject() throws Exception
	{
		return helper.getRootObject();
	}
	
	public void setHelper(RoundTripHelper helper)
    {
	    this.helper = helper;
    }
}
