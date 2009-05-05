package org.concord.otrunk.test;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;


public class OTMemoryHogView extends AbstractOTJComponentView    
{
	OTMemoryHog hog;
	byte [] buffer;
	
	OTChangeListener listener = new OTChangeListener(){

		public void stateChanged(OTChangeEvent e)
        {
        }
		
	};
	
	public JComponent getComponent(OTObject otObject)
    {
		hog = (OTMemoryHog) otObject;
		buffer = new byte[hog.getKiloBytes() * 1024];
		
		hog.addOTChangeListener(listener);
		
		return new MemoryHogComponent("This is using " + hog.getKiloBytes() + "KB");
    }

	@Override
	public void viewClosed()
	{
	    super.viewClosed();
	    
	    hog.removeOTChangeListener(listener);
	}
	
	class MemoryHogComponent extends JLabel {
        private static final long serialVersionUID = 1L;
		byte [] componentBuffer;
		
		MemoryHogComponent(String label){
			super(label);
			componentBuffer = new byte[hog.getKiloBytes() * 1024];
		}
	}
}
