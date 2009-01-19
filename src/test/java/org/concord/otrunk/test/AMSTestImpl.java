package org.concord.otrunk.test;

import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.concord.otrunk.OTObjectInternal;
import org.concord.otrunk.OTrunkImpl;

public class AMSTestImpl extends OTObjectInternal
    implements ASMTestInterface
{
	static OTClass OT_CLASS = OTrunkImpl.getOTClass(ASMTestInterface.class.getName());  
	static OTClassProperty NUMBER_PROP = OT_CLASS.getProperty("number");
	
	public int getNumber()
	{		
		return (Integer)getResourceChecked(NUMBER_PROP, Integer.TYPE);
	}

	public void setNumber(int num)
    {
		setResource("number", num);	    
    }
}
