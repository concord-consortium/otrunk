/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2007-02-20 06:01:56 $
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

/**
 * OTFolderObjectEditView
 * Edit view for the OTFolderObject
 *
 * Date created: Feb 19, 2007
 *
 * @author Ingrid Moncada<p>
 *
 */
public class OTFolderObjectEditView extends OTFolderObjectView 
	implements MouseListener
{
	protected OTObject parentObject;
	protected OTObject selectedObject;
	
	protected JMenu menu;
	
	/**
	 * Called when an element in the tree is selected
	 * (copied from OTFolderObjectView)
	 */
	public void valueChanged(TreeSelectionEvent e) 
	{		
		OTFolderNode parentNode = null;
		OTFolderNode node = null;
		
		TreePath path = tree.getSelectionPath();
		if (path == null) return;

	    node = (OTFolderNode)path.getLastPathComponent();
		if (node == null) return;
		
		selectedObject = (OTObject)node.getObject();
		
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
	 * 
	 */
	protected void showEditPopUpMenu(MouseEvent evt)
	{
		if (selectedObject == null) return;
		
		if (menu == null) createMenu();
		
		menu.getPopupMenu().show(tree, evt.getX(), evt.getY());
		
		menu = null;
	}

	/**
	 * 
	 */
	protected void createMenu()
	{		
		menu = new JMenu();
		menu.add(new OTFolderObjectAction("insert"));
		if (parentObject != null){
			menu.add(new OTFolderObjectAction("delete"));
		}
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
			
			if (actionCommand.equals("insert")){
				putValue(Action.NAME, "Insert object");
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
			if (e.getActionCommand().equals("insert")){
				System.out.println("insert object not implemented yet");
			}
			else if (e.getActionCommand().equals("delete")){
				
				if (parentObject instanceof OTFolder){
					((OTFolder)parentObject).removeChild(selectedObject);
				}
			}
		}
	}
}
