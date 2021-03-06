package org.concord.otrunk.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewEntryAware;
import org.concord.otrunk.OTrunkUtil;

public class OTChooserView extends AbstractOTJComponentView implements OTViewEntryAware, ActionListener{
	
	OTObject otObject;
	
	String name;
	
	OTViewEntry viewEntry;
	
	JButton insertButton;
	
	JPanel mainPanel;
	
	String property;
	
	String mode;
	
	public JComponent getComponent(OTObject otObject) {
		
		this.otObject = otObject;
		property = ((OTChooserViewEntry)viewEntry).getPropertyName();
		mode = ((OTChooserViewEntry)viewEntry).getFinalViewMode();
		
		if (viewEntry instanceof OTChooserViewEntry){
			mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			name = (otObject.getName() != null? otObject.getName() : "object");
			insertButton = new JButton();
			insertButton.addActionListener(this);
			updatePanel();
			return mainPanel;
		} else {
			System.err.println("OTChooserView must be used in a OTChooserViewEntry");
			return null;
		}
	}

	public void viewClosed() {
		// TODO Auto-generated method stub
		
	}

	public void setViewEntry(OTViewEntry viewConfig) {
		viewEntry = viewConfig;
	}
	
	private void updatePanel(){
		
		try {
			if (OTrunkUtil.getNonPathPropertyValue(property, otObject) != null){
				
			insertButton.setText("loading...");
			insertButton.setEnabled(false);
			try {
			//	URL url = new URL(sUrl);
				
				JComponent mwPanel = getChildComponent(otObject, null, "normal");
				mainPanel.removeAll();
				mainPanel.add(mwPanel);
				insertButton.setText("Change " + name);
				mainPanel.add(insertButton);
			} catch (Exception ex) {
				ex.printStackTrace();
				insertButton.setText("Insert " + name);
			}
			insertButton.setEnabled(true);
			} else {
				insertButton.setText("Insert " + name);
				mainPanel.add(insertButton);
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void actionPerformed(ActionEvent e) {
		
		String value = (String) JOptionPane.showInputDialog(insertButton,
				"Please specify the URL of the object's location:",
				"Object Choser", JOptionPane.PLAIN_MESSAGE, null, null,
				"http://");

		if (value != null) {
			insertButton.setText("loading...");
			insertButton.setEnabled(false);
			try {
			//	URL url = new URL(sUrl);
				OTrunkUtil.setNonPathPropertyValue(property, otObject, value);
				
			} catch (NoSuchMethodException ex){
				System.err.println("** No such property \"" + property + "\" for " + otObject.getName());
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(insertButton,
					    "Bad URL: An object cannot be loaded from the specified location",
					    "Warning",
					    JOptionPane.ERROR_MESSAGE);
			}
			updatePanel();
		}
		
	}



}
