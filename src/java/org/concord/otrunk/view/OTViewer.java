/*
 * Last modification information:
 * $Revision: 1.10 $
 * $Date: 2005-04-01 19:15:42 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerListener;
import org.concord.framework.util.SimpleTreeNode;
import org.concord.otrunk.OTUserObject;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.xml.Exporter;
import org.concord.otrunk.xml.XMLDatabase;
import org.concord.view.SimpleTreeModel;
import org.concord.view.SwingUserMessageHandler;


/**
 * OTViewer
 * Class name and description
 *
 * Date created: Dec 14, 2004
 *
 * @author scott<p>
 *
 */
public class OTViewer extends JFrame
	implements TreeSelectionListener, OTFrameManager,
		OTViewContainerListener
{
	private static OTrunkImpl otrunk;
	private static OTViewFactory otViewFactory;
	
	OTUserObject currentUser = null;
	URL currentURL = null;
	
	OTViewContainerPanel bodyPanel;
	JTree folderTreeArea;
	SimpleTreeModel folderTreeModel;
	JTree dataTreeArea;
	SimpleTreeModel dataTreeModel;
	JSplitPane splitPane;
	
	//Temp, to close the window
	AbstractAction exitAction;
	AbstractAction saveAsAction;
	
	JMenuBar menuBar;  //  @jve:decl-index=0:visual-constraint="576,108"
	XMLDatabase xmlDB;
	File currentFile = null;
	
	Hashtable otContainers = new Hashtable();
	
	boolean showTree = false;
    private AbstractAction saveUserDataAsAction;
	
	public static void setOTViewFactory(OTViewFactory factory)
	{
		OTViewContainerPanel.setOTViewFactory(factory);
		otViewFactory = factory;
	}
		
	public OTViewer(boolean showTree)
	{
		super("OTrunk Viewer");
		this.showTree = showTree;
	}
	
	public void updateTreePane()
	{
		Dimension minimumSize = new Dimension(100, 50);
	    JComponent leftComponent = null;
        folderTreeArea = new JTree(folderTreeModel);
        folderTreeArea.setEditable(true);
        folderTreeArea.addTreeSelectionListener(this);
        
        JScrollPane folderTreeScrollPane = new JScrollPane(folderTreeArea);

        if(System.getProperty("otrunk.view.debug","").equals("true")){
	        //			ViewFactory.getComponent(root);
	        
	        dataTreeArea = new JTree(dataTreeModel);
	        dataTreeArea.setEditable(true);
	        dataTreeArea.addTreeSelectionListener(this);
	        
	        JScrollPane dataTreeScrollPane = new JScrollPane(dataTreeArea);
	        
	        JTabbedPane tabbedPane = new JTabbedPane();
	        tabbedPane.add("Folders", folderTreeScrollPane);
	        tabbedPane.add("Resources", dataTreeScrollPane);

			//	Provide minimum sizes for the two components in the split pane
			folderTreeScrollPane.setMinimumSize(minimumSize);
			dataTreeScrollPane.setMinimumSize(minimumSize);
			tabbedPane.setMinimumSize(minimumSize);			    

	        leftComponent = tabbedPane;
	    } else {
	        leftComponent = folderTreeScrollPane;
	    }
	    
		//	Create a split pane with the two scroll panes in it.
		if(splitPane == null){
		    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		            leftComponent, bodyPanel);
		} else {
		    splitPane.setLeftComponent(leftComponent);
		}
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);
		
	}
	
	public void init(String url)
	{
		initMenuBar();
				
		setJMenuBar(menuBar);

		bodyPanel = new OTViewContainerPanel(this, null);
		
		bodyPanel.addViewContainerListener(this);
		
		if(showTree) {
		
			dataTreeModel = new SimpleTreeModel();
			
			folderTreeModel = new SimpleTreeModel();		
			
			updateTreePane();
						
			getContentPane().add(splitPane);
		} else {
			getContentPane().add(bodyPanel);
		}
		
        setBounds(100, 100, 875, 600);

        setVisible(true);
        
        if(url != null) {
        	try {
        		loadURL(new URL(url));
        	} catch(Exception e) {
        		// FIXME: this should popup a dialog
        		System.err.println("Can't load url");
        		e.printStackTrace();
        		return;
        	}
        } 
    }

	private void loadFile(File file)
	{
		currentFile = file;
		try {
			loadURL(currentFile.toURL());
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void loadURL(URL url)
		throws Exception
	{
		xmlDB = new XMLDatabase(url);
		otrunk = new OTrunkImpl(xmlDB,
				new Object [] {new SwingUserMessageHandler(this)});
	
		OTObject root = otrunk.getRoot();
		if(currentUser != null) {
		    root = otrunk.getUserRuntimeObject(root, currentUser);			    
		}
		
		if(showTree) {
		    OTDataObject rootDataObject = otrunk.getRootDataObject();
			dataTreeModel.setRoot(new OTDataObjectNode("root", 
					otrunk.getRootDataObject(), otrunk));
			
			folderTreeModel.setRoot(new OTFolderNode(root));
		}
		
		bodyPanel.setCurrentObject(root, null);
		
		if(showTree) {
			folderTreeModel.fireTreeStructureChanged((SimpleTreeNode)folderTreeModel.getRoot());
			dataTreeModel.fireTreeStructureChanged((SimpleTreeNode)dataTreeModel.getRoot());
		}
		
		Frame frame = (Frame)SwingUtilities.getRoot(this);

		currentURL = url;
		frame.setTitle(url.toString());
	}
		
	public void reload()
		throws Exception
	{
	    loadURL(currentURL);
	}
	
	public OTUserObject createUser(String name)
		throws Exception
	{
	    OTUserObject user = (OTUserObject)otrunk.createObject(OTUserObject.class); 
	    user.setName(name);
	    return user;
	}

	
	public void setCurrentUser(OTUserObject userObject)
	{
	    OTUserObject oldUser = currentUser;
	    currentUser = userObject;
	    if(!currentUser.equals(oldUser)) {
	        try {
	    		OTObject root = otrunk.getRoot();
	    		if(currentUser != null) {
	    		    root = otrunk.getUserRuntimeObject(root, currentUser);			    
	    		}
	    		
	    		if(showTree) {
	    			folderTreeModel.setRoot(new OTFolderNode(root));
	    		}
	    		
	    		bodyPanel.setCurrentObject(root, null);

	    		if(showTree) {
	    			folderTreeModel.fireTreeStructureChanged((SimpleTreeNode)folderTreeModel.getRoot());
	    			dataTreeModel.fireTreeStructureChanged((SimpleTreeNode)dataTreeModel.getRoot());
	    		}	    		
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	}
	
	public static void main(String [] args)
	{
		OTViewer viewer = new OTViewer(true);
		if(args.length > 0) {
			viewer.init(args[0]);
		} else {
			viewer.init(null);
		}
		
		viewer.addWindowListener( new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e)
			{
				((OTViewer)e.getSource()).exitAction.actionPerformed(null);
			}			
		});
		
		
	}
	
	class ExitAction extends AbstractAction
	{
		public ExitAction()
		{
			super("Exit");			
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			try {
				otrunk.close();
			} catch (Exception exp) {
				exp.printStackTrace();
				// exit anyhow 
			}
			System.exit(0);
	    }
	}

	/* (non-Javadoc)
     * @see org.concord.otrunk.view.OTViewContainerListener#currentObjectChanged(org.concord.framework.otrunk.view.OTViewContainer)
     */
    public void currentObjectChanged(OTViewContainer container)
    {
        // TODO Auto-generated method stub
        OTObject currentObject = container.getCurrentObject();
        if(folderTreeArea != null) {
			OTFolderNode node = (OTFolderNode)folderTreeArea.getLastSelectedPathComponent();
			if(node == null) return;
            if(node.getPfObject() != currentObject) {
                folderTreeArea.setSelectionPath(null);
            }
        }
    }
	
	/* (non-Javadoc)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent event) 
	{	
		if (event.getSource() == folderTreeArea) {
			OTFolderNode node = (OTFolderNode)
			folderTreeArea.getLastSelectedPathComponent();

			if (node == null) return;

			OTObject pfObject = node.getPfObject();

			bodyPanel.setCurrentObject(pfObject, null);
			
			if(splitPane.getRightComponent() != bodyPanel){
			    splitPane.setRightComponent(bodyPanel);
			}
		} else if (event.getSource() == dataTreeArea) {
			SimpleTreeNode node = (SimpleTreeNode)
				dataTreeArea.getLastSelectedPathComponent();
			Object resourceValue = null;
			if(node != null) {
				resourceValue = node.getObject();				
				if(resourceValue == null) {
					resourceValue = "null resource value";
				}
			} else {
				resourceValue = "no selected data object";
			}
			
			JTextArea textArea = new JTextArea(resourceValue.toString());
			
			splitPane.setRightComponent(textArea);
		}
	}
	
	public void setFrameObject(OTObject otObject, OTFrame otFrame)
	{
		// look up view container with the frame.
		OTViewContainerPanel otContainer = (OTViewContainerPanel)otContainers.get(otFrame.getGlobalId());
		
		if(otContainer == null) {

			JFrame jFrame = new JFrame(otFrame.getTitle());

			otContainer = new OTViewContainerPanel(this, jFrame);

			jFrame.getContentPane().setLayout(new BorderLayout());

			jFrame.getContentPane().add(otContainer, BorderLayout.CENTER);
			jFrame.setSize(otFrame.getWidth(), otFrame.getHeight());
			
			otContainers.put(otFrame.getGlobalId(), otContainer);
		}
		
		// call setCurrentObject on that view container with a null
		// frame
		otContainer.setCurrentObject(otObject, null);
		otContainer.showFrame();
	}
	
	/**
	 * @return Returns the menuBar.
	 */
	public JMenuBar initMenuBar()
	{
		/////////////////////////////////////////////////
		if (menuBar == null){
			menuBar = new JMenuBar();

			JMenu menu = new JMenu("File");
			
			AbstractAction loadAction = new AbstractAction(){
				
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent arg0)
				{
					Frame frame = (Frame)SwingUtilities.getRoot(OTViewer.this);

					FileDialog dialog = new FileDialog(frame, "Open", FileDialog.LOAD);
					if(currentFile != null) {
						dialog.setDirectory(currentFile.getParentFile().getAbsolutePath());
						dialog.setFile(currentFile.getName());
					}
					dialog.show();
					
					String fileName = dialog.getFile();
					if(fileName == null) {
						return;
					}
					
					fileName = dialog.getDirectory() + fileName;
					System.out.println("load file name: " + fileName);
					loadFile(new File(fileName));					
				}
				
			};
			loadAction.putValue(Action.NAME, "Open...");			
			menu.add(loadAction);

			AbstractAction saveAction = new AbstractAction(){
				
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent arg0)
				{
					if(currentFile == null){
						saveAsAction.actionPerformed(arg0);
						return;
					}
						
					if(!currentFile.exists() || checkForReplace(currentFile)){
						try {
							Exporter.export(currentFile, xmlDB.getRoot(), xmlDB);
						} catch(Exception e){
							e.printStackTrace();
						}	                    	
					}
				}

			};
			saveAction.putValue(Action.NAME, "Save");			
			saveAction.setEnabled(false);
			menu.add(saveAction);

			saveAsAction = new AbstractAction(){
				
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent arg0)
				{
					Frame frame = (Frame)SwingUtilities.getRoot(OTViewer.this);
					FileDialog dialog = new FileDialog(frame, "Save As", FileDialog.SAVE);
					if(currentFile != null) {
						dialog.setDirectory(currentFile.getParentFile().getAbsolutePath());
						dialog.setFile(currentFile.getName());
					}
					dialog.show();
						
					String fileName = dialog.getFile();
					if(fileName == null) {
						return;
					}

					fileName = dialog.getDirectory() + fileName;
					currentFile = new File(fileName);

					if(!fileName.toLowerCase().endsWith(".otml")){
						currentFile = new File(currentFile.getAbsolutePath()+".otml");
					}
					if(!currentFile.exists() || checkForReplace(currentFile)){
						try {
							Exporter.export(currentFile, xmlDB.getRoot(), xmlDB);
						} catch(Exception e){
							e.printStackTrace();
						}	                    	
					}
				
					frame.setTitle(fileName);
				}
			};
			saveAsAction.putValue(Action.NAME, "Save As...");			
			// saveAsAction.setEnabled(false);
			menu.add(saveAsAction);

			saveUserDataAsAction = new AbstractAction(){
				
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent arg0)
				{
					Frame frame = (Frame)SwingUtilities.getRoot(OTViewer.this);
					FileDialog dialog = new FileDialog(frame, "Save As", FileDialog.SAVE);
					if(currentFile != null) {
						dialog.setDirectory(currentFile.getParentFile().getAbsolutePath());
						dialog.setFile(currentFile.getName());
					}
					dialog.show();
						
					String fileName = dialog.getFile();
					if(fileName == null) {
						return;
					}

					fileName = dialog.getDirectory() + fileName;
					currentFile = new File(fileName);

					if(!fileName.toLowerCase().endsWith(".otml")){
						currentFile = new File(currentFile.getAbsolutePath()+".otml");
					}
					if(!currentFile.exists() || checkForReplace(currentFile)){
						try {
						    
						    OTDataObject userDataObject = otrunk.getOTDataObject(null, currentUser.getGlobalId());
							Exporter.export(currentFile, userDataObject, xmlDB);
						} catch(Exception e){
							e.printStackTrace();
						}	                    	
					}				
				}
			};
			saveUserDataAsAction.putValue(Action.NAME, "Save User Data As...");			
			// saveAsAction.setEnabled(false);
			menu.add(saveUserDataAsAction);

			JCheckBoxMenuItem debugItem = new JCheckBoxMenuItem("Debug Mode");
			debugItem.addActionListener(new ActionListener(){
			   public void actionPerformed(ActionEvent e)
			   {
			       Object source = e.getSource();
			       if(((JCheckBoxMenuItem)source).isSelected()){
			           System.setProperty("otrunk.view.debug","true");
			       } else {
			           System.setProperty("otrunk.view.debug","false");
			       }
			       
			       updateTreePane();			       
			   }
			});
			menu.add(debugItem);
			
			JMenuItem menuItem;
			
			exitAction = new ExitAction();
			menu.add(exitAction);
			
			menuBar.add(menu);			
		}
		/////////////////////////////////////////////////
		return menuBar;
	}

	boolean checkForReplace(File file){
        if(file == null || !file.exists()) return false;
        final Object[] options = { "Yes", "No" };
        return javax.swing.JOptionPane.showOptionDialog(null,
                  "The file '" + file.getName() +
                  "' already exists.  " +
                  "Replace existing file?",
                  "Warning",
                  javax.swing.JOptionPane.YES_NO_OPTION,
                  javax.swing.JOptionPane.WARNING_MESSAGE,
                  null,
                  options,
                  options[1]) == javax.swing.JOptionPane.YES_OPTION;

    }				
	
	public void exit()
	{
		exitAction.actionPerformed(null);
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
