package org.concord.otrunk.overlay;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTrunk;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.view.AbstractOTJComponentContainerView;
import org.concord.swing.MostRecentFileDialog;

public class OTOverlaySwitcherAuthorView extends AbstractOTJComponentContainerView
{
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private static final int SAVE = 0;
	private static final int OPEN = 1;
	private OTOverlaySwitcher otSwitcher;
	private OTObject activityRoot;
	private HashMap<String, OTUserObject> overlayToUserObjectMap = 
		new HashMap<String, OTUserObject>();
	private JTabbedPane tabbedPane;
	private OTrunkImpl otrunk;
	private OTObjectServiceImpl rootObjectService;
	private JPanel mainPanel;
	private OTUserOverlayManager overlayManager;

	public JComponent getComponent(OTObject otObject)
	{
		otSwitcher = (OTOverlaySwitcher) otObject;
		activityRoot = otSwitcher.getActivityRoot();
		rootObjectService = (OTObjectServiceImpl) otSwitcher.getOTObjectService();
		otrunk = (OTrunkImpl) rootObjectService.getOTrunkService(OTrunk.class);
		
		overlayManager = viewContext.getViewService(OTUserOverlayManager.class);
		if (overlayManager == null) {
			overlayManager = new OTUserSingleOverlayManager(otrunk);
			viewContext.addViewService(OTUserOverlayManager.class, overlayManager);
		}
		
		// create a tabbed pane, one tab per overlay plus a main tab for the root authored content
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Root", createOverlaySubView(null));

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		// mainPanel.add(menubar, BorderLayout.NORTH);
		mainPanel.add(tabbedPane, BorderLayout.CENTER);
		
		// set up menu items to add, remove and save overlays
		mainPanel.addComponentListener(new ComponentListener() {
			boolean hasRun = false;

			public void componentHidden(ComponentEvent e) { }
			public void componentMoved(ComponentEvent e) { }
			public void componentShown(ComponentEvent e) { }
			public void componentResized(ComponentEvent e)
            {
	            if (! hasRun) {
	            	createMenuBar();
	            	hasRun = true;
	            }
            }
		});
		
		return mainPanel;
	}
	
	private void registerOverlay(String title, OTUserObject userObj) {
		overlayToUserObjectMap.put(title, userObj);
	}
	
	private JComponent createOverlaySubView(OTUserObject user) {
		try {
			// get overlay version of the authoredRoot object
	        OTObject overlayObject = overlayManager.getOTObject(user, activityRoot);
       
	        // get subview component
	        JComponent subComponent = createSubViewComponent(overlayObject);
	        return subComponent;
		} catch (Exception e) {
		    e.printStackTrace();
	    }
		return null;
	}
	
	private OTUserObject getCurrentUserObject() {
		String title = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
		return overlayToUserObjectMap.get(title);
	}
	
	private void removeCurrentOverlay() {
		tabbedPane.remove(tabbedPane.getSelectedComponent());
	}
	
	private String askForName() {
		String name = JOptionPane.showInputDialog(mainPanel, "Please enter a name for this overlay:", "Name your overlay", JOptionPane.QUESTION_MESSAGE);
		name = verifyNameAvailability(name);
		return name;
	}
	
	private void addOverlay(File f) throws Exception {
    	String name = askForName();
		if (name == null || name.length() == 0) {
			name = verifyNameAvailability("Untitled");
		}
		
		OTUserObject userObj = rootObjectService.createObject(OTUserObject.class);
		
		overlayManager.addWriteable(f.toURI().toURL(), userObj, false);
		
		userObj.setName(name);
		
		Component comp = createOverlaySubView(userObj);
		tabbedPane.addTab(name, comp);
		tabbedPane.setSelectedComponent(comp);
		registerOverlay(name, userObj);
	}
	
	private String verifyNameAvailability(String name)
    {
		String origName = name;
		int i = 1;
	    while (overlayToUserObjectMap.containsKey(name)) {
	    	name = origName + " (" + i++ + ")";
	    }
	    return name;
    }

	private void newOverlay() {
		logger.info("New overlay");
        try {
			File f = askForFile(SAVE);
			if (f != null) {
				addOverlay(f);
			}
        } catch (Exception e) {
	        e.printStackTrace();
        }
	}
	
	private void openOverlay() {
		try {
			File f = askForFile(OPEN);
			if (f != null) {
				addOverlay(f);
			}
        } catch (Exception e) {
	        e.printStackTrace();
        }
	}
	
	private void saveCurrentOverlay() {
		OTUserObject currentUser = getCurrentUserObject();
		if (currentUser == null) { return; }
		
		logger.info("Save overlay");
		try {
	        overlayManager.remoteSave(currentUser, activityRoot);
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}
	
	private File askForFile(int style) {
		MostRecentFileDialog mrfd =
		    new MostRecentFileDialog("org.concord.otviewer.openotml");
		mrfd.setFilenameFilter("otml");

		int retval = MostRecentFileDialog.CANCEL_OPTION;
		if (style == OPEN) {
			retval = mrfd.showOpenDialog(mainPanel);
		} else if (style == SAVE) {
			retval = mrfd.showSaveDialog(mainPanel);
		} else {
			return null;
		}
		
		if (retval == MostRecentFileDialog.APPROVE_OPTION) {
			try {
				// set the context url
				File f = mrfd.getSelectedFile();
				if (! f.getName().endsWith(".otml")) {
					f = new File(f.getCanonicalPath() + ".otml");
				}
                return f;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private void createMenuBar() {
		// JMenuBar menuBar = new JMenuBar();
		JMenuBar menuBar = mainPanel.getRootPane().getJMenuBar();
		
		JMenu overlayMenu = new JMenu("Overlays");
		menuBar.add(overlayMenu);
		
		Action newMenuItem = new AbstractAction("New Overlay") {
            public void actionPerformed(ActionEvent e)
            {
	            newOverlay();
            }
		};
		Action openMenuItem = new AbstractAction("Open Existing Overlay") {
			public void actionPerformed(ActionEvent e)
            {
	            openOverlay();
            }
		};
		Action saveMenuItem = new AbstractAction("Save Current Overlay") {
			public void actionPerformed(ActionEvent e)
            {
	            saveCurrentOverlay();
            }
		};
		Action deleteMenuItem = new AbstractAction("Delete Current Overlay") {
			public void actionPerformed(ActionEvent e)
            {
				removeCurrentOverlay();
            }
		};
		
		overlayMenu.add(newMenuItem);
		overlayMenu.add(openMenuItem);
		overlayMenu.add(saveMenuItem);
		overlayMenu.add(deleteMenuItem);
	}
}
