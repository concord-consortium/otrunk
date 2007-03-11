/**
 * 
 */
package org.concord.otrunk.view;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTView;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerAware;
import org.concord.view.SimpleTreeModel;

/**
 * OTFolderObjectView
 * View for OTFolderObject
 *
 * Date created: Feb 16, 2007
 *
 * @author sfentress
 * 
 */
public class OTFolderObjectView extends AbstractOTView 
	implements OTJComponentView, OTViewContainerAware,
		TreeSelectionListener {

	protected OTFolderObject otFolderObject;

	protected JTree tree;

	protected SimpleTreeModel treeModel;

	// private JScrollPane folderViewPane;
	// private JEditorPane folderView;
	protected OTViewContainer viewContainer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.framework.otrunk.view.OTJComponentView#getComponent(org.concord.framework.otrunk.OTObject,
	 *      boolean)
	 */
	public JComponent getComponent(OTObject otObject, boolean editable) {
		otFolderObject = (OTFolderObject) otObject;

		treeModel = new SimpleTreeModel();
		updateTreePane();

		return tree;
	}

	protected void updateTreePane() {
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
	 * @see org.concord.framework.otrunk.view.OTJComponentView#viewClosed()
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
