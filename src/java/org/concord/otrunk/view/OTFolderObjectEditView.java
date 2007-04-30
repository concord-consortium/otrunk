/*
 * Last modification information:
 * $Revision: 1.11 $
 * $Date: 2007-04-30 18:43:02 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2007 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTFrameManager;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewEntryAware;
import org.concord.framework.otrunk.view.OTViewFactory;

/**
 * OTFolderObjectEditView
 * Edit view for the OTFolderObject
 * Allows the user to add and delete objects from the folder structure
 *
 * Date created: Feb 19, 2007
 *
 * @author Ingrid Moncada<p>
 *
 */
public class OTFolderObjectEditView extends OTFolderObjectView 
	implements MouseListener, OTViewEntryAware
{
	protected OTObject parentObject;
	protected OTObject selectedObject;
	protected OTFolderNode selectedNode;
	protected OTFolderNode parentNode;
	protected TreePath selectedPath;
	
	protected OTObjectEditViewConfig viewEntry;
	
	protected JMenu menu;
	
	/**
	 * Called when an element in the tree is selected
	 * (copied from OTFolderObjectView)
	 */
	public void valueChanged(TreeSelectionEvent e) 
	{		
		parentNode = null;
		selectedNode = null;
		
		selectedPath = tree.getSelectionPath();
		if (selectedPath == null) return;

		selectedNode = (OTFolderNode)selectedPath.getLastPathComponent();
		if (selectedNode == null) return;
		
		selectedObject = (OTObject)selectedNode.getObject();
		
		TreePath parentPath;
		parentPath = selectedPath.getParentPath();
		
		if (parentPath != null){
			parentNode = (OTFolderNode)parentPath.getLastPathComponent();
			parentObject = (OTObject)parentNode.getObject();
		}
		else{
			parentObject = null;
		}
				
		//System.out.println("parent: "+ parentObject);
		//System.out.println("object: "+ selectedObject);
	}	

	public void updateTreePane() 
	{
		super.updateTreePane();
		tree.addMouseListener(this);
		//Allow editing node names
		tree.setEditable(true);
		tree.setDragEnabled(true);
	}

	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent evt)
	{
		if (evt.isPopupTrigger()){
			showEditPopUpMenu(evt);
		}
	}

	public void mouseReleased(MouseEvent evt)
	{
		if (evt.isPopupTrigger()){
			showEditPopUpMenu(evt);
		}
	}
	
	/**
	 * Shows a pop up menu with the edit actions
	 */
	protected void showEditPopUpMenu(MouseEvent evt)
	{
		if (selectedObject == null) return;
		
		if (menu == null) createMenu();
		
		menu.getPopupMenu().show(tree, evt.getX(), evt.getY());
		
		menu = null;
	}

	/**
	 * Creates a new pop up menu with the edit actions
	 * Right now only "add" and "delete" are implemented
	 */
	protected void createMenu()
	{		
		menu = new JMenu();
		//"Add" action is only for folders
		if (selectedObject instanceof OTFolder){
			menu.add(new OTFolderObjectAction("add"));
		}
		//Delete action only for objects with a parent folder
		if (parentObject != null){
			menu.add(new OTFolderObjectAction("delete"));
		}
		//Edit action 
		menu.add(new OTFolderObjectAction("rename"));
		menu.addSeparator();
		//Move
		menu.add(new OTFolderObjectAction("moveup"));
		menu.add(new OTFolderObjectAction("movedown"));
	}
	
	class OTFolderObjectAction extends AbstractAction
	{
		/**
		 * Not intended to be serialized, just added remove compile warning
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 *
		 */
		public OTFolderObjectAction(String actionCommand)
		{
			super(actionCommand);
			putValue(Action.ACTION_COMMAND_KEY, actionCommand);
			
			if (actionCommand.equals("add")){
				putValue(Action.NAME, "Add object");
			}
			else if (actionCommand.equals("delete")){
				putValue(Action.NAME, "Delete");
			}
			else if (actionCommand.equals("rename")){
				putValue(Action.NAME, "Rename");
			}
			else if (actionCommand.equals("moveup")){
				putValue(Action.NAME, "Move up");
			}
			else if (actionCommand.equals("movedown")){
				putValue(Action.NAME, "Move down");
			}
		}
		
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("action command: "+e.getActionCommand());
			if (e.getActionCommand().equals("add")){

				addObject();
			}
			else if (e.getActionCommand().equals("delete")){
				
				deleteSelectedObject();
			}
			else if (e.getActionCommand().equals("rename")){
				
				editSelectedObject();
			}
			else if (e.getActionCommand().equals("moveup")){
				
				moveUpSelectedObject();
			}
			else if (e.getActionCommand().equals("movedown")){
				
				moveDownSelectedObject();
			}
		}

		/**
		 * @param string
		 */
		protected void moveUpSelectedObject()
		{
			if (selectedObject == null || parentObject == null){
				System.err.println("Error: cannot move if selected object or parent are null");
				return;
			}
			
			int index = parentNode.getIndexOfChild(selectedNode);
			
			//Determine if it's the first child
			if (index == 0){
				TreePath upPath = selectedPath.getParentPath().getParentPath();
				
				if (upPath == null){
					//No path to move to
					System.err.println("Cannot move more up.");
					return;
				}

				((OTFolder)parentObject).removeChild(selectedObject);
				OTFolderNode upNode = (OTFolderNode)upPath.getLastPathComponent();
				OTFolderObject upObject = (OTFolderObject)upNode.getPfObject();
				
				int upIndex = upNode.getIndexOfChild(parentNode);
				upObject.addChild(upIndex, selectedObject);
			}
			else{
				((OTFolder)parentObject).removeChild(selectedObject);
				
				OTFolderNode upNode = (OTFolderNode)parentNode.getChild(index -1);
				OTObject upObject = upNode.getPfObject();
				
				if (upObject instanceof OTFolder){
					OTFolder upFolder = (OTFolder)upObject;
					upFolder.addChild(selectedObject);
				}
				else{
					((OTFolder)parentObject).addChild(index - 1, selectedObject);
				}
			}

			treeModel.fireTreeStructureChanged(selectedNode);
		}

		/**
		 * @param string
		 */
		protected void moveDownSelectedObject()
		{
			if (selectedObject == null || parentObject == null){
				System.err.println("Error: cannot move if selected object or parent are null");
				return;
			}
			
			int index = parentNode.getIndexOfChild(selectedNode);
			
			//Determine if it's the last child
			if (index == parentNode.getChildCount() - 1){
				TreePath upPath = selectedPath.getParentPath().getParentPath();
				
				if (upPath == null){
					//No path to move to
					System.err.println("Cannot move more down.");
					return;
				}
				
				((OTFolder)parentObject).removeChild(selectedObject);
				OTFolderNode upNode = (OTFolderNode)upPath.getLastPathComponent();
				OTFolderObject upObject = (OTFolderObject)upNode.getPfObject();
				
				int upIndex = upNode.getIndexOfChild(parentNode);
				upObject.addChild(upIndex + 1, selectedObject);
			}
			else{
				((OTFolder)parentObject).removeChild(selectedObject);

				OTFolderNode upNode = (OTFolderNode)parentNode.getChild(index);
				OTObject upObject = upNode.getPfObject();
				
				if (upObject instanceof OTFolder){
					OTFolder upFolder = (OTFolder)upObject;
					upFolder.addChild(0, selectedObject);
				}
				else{
					((OTFolder)parentObject).addChild(index, selectedObject);
				}
			}

			treeModel.fireTreeStructureChanged(selectedNode);
		}
		
		/**
		 * 
		 */
		protected void editSelectedObject()
		{
			tree.startEditingAtPath(tree.getSelectionPath());
		}

		/**
		 * Adds an ot object to the selected folder. Lets the user pick which object to add.
		 */
		protected void addObject()
		{
			if (selectedObject == null) return;
			
			OTObject otObj = getObjectToInsertFromUser();
			
			if (otObj == null) return;
			
			if (selectedObject instanceof OTFolder){
				((OTFolder)selectedObject).addChild(otObj);
				
				treeModel.fireTreeStructureChanged(selectedNode);
			}
			else{
				System.err.println("Error: OT Objects can only be added to OT Folders.");
			}
		}

		/**
		 * Shows a dialog with the list of possible objects to insert and lets the user choose
		 * selected object is guaranteed to be not null
		 * 
		 * @return OT Object selected by the user
		 */
		private OTObject getObjectToInsertFromUser()
		{
			if (viewEntry == null) return null;
			
			OTViewFactory viewFactory = getViewFactory();
			OTFrameManager frameManager = getFrameManager();
			
			OTObject otObj = null;
			
			otObj = OTObjectListViewer.showDialog(tree, "Choose object to add to the tree", frameManager, viewFactory, 
					viewEntry.getObjectsToInsert(), selectedObject.getOTObjectService(), true, true);		//Last parameter is null because we don't have an ot object service yet
			
			return otObj;
		}
		
		/**
		 * Deletes the selected object from its parent folder
		 */
		protected void deleteSelectedObject()
		{
			if (parentObject instanceof OTFolder){
				((OTFolder)parentObject).removeChild(selectedObject);
			}
			else{
				System.err.println("Error: OT Object deletion only available for objects inside of folders.");
			}
		}
	}

	/**
	 * @see org.concord.framework.otrunk.view.OTViewEntryAware#setViewEntry(OTViewEntry)
	 */
	public void setViewEntry(OTViewEntry viewEntry)
	{
		if (!(viewEntry instanceof OTObjectEditViewConfig)){
			System.err.println("Error: the specified view config should be an istance of OTObjectEditViewConfig. View entry specified is: " + viewEntry);
			this.viewEntry = null;
			return;
		}
		
		this.viewEntry = (OTObjectEditViewConfig)viewEntry;
	}
}