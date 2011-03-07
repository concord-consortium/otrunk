package org.concord.otrunk.util;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTObjectTree;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDataPropertyReference;

public class ObjectFinderDialog {
	private static final Logger logger = Logger.getLogger(ObjectFinderDialog.class.getName());
    private OTObject chosenObject;
    private final OTrunkImpl otrunk;
    private final OTObject currentObject;
    private final OTObject root;
    
    public ObjectFinderDialog(OTrunkImpl otrunk, OTObject currentObject, OTObject root) {
        this.otrunk = otrunk;
        this.currentObject = currentObject;
        this.root = root;
    }
    
    public OTObject showFinder(Component parent) {
        final JPanel objectPanel = getObjectPanel();
        objectPanel.setMaximumSize(new Dimension(800,500));
        
        // make the option pane resizable
        objectPanel.addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                Window window = SwingUtilities.getWindowAncestor(objectPanel);
                if (window instanceof Dialog) {
                    Dialog dialog = (Dialog)window;
                    if (!dialog.isResizable()) {
                        dialog.setResizable(true);
                    }
                }
            }
        });
        
        int choice = JOptionPane.showConfirmDialog(parent, objectPanel, "Select an object", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice == JOptionPane.OK_OPTION) {
            return chosenObject;
        }
        return currentObject;
    }
    
    protected JPanel getObjectPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Objects"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        HashMap<OTID, DefaultMutableTreeNode> childMap = new HashMap<OTID, DefaultMutableTreeNode>();

        final DefaultMutableTreeNode rootNode = populateTree(otrunk, root, childMap);
        
        final JTree tree = new OTObjectTree(rootNode);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setExpandsSelectedPaths(true);
        
        if (currentObject != null) {
            tree.setSelectionPath(new TreePath(childMap.get(currentObject.getGlobalId()).getPath()));
        }
        
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                Object userObject = ((DefaultMutableTreeNode)e.getNewLeadSelectionPath().getLastPathComponent()).getUserObject();
                if (userObject instanceof OTObject) {
                    chosenObject = (OTObject)userObject;
                } else {
                    chosenObject = null;
                }
            }
        });
        
        JScrollPane scroll = new JScrollPane(tree);
        scroll.setPreferredSize(new Dimension(400,400));
        panel.add(scroll);
        
        return panel;
    }

	public static DefaultMutableTreeNode populateTree(OTrunkImpl otrunk, OTObject sourceObject, HashMap<OTID, DefaultMutableTreeNode> knownNodes) {
		if (knownNodes == null) {
			knownNodes = new HashMap<OTID, DefaultMutableTreeNode>();
		}
		
		DefaultMutableTreeNode objNode = new DefaultMutableTreeNode(sourceObject);
		knownNodes.put(sourceObject.getGlobalId(), objNode);
		
		ArrayList<OTID> excludeIds = new ArrayList<OTID>();
		
		ArrayList<ArrayList<OTDataPropertyReference>> children = otrunk.getOutgoingReferences(sourceObject.getGlobalId(), OTObject.class, true, excludeIds);
		for (ArrayList<OTDataPropertyReference> childPath : children) {
//			logger.finer("Next child");
			for (int i = childPath.size()-1; i >= 0; i--) {
				OTDataPropertyReference ref = childPath.get(i);
				DefaultMutableTreeNode parentNode = knownNodes.get(ref.getSource());
				DefaultMutableTreeNode childNode = knownNodes.get(ref.getDest());
//				logger.finer(ref.getSource() + " ---> " + ref.getDest());
				
				if (parentNode == null) {
					try {
//						logger.finer("Creating parent node for: " + ref.getSource());
						parentNode = new DefaultMutableTreeNode(otrunk.getOTObject(ref.getSource()));
						knownNodes.put(ref.getSource(), parentNode);
					} catch (Exception e) {
						logger.log(Level.WARNING, "Couldn't get OTObject for parent OTID", e);
					}
				}
				
				if (childNode == null) {
					try {
//						logger.finer("Creating child node for: " + ref.getDest());
						childNode = new DefaultMutableTreeNode(otrunk.getOTObject(ref.getDest()));
						knownNodes.put(ref.getDest(), childNode);
					} catch (Exception e) {
						logger.log(Level.WARNING, "Couldn't get OTObject for child OTID", e);
					}
				}
				
				if (childNode == null || parentNode == null) {
//					logger.finer("Creating child or parent node failed.");
					continue;
				}
				
//				logger.finer("Childnode's path length: " + childNode.getPath().length);
				
				if (childNode.getPath().length < 2) {
//					logger.finer("Adding child: " + childNode + " to parent: " + parentNode);
					try {
						parentNode.add(childNode);
					} catch (IllegalArgumentException e) {
						// sometimes a node is already somewhere higher up in the tree
						// in that case, skip it.
					}
				} else {
//					logger.finer("Child: " + childNode + ", parent path: " + childNode.getPath());
				}
			}
		}
		
		return objNode;
	}
}
