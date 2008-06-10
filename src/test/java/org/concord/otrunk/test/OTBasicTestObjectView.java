package org.concord.otrunk.test;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;

public class OTBasicTestObjectView extends AbstractOTJComponentView
{
	public JComponent getComponent(OTObject otObject)
    {
	    return new JLabel("OTBasicTestObjectView");
    }

	public void viewClosed()
    {
		System.out.println("OTBasicTestObjectView#viewClosed()");
    }
}
