/**
 * 
 */
package org.concord.otrunk.view;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTObjectView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerAware;
import org.concord.view.SimpleTreeModel;

/**
 * @author sfentress
 * 
 */
public class OTFolderObjectView implements OTObjectView, OTViewContainerAware,
		TreeSelectionListener {

	private OTFolderObject otFolderObject;

	private JTree tree;

	private SimpleTreeModel treeModel;

	// private JScrollPane folderViewPane;
	// private JEditorPane folderView;
	private OTViewContainer viewContainer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.framework.otrunk.view.OTObjectView#getComponent(org.concord.framework.otrunk.OTObject,
	 *      boolean)
	 */
	public JComponent getComponent(OTObject otObject, boolean editable) {
		otFolderObject = (OTFolderObject) otObject;

		treeModel = new SimpleTreeModel();
		updateTreePane();
		
		JScrollPane treeScrollPane = new JScrollPane(tree);

		return treeScrollPane;
	}

	public void updateTreePane() {
		treeModel.setRoot(new OTFolderNode((OTFolder) otFolderObject));
		tree = new JTree(treeModel);

		tree.setEditable(false);
		tree.addTreeSelectionListener(this);

		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.framework.otrunk.view.OTObjectView#viewClosed()
	 */
	public void viewClosed() {
		// TODO Auto-generated method stub

	}

	public void setViewContainer(OTViewContainer container) {
		viewContainer = container;
	}

	public void valueChanged(TreeSelectionEvent e) {
		OTFolderNode node = (OTFolderNode) tree.getLastSelectedPathComponent();

		if (node == null)
			return;
		
		OTObject linkObj = (OTObject) node.getObject();
		if (!(linkObj instanceof OTFolderObject)){
			viewContainer.setCurrentObject(linkObj);
		}
	}

}
