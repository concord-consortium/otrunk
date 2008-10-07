package org.concord.otrunk.test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;

public class OTStretchingView extends AbstractOTJComponentView
{
	public JComponent getComponent(OTObject otObject)
	{
		JTextArea textArea = new JTextArea();
		// this border is just so it can be seen
		textArea.setBorder(BorderFactory.createEtchedBorder());		
		// This is required to make it stretch
		textArea.setLineWrap(true);

		JButton button = new JButton("v");		
		// This is required to make it stretch
		button.setPreferredSize(new Dimension(40,30));
		
		// it has to be a border layout
		JPanel borderLayoutPanel = new JPanel(new BorderLayout());

		borderLayoutPanel.add(textArea, BorderLayout.CENTER);
		borderLayoutPanel.add(button, BorderLayout.EAST);
		
		return borderLayoutPanel;
	}
}
