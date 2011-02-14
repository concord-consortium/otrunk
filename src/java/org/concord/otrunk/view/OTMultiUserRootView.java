/*
 *  Copyright (C) 2008  The Concord Consortium, Inc.,
 *  25 Love Lane, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */


package org.concord.otrunk.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.AbstractOTView;
import org.concord.framework.otrunk.view.OTXHTMLView;
import org.concord.otrunk.OTIncludeRootObject;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.overlay.OTUserOverlayManager;
import org.concord.otrunk.overlay.OTUserOverlayManagerFactory;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.util.MultiThreadedProcessingException;
import org.concord.otrunk.util.MultiThreadedProcessor;
import org.concord.otrunk.util.MultiThreadedProcessorRunnable;


public class OTMultiUserRootView extends AbstractOTView implements OTXHTMLView 
{
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private boolean firstRun = true;
	private OTrunkImpl otrunk;
	OTUserOverlayManager overlayManager;
	private OTMultiUserRoot root;
	private OTObject reportTemplate;
	
	private void init(OTObject otObject) {
		root = (OTMultiUserRoot) otObject;
	    otrunk = (OTrunkImpl) getViewService(OTrunk.class);
	    
		if (firstRun) { //why is this method called twice?
			loadGlobalOverlay(root);
			loadUserDatabases(root);
			viewContext.addViewService(OTUserOverlayManager.class, overlayManager);
			firstRun = false;
		}
	    reportTemplate = root.getReportTemplate();
	}
	
/*	public JComponent getComponent(OTObject otObject) {
		logger.info("Running in JComponent mode");
		init(otObject);
		JComponent c = null;
		c = createSubViewComponent(root.getReportTemplate(), false, (OTViewEntry) root.getReportTemplateViewEntry());
		return c;
	}
*/
	public String getXHTMLText(OTObject otObject) {
		logger.info("Running in XHTML mode");
		init(otObject);
		if (reportTemplate instanceof OTIncludeRootObject) {
			reportTemplate = ((OTIncludeRootObject)reportTemplate).getReference();
		}
	    String result = "<object refid=\"" + reportTemplate.otExternalId() + "\" ";
	    if(root.getReportTemplateViewEntry() != null){
	    	result += "viewid=\"" + root.getReportTemplateViewEntry().otExternalId() + "\" ";
	    }
	    result += "/>";
	    return result;
    }
	
	protected void loadUserDatabases(final OTMultiUserRoot root) {
		long start = System.currentTimeMillis();

		OTUserList userList = null;
		OTObject userListOrig = root.getUserList();
		if(userListOrig instanceof OTIncludeRootObject){
			userList = (OTUserList) ((OTIncludeRootObject)userListOrig).getReference();
		} else {
			userList = (OTUserList) userListOrig;
		}

        System.out.println("user list load time: " + 
        	(System.currentTimeMillis() - start) + "ms");        

	    final ArrayList<OTUserDatabaseRef> userDatabases =  new ArrayList<OTUserDatabaseRef>();
	    OTObjectList dbs = userList.getUserDatabases();
	    for (OTObject db : dbs) {
	    	userDatabases.add((OTUserDatabaseRef)db);
	    }
	    
		// use 3 threads to speed things up
		MultiThreadedProcessorRunnable<OTUserDatabaseRef> objectLoadingTask = new MultiThreadedProcessorRunnable<OTUserDatabaseRef>(){
			public void process(OTUserDatabaseRef ref) {
				URL url = ref.getUrl();
				URL overlayURL = ref.getOverlayURL();	    	
				OTUserObject userObject = null;
				try {
					// set up the user session and register it
					OTMLUserSession userSession;
					if (url != null) {
						userSession = new OTMLUserSession(url, null);
					} else {
						userSession = new OTMLUserSession();
					}
					otrunk.registerUserSession(userSession);
					userObject = userSession.getUserObject();
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, "Couldn't initialize user session", e);
				}

				// set up the overlay, if it exists
				if (overlayURL != null && userObject != null) {
					try {
	    			  	synchronized (OTMultiUserRootView.this){
	    			  		if (overlayManager == null) {
								overlayManager = OTUserOverlayManagerFactory.getUserOverlayManager(overlayURL, otrunk);
							}
	        				// map the object service/overlay to the user
	    			  		overlayManager.addReadOnly(overlayURL, userObject, false);
	    			  	}
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Couldn't initialize user overlay", e);
					}
				}           
            }
	    };

	    MultiThreadedProcessor<OTUserDatabaseRef> processor = new MultiThreadedProcessor<OTUserDatabaseRef>(userDatabases, 3, objectLoadingTask);
	    try {
	        processor.process();
        } catch (MultiThreadedProcessingException e) {
	        System.err.println("Error processing user database list - " + e.getExceptions().size() + " exceptions:" + e.getMessage());
	        e.printStackTrace();
	        for (Exception ex : e.getExceptions()) {
	        	System.err.println("  Exception: " + ex.getMessage());
	        	ex.printStackTrace();
	        }
        }    
        
        System.out.println("total user db and overlay load time: " + 
        	(System.currentTimeMillis() - start) + "ms");        
	}
	
	protected void loadGlobalOverlay(OTMultiUserRoot root) {
		try {
			if(root.getGroupOverlayURL() == null){
				return;
			}
			overlayManager = OTUserOverlayManagerFactory.getUserOverlayManager(root.getGroupOverlayURL(), otrunk);
			long start = System.currentTimeMillis();
			overlayManager.addReadOnly(root.getGroupOverlayURL(), null, true);
	        System.out.println("group overlay load time: " + 
	        	(System.currentTimeMillis() - start) + "ms");
		} catch (Exception e) {
	        logger.log(Level.WARNING, "Couldn't set up the group-wide overlay", e);
        }
	}

	public boolean getEmbedXHTMLView(OTObject otObject)
    {
	    return true;
    }
}
