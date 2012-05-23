package org.concord.otrunk.view;

import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.PeriodicUploadingEventListener;
import org.concord.otrunk.user.OTReferenceMap;

public class PeriodicUploadingStatusPanel extends JPanel implements PeriodicUploadingEventListener
{
    private static final long serialVersionUID = 1L;
	private OTrunkImpl otrunk;
	private JButton saveErrorButton;
	
	public void setOtrunk(OTrunkImpl otrunk) {
		this.otrunk = otrunk;
		
		init();
	}
	
	private void init() {
		if (OTConfig.isPeriodicUploadingUserDataEnabled() && OTConfig.getPeriodicUploadingUserDataUrl() != null) {
    		URL url = this.getClass().getResource("page_error.png");
    		ImageIcon saveErrorIcon = new ImageIcon(url);
    		saveErrorButton = new JButton(new ImageIcon(saveErrorIcon.getImage().getScaledInstance(20, -1, Image.SCALE_SMOOTH)));
    		saveErrorButton.addActionListener(new ActionListener(){
    			public void actionPerformed(ActionEvent arg0) {
    				otrunk.triggerPeriodicUploading();
    			}
    		});
    		
    		saveErrorButton.setOpaque(false);
    		saveErrorButton.setBorder(null);
    		saveErrorButton.setMargin(new Insets(0,0,0,0));
    		
    	    saveErrorButton.setVisible(false);
    	    saveErrorButton.setEnabled(false);
    	    
    	    saveErrorButton.setToolTipText("There was an error uploading your data! Make sure your computer is connected to the internet.");
    	    
    	    this.add(saveErrorButton);
    		
    		otrunk.addPeriodicUploadingEventListener(this);
		}
	}

	public void uploadFailed(OTReferenceMap refMap, Exception e) {
	    // TODO Add some tooltip text to explain what's happening?
	    
	    toggleButton(true);
    }

	public void uploadSucceeded(OTReferenceMap refMap) {
		toggleButton(false);
    }

	private void toggleButton(boolean enabled) {
	    saveErrorButton.setVisible(enabled);
	    saveErrorButton.setEnabled(enabled);
	    
	    saveErrorButton.revalidate();
	    saveErrorButton.repaint();
	}
}
