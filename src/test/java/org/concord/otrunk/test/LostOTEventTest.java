package org.concord.otrunk.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTChangeNotifying;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;

/**
 * 
 * LostOTEventTest <br>
 * This class is to test reiterant stateChanged methods.  If a set method is called
 * on the same object inside of a stateChanged method, then that stateChanged is called 
 * a second time before the first call to it is complete.  This used to cause problems
 * with the change events.  But that is fixed now. 
 * 
 * <p>
 * Date created: Oct 14, 2007
 * 
 * @author scytacki<p>
 *
 */
public class LostOTEventTest extends AbstractOTJComponentView 
	implements OTChangeListener, ActionListener
{
	protected OTBasicTestObject otObject;
	protected JLabel textLabel;
	protected JCheckBox optionCheck;
	
	public JComponent getComponent(OTObject otObject)
    {
		this.otObject = (OTBasicTestObject)otObject;
		
		JPanel panel = new JPanel();
		JButton button = new JButton("click here");
		button.setActionCommand("button");
		textLabel = new JLabel(this.otObject.getString());
		optionCheck = new JCheckBox("set value?", true);
		button.addActionListener(this);

		panel.add(optionCheck);
		panel.add(textLabel);
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
		System.out.println("1. " + e + " "+ e.getDescription());
		//The first event should be property string
		if (e.getProperty().equals("string")){
			if (optionCheck.isSelected()){
				otObject.setFloat(otObject.getFloat()+1);
			}
			
    		System.out.println("2. " + e + " "+ e.getDescription());
    		textLabel.setText((String)e.getValue());		
		}		
    }
	
	/**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
    	if (e.getActionCommand().equals("button")){
    		otObject.setString(otObject.getString() + "0");
    	}
    }			
}
