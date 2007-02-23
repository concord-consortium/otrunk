/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2007-02-23 04:39:05 $
 * $Author: imoncada $
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
import org.concord.framework.otrunk.view.OTFrameManagerAware;
import org.concord.framework.otrunk.view.OTViewConfigAware;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.framework.otrunk.view.OTViewFactoryAware;

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
	implements MouseListener, OTFrameManagerAware, OTViewFactoryAware, OTViewConfigAware
{
	protected OTObject parentObject;
	protected OTObject selectedObject;
	protected OTFolderNode selectedNode;
	
	protected OTFrameManager frameManager;
	protected OTViewFactory viewFactory;
	protected OTObjectEditViewConfig viewConfig;
	
	protected JMenu menu;
	
	/**
	 * Called when an element in the tree is selected
	 * (copied from OTFolderObjectView)
	 */
	public void valueChanged(TreeSelectionEvent e) 
	{		
		OTFolderNode parentNode = null;
		selectedNode = null;
		
		TreePath path = tree.getSelectionPath();
		if (path == null) return;

		selectedNode = (OTFolderNode)path.getLastPathComponent();
		if (selectedNode == null) return;
		
		selectedObject = (OTObject)selectedNode.getObject();
		
		TreePath parentPath;
		parentPath = path.getParentPath();
		
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
	}
	
	/**
	 * @see org.concord.framework.otrunk.view.OTFrameManagerAware#setFrameManager(org.concord.framework.otrunk.view.OTFrameManager)
	 */
	public void setFrameManager(OTFrameManager frameManager)
	{
		this.frameManager = frameManager;		
	}

	/**
	 * @see org.concord.framework.otrunk.view.OTViewFactoryAware#setViewFactory(org.concord.framework.otrunk.view.OTViewFactory)
	 */
	public void setViewFactory(OTViewFactory viewFactory)
	{
		this.viewFactory = viewFactory;
	}

	class OTFolderObjectAction extends AbstractAction
	{
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
				putValue(Action.NAME, "Delete object");
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
			OTObject otObj = null;
			
			otObj = OTObjectListViewer.showDialog(tree, "Choose object to add to the tree", frameManager,
					viewFactory, viewConfig, selectedObject.getOTObjectService(), true, true);		//Last parameter is null because we don't have an ot object service yet
			
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
	 * @see org.concord.framework.otrunk.view.OTViewConfigAware#setViewConfig(org.concord.framework.otrunk.OTObject)
	 */
	public void setViewConfig(OTObject viewConfig)
	{
		if (!(viewConfig instanceof OTObjectEditViewConfig)){
			System.err.println("Error: the specified view config should be an istance of OTObjectEditViewConfig.");
			return;
		}
		
		this.viewConfig = (OTObjectEditViewConfig)viewConfig;
	}
}
