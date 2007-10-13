package org.concord.otrunk.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTChangeNotifying;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;

public class ExceptionThrowingListenerTest extends AbstractOTJComponentView 
	implements OTChangeListener, ActionListener
{
	protected OTBasicTestObject otObject;
	
	public JComponent getComponent(OTObject otObject)
    {
		this.otObject = (OTBasicTestObject)otObject;
		
		JPanel panel = new JPanel();
		JButton button = new JButton("click here");
		button.addActionListener(this);
		panel.add(button);
		
		((OTChangeNotifying)otObject).addOTChangeListener(this);
		
		return panel;
    }

	public void viewClosed()
    {
	    // TODO Auto-generated method stub
    }
	
	public void stateChanged(OTChangeEvent e)
    {
		// try casting e to something it isn't
		Object obj = new Object();
		System.out.println(((String)obj));
//		throw new NullPointerException();	            
    }

	/**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
		otObject.setString("new string");
    }			
}
