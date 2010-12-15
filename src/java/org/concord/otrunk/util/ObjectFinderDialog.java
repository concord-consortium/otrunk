package org.concord.otrunk.util;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTObjectTree;
import org.concord.otrunk.OTrunkImpl;

public class ObjectFinderDialog {
    private OTObject chosenObject;
    private final OTrunkImpl otrunk;
    private final OTObject currentObject;
    
    public ObjectFinderDialog(OTrunkImpl otrunk, OTObject currentObject) {
        this.otrunk = otrunk;
        this.currentObject = currentObject;
    }
    
    public OTObject showFinder(Component parent) {
        JPanel objectPanel = getObjectPanel();
        objectPanel.setMaximumSize(new Dimension(800,500));
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
        
        final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Objects");

        ArrayList<OTObject> multiUserObjects = otrunk.getAllObjects(OTObject.class);
        
        HashMap<OTID, DefaultMutableTreeNode> childMap = new HashMap<OTID, DefaultMutableTreeNode>();
        
        if (currentObject != null && ! multiUserObjects.contains(currentObject)) {
            DefaultMutableTreeNode mNode = new DefaultMutableTreeNode(currentObject);
            childMap.put(currentObject.getGlobalId(), mNode);
            rootNode.add(mNode);
        }
        for (OTObject mUser : multiUserObjects) {
            DefaultMutableTreeNode mNode = new DefaultMutableTreeNode(mUser);
            childMap.put(mUser.getGlobalId(), mNode);
            rootNode.add(mNode);
        }
        
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

}
