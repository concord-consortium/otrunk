/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2007-02-11 03:09:39 $
 * $Author: imoncada $
 *
 * Licence Information
 * Copyright 2007 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.view.OTFrameManager;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.otrunk.VirtualFolder;
import org.concord.view.SimpleTreeModel;


/**
 * OTObjectListViewer
 * 
 * Given a list of OT Objects, this panel shows them in a tree view
 * and it keeps track of the object selected by the user.
 *
 * Date created: Feb 10, 2007
 *
 * @author Ingrid Moncada<p>
 *
 */
public class OTObjectListViewer extends JPanel 
	implements TreeSelectionListener
{
	protected OTFrameManager frameManager;
	protected OTViewFactory oTViewFactory;
	
	protected SimpleTreeModel folderTreeModel;
	protected OTViewContainerPanel viewPanel;
	
	protected OTObject currentSelectedOTObj;
	
	/**
	 * 
	 */
	public OTObjectListViewer(OTFrameManager frameManager)
	{
		super();
		this.frameManager = frameManager;
		currentSelectedOTObj = null;
		
		setLayout(new BorderLayout());
	}
	
	public void setOTViewFactory(OTViewFactory oTViewFactory)
	{
		this.oTViewFactory = oTViewFactory;
	}
	
	/**
	 * Sets the list of OT objects to show
	 * @param otObjList
	 */
	public void setOtObjList(OTObjectList otObjList)
	{
		initView(otObjList);
	}
	
	protected void initView(OTObjectList otObjList)
	{
		//Create a "fake" folder with the list of objects to insert
		VirtualFolder rootFolder = new VirtualFolder();
		rootFolder.setName("Objects to insert");
		OTObject otObj;
		
		for (int i=0; i<otObjList.size(); i++){
			otObj = otObjList.get(i);
			rootFolder.addVirtualChild(otObj);
			System.out.println("adding "+otObj);
		}
		
		//Create the tree to display 
		folderTreeModel = new SimpleTreeModel();
		folderTreeModel.setRoot(new OTFolderNode(rootFolder));
		JTree tree = new JTree(folderTreeModel);
		tree.addTreeSelectionListener(this);
		
		//Create the objectView
		viewPanel = new OTViewContainerPanel(frameManager);
		viewPanel.setOTViewFactory(oTViewFactory);
		viewPanel.setVisible(false);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setPreferredSize(new Dimension(300,200));
		rightPanel.add(viewPanel);
		
		add(rightPanel);
		add(tree, BorderLayout.WEST);
	}

	/**
	 * Returns the current selected OT object
	 * @return
	 */
	public OTObject getCurrentOTObject()
	{
		return currentSelectedOTObj;
	}

	/**
	 * Listens to the selection on the tree and keeps track of the selected object 
	 *
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		JTree tree = (JTree)e.getSource();
		OTFolderNode node = (OTFolderNode)tree.getLastSelectedPathComponent();
		
		if (node != null){
			currentSelectedOTObj = node.getPfObject();
			
			if (currentSelectedOTObj != null){
				viewPanel.setCurrentObject(currentSelectedOTObj);
				viewPanel.setVisible(true);
			}
			else{
				viewPanel.setVisible(false);				
			}
		}
		else{
			viewPanel.setVisible(false);
		}
	}

}
