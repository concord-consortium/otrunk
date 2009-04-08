package org.concord.otrunk.overlay;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.datamodel.OTTransientMapID;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.view.AbstractOTJComponentContainerView;
import org.concord.otrunk.view.OTGroupListManager;

public class OTOverlayWrapperView extends AbstractOTJComponentContainerView
{
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private OTOverlay overlay;
	private OTObject wrappedObject;
	private JComponent subview;
	private OTOverlayWrapper wrapper;
	private OTGroupListManager groupListManager;
	private OTUserOverlayManager overlayManager;
	private JButton submitButton;
	private GridBagConstraints noStretchConstraints;
	private GridBagConstraints stretchConstraints;
	private OTObject resultsObject;
	private JFrame resultsFrame;
	
	public JComponent getComponent(OTObject otObject)
	{
		// FIXME This should be changed so that the user data is stored in the user session layer,
		// and only when the overlay data is saved remotely should it get copied into the overlay
		wrapper = (OTOverlayWrapper) otObject;
		overlay = wrapper.getOverlay();
		groupListManager = wrapper.getOTObjectService().getOTrunkService(OTGroupListManager.class);
		overlayManager = wrapper.getOTObjectService().getOTrunkService(OTUserOverlayManager.class);
		wrappedObject = wrapper.getWrappedObject();
		
		/* String msg = "\nwrapped object is: " + wrappedObject;
		msg += "\noverlay object is: " + overlay;
		msg += "\ngroup list manager is: " + groupListManager;
		msg += "\noverlay manager is: " + overlayManager;
		logger.info(msg);
		*/
		if (overlay == null && groupListManager != null && overlayManager != null) {
			// logger.info("Using group list manager/overlay manager");
			OTUserObject currentGroupMember = groupListManager.getCurrentGroupMember().getUserObject();
			overlay = overlayManager.getOverlay(currentGroupMember);
		}

		subview = createSubViewComponent(wrappedObject);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		noStretchConstraints = new GridBagConstraints();
		noStretchConstraints.anchor = GridBagConstraints.NORTHWEST;
		noStretchConstraints.weightx = 0;
		noStretchConstraints.weighty = 0;
		noStretchConstraints.fill = GridBagConstraints.NONE;
		noStretchConstraints.gridwidth = GridBagConstraints.REMAINDER;
		noStretchConstraints.ipady = 5;
		
		stretchConstraints = new GridBagConstraints();
		stretchConstraints.anchor = GridBagConstraints.NORTHWEST;
		stretchConstraints.weightx = 1;
		stretchConstraints.weighty = 0;
		stretchConstraints.fill = GridBagConstraints.HORIZONTAL;
		stretchConstraints.gridwidth = GridBagConstraints.REMAINDER;
		stretchConstraints.ipady = 5;
		
		mainPanel.add(subview, stretchConstraints);
		
		if (wrapper.getShowButton()) {
			submitButton = new JButton(wrapper.getButtonText());
			mainPanel.add(submitButton, noStretchConstraints);
			
			submitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
                {
	                saveData();
                }
			});
		}
		
		resultsObject = wrapper.getResultsObject();
		if (resultsObject != null) {
    		JButton resultsButton = new JButton("Results");
    		mainPanel.add(resultsButton, noStretchConstraints);
    		
    		resultsButton.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e)
                {
                    popUpResults();
                }
    		});
		}
		
		return mainPanel;
	}
	
	private void popUpResults() {
		final JComponent resultsSubview = createSubViewComponent(resultsObject);
		resultsSubview.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) { }
			public void componentShown(ComponentEvent e) { }
			public void componentMoved(ComponentEvent e) { }

			public void componentResized(ComponentEvent e)
            {
				resultsSubview.removeComponentListener(this);
				resultsFrame.pack();
            }
		});
		if (resultsFrame == null) {
    		resultsFrame = new JFrame("Results");
    		resultsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		}
		resultsFrame.getContentPane().removeAll();
		resultsFrame.getContentPane().add(resultsSubview);
		resultsFrame.pack();
		resultsFrame.setVisible(true);
	}
	
	@Override
    public void viewClosed() {
		// logger.info("Closing the wrapped object's views");
		removeAllSubViews();
		if (wrapper.getAutoSubmit()) {
			saveData();
		}
		super.viewClosed();
	}
	
	private void saveData() {
		// save everything
		if (overlayManager != null && overlay != null) {
			// logger.info("had an overlay");
    		try {
    			OTID id = wrappedObject.getGlobalId();
    			if (id instanceof OTTransientMapID) {
    				id = ((OTTransientMapID) id).getMappedId();
    			}
    	        OTObject newWrappedObject = overlayManager.getOTObject(overlay, id);
    	        ((OTObjectServiceImpl) wrappedObject.getOTObjectService()).copyInto(wrappedObject, newWrappedObject, -1, true);
    	        
    	        overlayManager.remoteSave(overlay);
            } catch (Exception e) {
    	        logger.log(Level.SEVERE, "Couldn't get the object from the overlay!", e);
    	        logger.log(Level.FINER, "stack trace:", e);
            }
            try {
    	        overlayManager.remoteSave(overlay);
            } catch (Exception e) {
            	logger.log(Level.SEVERE, "Couldn't save the user's overlay!", e);
            }
		}
	}

}
