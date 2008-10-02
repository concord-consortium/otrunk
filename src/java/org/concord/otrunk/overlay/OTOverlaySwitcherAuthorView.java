package org.concord.otrunk.overlay;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTrunk;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.view.AbstractOTJComponentContainerView;
import org.concord.otrunk.view.OTViewer;
import org.concord.otrunk.xml.XMLDatabase;
import org.concord.swing.MostRecentFileDialog;

public class OTOverlaySwitcherAuthorView extends AbstractOTJComponentContainerView
{

	private OTOverlaySwitcher otSwitcher;
	private OTObject activityRoot;
	private HashMap overlayToUserObjectMap = new HashMap();
	private JTabbedPane tabbedPane;
	private OTrunkImpl otrunk;
	private OTObjectServiceImpl rootObjectService;
	private JPanel mainPanel;
	private OTUserOverlayManager overlayManager;
	private OTObjectList overlayList;

	public JComponent getComponent(OTObject otObject)
	{
		otSwitcher = (OTOverlaySwitcher) otObject;
		activityRoot = otSwitcher.getActivityRoot();
		overlayList = otSwitcher.getOverlays();
		rootObjectService = (OTObjectServiceImpl) otSwitcher.getOTObjectService();
		otrunk = (OTrunkImpl) rootObjectService.getOTrunkService(OTrunk.class);
		
		overlayManager = (OTUserOverlayManager) viewContext.getViewService(OTUserOverlayManager.class);
		if (overlayManager == null) {
			overlayManager = new OTUserOverlayManager();
			viewContext.addViewService(OTUserOverlayManager.class, overlayManager);
		}

        try {
        	OTUserObject rootUserObject = (OTUserObject) rootObjectService.createObject(OTUserObject.class);
	        overlayManager.add(null, rootObjectService, rootUserObject);
	        registerOverlay("Root", rootUserObject);
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		
		// create a tabbed pane, one tab per overlay plus a main tab for the root authored content
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Root", createOverlaySubView(null));

		// set up menu items to add, remove and save overlays
		JMenuBar menubar = createMenuBar();

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(menubar, BorderLayout.NORTH);
		mainPanel.add(tabbedPane, BorderLayout.CENTER);
		return mainPanel;
	}
	
	private void registerOverlay(String title, OTUserObject userObj) {
		overlayToUserObjectMap.put(title, userObj);
	}
	
	private JComponent createOverlaySubView(OTOverlay overlay) {
		try {
			// get overlay version of the authoredRoot object
			OTObjectService objService = overlayManager.getObjectService(overlay);
	        OTObject overlayObject = objService.getOTObject(activityRoot.getGlobalId());
       
	        // get subview component
	        JComponent subComponent = createSubViewComponent(overlayObject);
	        return subComponent;
		} catch (Exception e) {
		    e.printStackTrace();
	    }
		return null;
	}
	
	private OTOverlay getCurrentOverlay() {
		String title = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
		OTUserObject userObj = (OTUserObject) overlayToUserObjectMap.get(title);
		return overlayManager.getOverlay(userObj);
	}
	
	private void removeCurrentOverlay() {
		tabbedPane.remove(tabbedPane.getSelectedComponent());
	}
	
	private void addOverlay() {
        try {
        	OTOverlay newOverlay = null;
        	 // open up a file save dialog
			MostRecentFileDialog mrfd =
			    new MostRecentFileDialog("org.concord.otviewer.openotml");
			mrfd.setFilenameFilter("otml");

			int retval = mrfd.showSaveDialog(mainPanel);
			
			if (retval == MostRecentFileDialog.APPROVE_OPTION) {
				try {
					// set the context url
	                File f = mrfd.getSelectedFile();
	                if (f.exists()) {
	                	newOverlay = (OTOverlay) otrunk.getExternalObject(f.toURL(), rootObjectService);
	                }
                	if (newOverlay == null) {
    					// create a blank one
    					XMLDatabase xmldb = new XMLDatabase();
    					OTObjectService newObjectService = otrunk.createObjectService(xmldb);
    					newOverlay = (OTOverlay) newObjectService.createObject(OTOverlay.class);
    					xmldb.setRoot(newOverlay.getGlobalId());
    					otrunk.localSaveData(xmldb, f);
    					newOverlay = (OTOverlay) otrunk.getExternalObject(f.toURL(), rootObjectService);
    				}
                } catch (Exception e) {
	                e.printStackTrace();
                }
			}
        	
        	overlayList.add(newOverlay);
        	if (newOverlay.getName() == null || newOverlay.getName().length() == 0) {
        		newOverlay.setName(JOptionPane.showInputDialog(mainPanel, "Please enter a name for this overlay:", "Name your overlay", JOptionPane.QUESTION_MESSAGE));
        	}
        	
        	// create object service
    		OTObjectServiceImpl objService = (OTObjectServiceImpl) otrunk.createObjectService(newOverlay);
    		
    		OTUserObject userObj = (OTUserObject) rootObjectService.createObject(OTUserObject.class);
    		userObj.setName(newOverlay.getName());
    		
    		System.err.println("Adding overlay");
    		System.err.println("overlay: " + newOverlay.getGlobalId());
    		System.err.println("objServ: " + objService.getCreationDb().getDatabaseId());
    		System.err.println("userObj: " + userObj.getGlobalId());
    		overlayManager.add(newOverlay, objService, userObj);
    		
    		Component comp = createOverlaySubView(newOverlay);
    		tabbedPane.addTab(newOverlay.getName(), comp);
    		tabbedPane.setSelectedComponent(comp);
    		registerOverlay(newOverlay.getName(), userObj);
        } catch (Exception e) {
	        e.printStackTrace();
        }
	}
	
	private void openOverlay() {
		// FIXME
		System.err.println("Open overlay");
	}
	
	private XMLDatabase getDbForOverlay(OTOverlay overlay) {
		OTDatabase db = overlayManager.getDatabase(overlay);
		if (db instanceof XMLDatabase) {
			return (XMLDatabase) db;
		} else if (db instanceof CompositeDatabase) {
			return (XMLDatabase) ((CompositeDatabase) db).getActiveOverlayDb();
		}
		return null;
	}
	
	private void saveCurrentOverlay() {
		OTOverlay overlay = getCurrentOverlay();
		if (overlay == null) { return; }
		
		System.err.println("Save overlay");
		XMLDatabase db = getDbForOverlay(overlay);
		// if database doesn't have a context url
		URL contextURL = db.getContextURL();
		System.err.println("Context url is: " + contextURL.toExternalForm());
		// dump the db to the context url
	    try {
	    	otrunk.localSaveData(db, contextURL);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu overlayMenu = new JMenu("Overlays");
		menuBar.add(overlayMenu);
		
		Action addMenuItem = new AbstractAction("Add Overlay") {
			public void actionPerformed(ActionEvent e)
            {
	            addOverlay();
            }
		};
		Action removeMenuItem = new AbstractAction("Remove Overlay") {
			public void actionPerformed(ActionEvent e)
            {
				removeCurrentOverlay();
            }
		};
		Action saveMenuItem = new AbstractAction("Save Overlay") {
			public void actionPerformed(ActionEvent e)
            {
	            saveCurrentOverlay();
            }
		};
		
		overlayMenu.add(addMenuItem);
		overlayMenu.add(saveMenuItem);
		overlayMenu.add(removeMenuItem);
		
		return menuBar;
	}
}
