/*
 * Last modification information:
 * $Revision: 1.8 $
 * $Date: 2007-07-19 12:12:26 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2007 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.view.OTFrameManager;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.otrunk.VirtualFolder;
import org.concord.swing.CustomDialog;
import org.concord.view.SimpleTreeModel;


/**
 * OTObjectListViewer
 * 
 * Given a list of OT Objects, this panel shows them in a tree view
 * and it keeps track of the object selected by the user.
 * 
 * There is a static convenience method to show this panel in a modal dialog
 * and let the user choose an object.
 *
 * Date created: Feb 10, 2007
 *
 * @author Ingrid Moncada<p>
 *
 */
public class OTObjectListViewer extends JPanel 
	implements TreeSelectionListener
{
	
    private static final long serialVersionUID = 1L;
    
	protected OTFrameManager frameManager;
	protected OTViewFactory oTViewFactory;
	
	protected SimpleTreeModel folderTreeModel;
	protected OTViewContainerPanel viewPanel;
	
	protected OTObject currentSelectedOTObj;
	
	protected JCheckBox copyCheck;

	private boolean showPreview;
	
	/**
	 * Convenience method to show this viewer in a modal dialog and let the user choose an object
	 * It returns the object chosen by the user. If the "copy object" checkbox was checked, it
	 * makes a copy of the object and returns it.
	 * It receives a bunch of parameters in order to display the OT objects.
	 * The most crutial parameter is viewConfig, which has the list of objects to be displayed in the dialog
	 * 
	 * @param parent		Parent of the dialog
	 * @param title			Title of the dialog to be displayed
	 * @param frameManager	the OT frame manager, used to pass it to the views of the objects displayed in the dialog
	 * @param viewFactory	the OT View factory, used to create the views for the objects displayed in the dialog
	 * @param objList		An list of OT objects to choose from 
	 * @param otObjService	An OT Object service, used to make a copy of the OT object, or null, if object copying is not needed.
	 * @param enableCopyOption	Determines whether the "copy object" checkbox should be enabled or not
	 * @param defaultCopyOption	Determines if the "copy object" checkbox is selected by default or not
	 * @return	The OT object selected by the user
	 */
	public static OTObject showDialog(Component parent, String title, OTFrameManager frameManager, OTViewFactory viewFactory,
			OTObjectList objList, OTObjectService otObjService, boolean enableCopyOption, boolean defaultCopyOption)
	{
		return showDialog(parent, title, frameManager, viewFactory, objList, otObjService, enableCopyOption, defaultCopyOption, false);
	}
	
	/**
	 * Convenience method to show this viewer in a modal dialog and let the user choose an object
	 * It returns the object chosen by the user. If the "copy object" checkbox was checked, it
	 * makes a copy of the object and returns it.
	 * It receives a bunch of parameters in order to display the OT objects.
	 * The most crutial parameter is viewConfig, which has the list of objects to be displayed in the dialog
	 * 
	 * @param parent		Parent of the dialog
	 * @param title			Title of the dialog to be displayed
	 * @param frameManager	the OT frame manager, used to pass it to the views of the objects displayed in the dialog
	 * @param viewFactory	the OT View factory, used to create the views for the objects displayed in the dialog
	 * @param objList		An list of OT objects to choose from 
	 * @param otObjService	An OT Object service, used to make a copy of the OT object, or null, if object copying is not needed.
	 * @param enableCopyOption	Determines whether the "copy object" checkbox should be enabled or not
	 * @param defaultCopyOption	Determines if the "copy object" checkbox is selected by default or not
	 * @param showPreview	Whether a preview of the object should be displayed
	 * @return	The OT object selected by the user
	 */
	public static OTObject showDialog(Component parent, String title, OTFrameManager frameManager, OTViewFactory viewFactory,
			OTObjectList objList, OTObjectService otObjService, boolean enableCopyOption, boolean defaultCopyOption, boolean showPreview)
	{
		OTObject otObj;
		
		//Show the user all the possible objects to insert so he can choose
		if (objList == null || objList.size() == 0){
			System.err.println("Error: list of objects is empty or null. No objects to insert.");
			return null;
		}
				
		OTObjectListViewer selectPanel = new OTObjectListViewer(frameManager, showPreview);
		selectPanel.setOTViewFactory(viewFactory);
		selectPanel.setOtObjList(objList);
		selectPanel.configCopyObjectOption(enableCopyOption, defaultCopyOption);
		
		//If there is no object service, we cannot copy the object!
		if (otObjService == null){
			selectPanel.configCopyObjectOption(false, false);
			System.err.println("Warning: object cannot be copied because the OT Object Service is null.");
		}
		
		int retCode = CustomDialog.showOKCancelDialog(parent, selectPanel, title, true, true);
		selectPanel.close();
		
		if (retCode == JOptionPane.OK_OPTION){
			otObj = selectPanel.getCurrentOTObject();
			if (otObj == null) return null;
			
			if (selectPanel.getCopyObjectOption()){
				if (otObjService != null){
					//Create a new instance of the object to insert with this template
					//and add a object reference in that case
					try{
						otObj = otObjService.copyObject(otObj, -1);
					}
					catch(Exception ex){
						System.err.println("Warning: object could not be copied.");
						ex.printStackTrace();
					}
				}
				else{
					System.err.println("Warning: object could not be copied because the OT Object Service was null.");
				}
			}
			return otObj;			
		}
		else{
			return null;
		}
	}
	
	/**
	 * 
	 */
	public OTObjectListViewer(OTFrameManager frameManager, boolean showPreview)
	{
		super();
		this.frameManager = frameManager;
		this.showPreview = showPreview;
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
			//System.out.println("adding "+otObj);
		}
		
		//Create the tree to display 
		folderTreeModel = new SimpleTreeModel();
		folderTreeModel.setRoot(new OTFolderNode(rootFolder));
		JTree tree = new JTree(folderTreeModel);
		tree.addTreeSelectionListener(this);
		
		if (showPreview ){
    		//Create the objectView
    		viewPanel = new OTViewContainerPanel(frameManager);
    		viewPanel.setOTViewFactory(oTViewFactory);
    		viewPanel.setVisible(false);
    		
    		JPanel rightPanel = new JPanel();
    		rightPanel.setPreferredSize(new Dimension(300,200));
    		rightPanel.add(viewPanel);
    		
    		add(rightPanel);
		}
		
		copyCheck = new JCheckBox("Make just a copy of the object");
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(copyCheck);
		
		add(tree, BorderLayout.WEST);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	/**
	 * Returns the current selected OT object
	 * @return
	 */
	public OTObject getCurrentOTObject()
	{
		return currentSelectedOTObj;
	}
	
	public boolean getCopyObjectOption()
	{
		return copyCheck.isSelected();
	}
	
	public void configCopyObjectOption(boolean enableCopyOption, boolean defaultCopyOption)
	{
		copyCheck.setEnabled(enableCopyOption);
		copyCheck.setSelected(defaultCopyOption);
	}
	
	public void setCopyObjectOption(boolean b)
	{
		copyCheck.setSelected(b);
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
		} else {
			currentSelectedOTObj = null;
		}
			
		if (showPreview){
			viewPanel.setCurrentObject(currentSelectedOTObj);
			if (currentSelectedOTObj != null){
				viewPanel.setVisible(true);
			}
			else{
				viewPanel.setVisible(false);				
			}
		}
	}

	/**
	 * This method should be called in order to release all the references in the views
	 */
	public void close()
	{
		if (showPreview){
			viewPanel.setCurrentObject(null);
		}
	}
}
