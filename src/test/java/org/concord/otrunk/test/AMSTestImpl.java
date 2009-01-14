package org.concord.otrunk.test;

import org.concord.otrunk.OTObjectInternal;

public class AMSTestImpl extends OTObjectInternal
    implements ASMTestInterface
{
	public int getNumber()
	{		
		return (Integer)getResourceChecked("number", Integer.TYPE);
	}

	public void setNumber(int num)
    {
		setResource("number", num);	    
    }
}
