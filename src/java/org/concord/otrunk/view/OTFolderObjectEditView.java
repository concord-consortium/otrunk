/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2007-02-20 04:39:59 $
 * $Author: imoncada $
 *
 * Licence Information
 * Copyright 2007 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.TreeSelectionEvent;

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
	protected OTObject selectedObject;
	
	/**
	 * Called when an element in the tree is selected
	 * (copied from OTFolderObjectView)
	 */
	public void valueChanged(TreeSelectionEvent e) 
	{
		OTFolderNode node = (OTFolderNode)tree.getLastSelectedPathComponent();

		if (node == null) return;
		
		selectedObject = (OTObject) node.getObject();
	}	

	public void updateTreePane() 
	{
		super.updateTreePane();
		tree.addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}

	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger()){
			System.out.println("right click");
		}
	}
	
	
}
