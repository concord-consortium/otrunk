/*
 * Last modification information:
 * $Revision: 1.12 $
 * $Date: 2007-04-24 15:07:08 $
 * $Author: sfentress $
 *
 * Licence Information
 * Copyright 2007 The Concord Consortium 
*/
package org.concord.otrunk.view.document;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewEntryAware;
import org.concord.otrunk.view.OTObjectEditViewConfig;
import org.concord.otrunk.view.OTObjectListViewer;

/**
 * OTCompoundDocEditView
 * This is an edit view for the compound document
 * It has a split pane with a preview in the bottoms
 * and a text area at the top for editing the code of the document 
 *
 * Date created: Feb 5, 2007
 *
 * @author Ingrid Moncada<p>
 *
 */
public class OTCompoundDocEditView extends AbstractOTDocumentView
	implements OTViewEntryAware, ActionListener
{
	protected JPanel textPanel;
	protected OTDocumentView previewView;
	protected OTObjectEditViewConfig viewEntry;
	
	/**
	 * 
	 */
	public OTCompoundDocEditView()
	{
		super();
	}

	public JComponent getComponent(OTObject otObject, boolean editable)
	{
		//Get the OTDocumentView view as the 'hardcoded' preview
		previewView = (OTDocumentView)getViewFactory().getView(otObject, OTDocumentView.class);
		
		//set mode of OTDocumentView to mode set for OTObjectEditViewConfig, so objects
		//in the Doc will be in the correct mode
		previewView.setViewMode(viewEntry.getMode());
		
		//System.out.println("preview view is " + previewView);
				
		//Create a split pane with the preview pane and the text area 
		JComponent editTextPane = super.getComponent(otObject, true);
		JPanel editPane = createTextPanel(editTextPane);
		
		//If preview is not available, don't use it then
		if (previewView == null){
			return editPane;
		}
		else{
			JComponent previewPane = previewView.getComponent(otObject, false);
			
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
			                           editPane, previewPane);
			splitPane.setOneTouchExpandable(true);
			splitPane.setDividerLocation(150);
	
			return splitPane;
		}
	}
	
	/**
	 * Sets up the top panel that allows to edit the text and 
	 * also has the buttons to insert objects
	 */
	public JPanel createTextPanel(JComponent editTextPane)
	{
		textPanel = new JPanel();
		textPanel.setLayout(new BorderLayout());
		
		//Add the text edit pane at the top
		textPanel.add(editTextPane);
		
		//Create buttons and add to the bottom
		JPanel buttonsPanel = new JPanel();

		JButton addObjectButton = new JButton("Insert Object");
		buttonsPanel.add(addObjectButton);
		addObjectButton.setActionCommand("insertObject");
		addObjectButton.addActionListener(this);
		
		JButton updatePreviewButton = new JButton("Update Preview");
		buttonsPanel.add(updatePreviewButton);
		updatePreviewButton.setActionCommand("updatePreview");
		updatePreviewButton.addActionListener(this);
		
		textPanel.add(buttonsPanel, BorderLayout.NORTH);
		
		return textPanel;
	}

	/**
	 * Listens to insert button
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("insertObject")){
			
			if (textArea == null){
				System.err.println("Error, CompoundDocEditPane has no text area to use. Should I use the compound doc directly?");
				return;
			}
			
			OTObject objToInsert = getObjectToInsertFromUser();
			
			if (objToInsert == null){
				//No object to insert. Either we couldn't find one, or the user changed his mind
				return;
			}
									
			String strObjID = objToInsert.getGlobalId().toString();
			
			String strObjText = "<object refid=\"" + strObjID + "\" />";
			
			int pos = textArea.getSelectionStart();
			textArea.insert(strObjText, pos);
			
			updatePreviewView();
			
		}
		else if (e.getActionCommand().equals("updatePreview")){
			
			updatePreviewView();
		}
	}

	/**
	 * 
	 */
	private void updatePreviewView()
	{
		if (previewView == null) return;
		previewView.updateFormatedView();
	}

	/**
	 * Shows a dialog with the list of possible objects to insert and lets the user choose
	 * 
	 * @return OT Object selected by the user
	 */
	private OTObject getObjectToInsertFromUser()
	{
		OTObject otObj = null;
		
		otObj = OTObjectListViewer.showDialog(textPanel, "Choose object to add", getFrameManager(), getViewFactory(), 
				viewEntry.getObjectsToInsert(), ((OTCompoundDoc)pfObject).getOTObjectService(), true, false);
		
		return otObj;
	}

	/**
	 * @see org.concord.framework.otrunk.view.OTViewEntryAware#setViewEntry(OTViewEntry)
	 */
	public void setViewEntry(OTViewEntry viewEntry)
	{
		this.viewEntry = (OTObjectEditViewConfig)viewEntry;
	}
	
	public OTObjectEditViewConfig getViewEntry()
	{
		return viewEntry;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.document.AbstractOTDocumentView#viewClosed()
	 */
	public void viewClosed() 
	{
		super.viewClosed();
		
		if(previewView != null){
			previewView.viewClosed();
		}
	}
}
