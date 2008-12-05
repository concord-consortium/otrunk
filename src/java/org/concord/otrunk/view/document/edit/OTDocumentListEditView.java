package org.concord.otrunk.view.document.edit;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;
import org.concord.otrunk.view.document.OTCompoundDoc;
import org.concord.otrunk.view.document.OTDocument;

/**
 * The ListEditView is a view for a document that would be a section of a larger
 * document, to be used in a template authoring system such as RITES.
 * 
 * This view is list authoring view, which can be used to create html lists
 * within a larger document.
 * 
 * @author sfentress
 *
 */
public class OTDocumentListEditView extends AbstractOTJComponentView implements ListSelectionListener
{

	JPanel listPanel;
	
	private DefaultListModel listModel;
	
	private JList list;
	
	private JButton removeButton;
	
    private JTextField optionText;

	private OTDocument document;
    	
	public JComponent getComponent(OTObject otObject)
	{
		document = (OTDocument)otObject;
		
		listPanel = new JPanel();
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panel.add(new JLabel("Items: "));
		panel.add(createListPanel());
		
		panel.setOpaque(false);
				
		return panel;
	}
	

	private JPanel createListPanel(){
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		
		listModel = new DefaultListModel();
		addOriginalOptionsToList();

        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(6);
        final JScrollPane listScrollPane = new JScrollPane(list);
        
        

        JButton addButton = new JButton("Add");
        AddListener addListener = new AddListener(addButton);
        addButton.setActionCommand("Remove");
        addButton.addActionListener(addListener);
        addButton.setEnabled(false);

        removeButton = new JButton("Remove");
        removeButton.setActionCommand("Remove");
        removeButton.addActionListener(new RemoveListener());
        removeButton.setEnabled(false);

        optionText = new JTextField(10);
        optionText.addActionListener(addListener);
        optionText.getDocument().addDocumentListener(addListener);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        
        buttonPane.add(optionText);
        buttonPane.add(addButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(removeButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
       
        listPanel.add(listScrollPane);
        listPanel.add(buttonPane);
        
        listPanel.setOpaque(false);
        buttonPane.setOpaque(false);
        optionText.setOpaque(false);
        
        return listPanel;
	}
	

	private void addOriginalOptionsToList(){
		String list = document.getDocumentText();
		if (list == null)
			return;
		
		Pattern p = Pattern.compile("<li>(.*?)</li>");
		Matcher m = p.matcher(list);
		while (m.find()) {
			String item = m.group(1);
			listModel.addElement(item);
		}
	}
	
	private void setOptions(){
		document.setDocumentText("<ul>");
		for (int i = 0; i < listModel.getSize(); i++) {
			addOption((String)listModel.getElementAt(i));
		}
		document.setDocumentText(document.getDocumentText() + "</ul>");
	}
	
	private void addOption(String option){
		document.setDocumentText(document.getDocumentText() + "<li>"+option+"</li>");
	}

	public void valueChanged(ListSelectionEvent e) {
		if(list.getSelectedIndex() >= 0)
			removeButton.setEnabled(true);
		else removeButton.setEnabled(false);
	}
	
	class AddListener implements ActionListener, DocumentListener {
        private boolean alreadyEnabled = false;
        private JButton button;

        public AddListener(JButton button) {
            this.button = button;
        }

        public void actionPerformed(ActionEvent e) {
            String name = optionText.getText();

            //User didn't type in a unique name...
            if (name.equals("") || alreadyInList(name)) {
                optionText.requestFocusInWindow();
                optionText.selectAll();
                return;
            }

            int index = list.getSelectedIndex();
            if (index == -1) {
                index = 0;
            } else {
                index++;
            }

            listModel.insertElementAt(optionText.getText(), index);
            
            optionText.requestFocusInWindow();
            optionText.setText("");

            list.setSelectedIndex(index);
            list.ensureIndexIsVisible(index);
            
            setOptions();
        }

        protected boolean alreadyInList(String name) {
            return listModel.contains(name);
        }

        public void insertUpdate(DocumentEvent e) {
            enableButton();
        }

        public void removeUpdate(DocumentEvent e) {
            handleEmptyTextField(e);
        }

        public void changedUpdate(DocumentEvent e) {
            if (!handleEmptyTextField(e)) {
                enableButton();
            }
        }

        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }

        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
                button.setEnabled(false);
                alreadyEnabled = false;
                return true;
            }
            return false;
        }
    }

	class RemoveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	
            int index = list.getSelectedIndex();
            listModel.remove(index);

            int size = listModel.getSize();

            if (size == 0) {
                removeButton.setEnabled(false);

            } else {
                if (index == listModel.getSize()) {
                    index--;
                }

                list.setSelectedIndex(index);
                list.ensureIndexIsVisible(index);
            }
            
            setOptions();
        }
    }

}
