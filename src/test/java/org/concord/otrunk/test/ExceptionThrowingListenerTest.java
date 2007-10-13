package org.concord.otrunk.test;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTChangeNotifying;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;

public class ExceptionThrowingListenerTest
	extends AbstractOTJComponentView
{

	public JComponent getComponent(OTObject otObject)
    {
		OTChangeListener myListener = new OTChangeListener()
		{
			public void stateChanged(OTChangeEvent e)
            {
				// try casting e to something it isn't
				Object obj = new Object();
				System.out.println(((String)obj));
//				throw new NullPointerException();	            
            }			
		};
		
		((OTChangeNotifying)otObject).addOTChangeListener(myListener);
		
		otObject.setName("new name");

		return new JLabel("Test View");
    }

	public void viewClosed()
    {
	    // TODO Auto-generated method stub
	    
    }
	
}
