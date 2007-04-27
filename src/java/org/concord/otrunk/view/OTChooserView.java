package org.concord.otrunk.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewEntryAware;
import org.concord.otrunk.OTrunkUtil;
import org.concord.otrunk.view.document.OTCompoundDocEditView;

public class OTChooserView extends AbstractOTJComponentView implements OTViewEntryAware, ActionListener{
	
	OTObject otObject;
	
	String name;
	
	OTViewEntry viewEntry;
	
	JButton insertButton;
	
	JPanel mainPanel;
	
	public JComponent getComponent(OTObject otObject, boolean editable) {
		
		this.otObject = otObject;
		
		if (viewEntry instanceof OTChooserViewEntry){
			mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			name = (otObject.getName() != null? otObject.getName() : "object");
			insertButton = new JButton();
			insertButton.setText("Insert " + name);
			insertButton.addActionListener(this);
			mainPanel.add(insertButton);
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

	public void actionPerformed(ActionEvent e) {
		String property = ((OTChooserViewEntry)viewEntry).getPropertyName();
		String mode = ((OTChooserViewEntry)viewEntry).getFinalViewMode();
		
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
				JComponent mwPanel = getChildComponent(otObject, null, false,
						"normal");
				mainPanel.removeAll();
				mainPanel.add(mwPanel);
				insertButton.setText("Change " + name);
				mainPanel.add(insertButton);
			} catch (NoSuchMethodException ex){
				System.err.println("** No such property \"" + property + "\" for " + otObject.getName());
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(insertButton,
					    "Bad URL: An object cannot be loaded from the specified location",
					    "Warning",
					    JOptionPane.ERROR_MESSAGE);
				insertButton.setText("Insert " + name);
			}
			insertButton.setEnabled(true);
		}
		
	}



}
