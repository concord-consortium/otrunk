package org.concord.otrunk.overlay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.otrunk.datamodel.OTTransientMapID;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.view.AbstractOTJComponentContainerView;
import org.concord.otrunk.view.OTClassListManager;

public class OTOverlayWrapperView extends AbstractOTJComponentContainerView
{
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private OTOverlay overlay;
	private OTObject wrappedObject;
	private JComponent subview;
	private OTOverlayWrapper wrapper;
	private OTClassListManager classListManager;
	private OTUserOverlayManager overlayManager;
	private JButton submitButton;
	
	public JComponent getComponent(OTObject otObject)
	{
		// logger.info("Initializing overlay wrapper");
		// FIXME Changes to the object will ONLY be save in the overlay! We should probably mirror
		// the changes to the current user's session overlay, too!
		// logger.info("Initializing");
		wrapper = (OTOverlayWrapper) otObject;
		overlay = wrapper.getOverlay();
		classListManager = (OTClassListManager) wrapper.getOTObjectService().getOTrunkService(OTClassListManager.class);
		overlayManager = (OTUserOverlayManager) wrapper.getOTObjectService().getOTrunkService(OTUserOverlayManager.class);
		wrappedObject = wrapper.getWrappedObject();
		
		/* String msg = "\nwrapped object is: " + wrappedObject;
		msg += "\noverlay object is: " + overlay;
		msg += "\nclass list manager is: " + classListManager;
		msg += "\noverlay manager is: " + overlayManager;
		logger.info(msg);
		*/
		if (overlay == null && classListManager != null && overlayManager != null) {
			// logger.info("Using class list manager/overlay manager");
			OTUserObject currentClassMember = classListManager.getCurrentClassMemberUserObject();
			overlay = overlayManager.getOverlay(currentClassMember);
		}

		// logger.info("overlay object is: " + overlay);
		
		if (overlay != null) {
			// logger.info("had an overlay");
    		try {
    			OTID id = wrappedObject.getGlobalId();
    			if (id instanceof OTTransientMapID) {
    				id = ((OTTransientMapID) id).getMappedId();
    			}
    	        wrappedObject = overlayManager.getOTObject(overlay, id);
    	        
            } catch (Exception e) {
    	        logger.log(Level.SEVERE, "Couldn't get the object from the overlay!", e);
            }
		}
		
		// logger.info("wrapped object is: " + wrappedObject);
		subview = createSubViewComponent(wrappedObject);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		mainPanel.add(subview);
		
		if (wrapper.getShowButton()) {
			submitButton = new JButton(wrapper.getButtonText());
			mainPanel.add(submitButton);
			
			submitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
                {
	                saveData();
                }
			});
		}
		
		return mainPanel;
	}
	
	public void viewClosed() {
		// logger.info("Closing the wrapped object's views");
		removeAllSubViews();
		saveData();
		super.viewClosed();
	}
	
	private void saveData() {
		// save everything
		try {
	        overlayManager.remoteSave(overlay);
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Couldn't save the user's overlay!", e);
        }
	}

}
