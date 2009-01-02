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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.AbstractOTView;
import org.concord.framework.otrunk.view.OTXHTMLView;
import org.concord.otrunk.OTIncludeRootObject;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.overlay.CompositeDatabase;
import org.concord.otrunk.overlay.OTOverlay;
import org.concord.otrunk.overlay.OTUserOverlayManager;
import org.concord.otrunk.overlay.Overlay;
import org.concord.otrunk.overlay.OverlayImpl;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.xml.XMLDatabase;


public class OTMultiUserRootView extends AbstractOTView implements OTXHTMLView 
{
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private boolean firstRun = true;
	private OTrunkImpl otrunk;
	OTUserOverlayManager overlayManager;
	private Overlay globalOverlay;
	
	public String getXHTMLText(OTObject otObject) {
		//System.out.println("ENTER: OTMultiUserRootView#getXHTMLText()");
	    OTMultiUserRoot root = (OTMultiUserRoot) otObject;
	    otrunk = (OTrunkImpl) getViewService(OTrunk.class);
	    
		if (firstRun) { //why is this method called twice?
			overlayManager = new OTUserOverlayManager();
			viewContext.addViewService(OTUserOverlayManager.class, overlayManager);
			loadGlobalOverlay(root);
			loadUserDatabases(root);
			firstRun = false;
		}
	    OTObject reportTemplate = root.getReportTemplate();
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

	    final Vector userDatabases = userList.getUserDatabases().getVector();
	    
	    Runnable userDBTask = new Runnable(){

	    	public synchronized OTUserDatabaseRef nextDb()
	    	{
	    		if(userDatabases.size() == 0){
	    			return null;
	    		}
	    		
	    		return (OTUserDatabaseRef) userDatabases.remove(0);
	    	}
	    	
			public void run()
            {	
				while(true){
					OTUserDatabaseRef ref = nextDb();
					if(ref == null){
						break;
					}
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
							// get the OTOverlay
							OTOverlay overlay = (OTOverlay) otrunk.getExternalObject(overlayURL, ref.getOTObjectService());

							if (overlay == null) {
								// create a blank one
								XMLDatabase xmldb = new XMLDatabase();
								overlay = (OTOverlay) root.getOTObjectService().createObject(OTOverlay.class);
								xmldb.setRoot(overlay.getGlobalId());
								otrunk.remoteSaveData(xmldb, overlayURL, OTViewer.HTTP_PUT);
								overlay = (OTOverlay) otrunk.getExternalObject(overlayURL, ref.getOTObjectService());
							}

							OTObjectService objService = createObjectService(overlay, false);

		    			  	synchronized (this){
		        				// map the object service/overlay to the user
		    			  		overlayManager.add(overlay, objService, userObject);
		    			  	}
						} catch (Exception e) {
							logger.log(Level.SEVERE, "Couldn't initialize user overlay", e);
						}
					}
				}	            
            }
	    
			
	    };

	    Thread [] threads = new Thread[3];
	    for(int i=0; i<threads.length; i++){
	    	threads[i] = new Thread(userDBTask);
	    	threads[i].start();
	    }
		try {
		    for(int i=0; i<threads.length; i++){
		    	threads[i].join();
		    }
        } catch (InterruptedException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }	    
        
        System.out.println("total user db and overlay load time: " + 
        	(System.currentTimeMillis() - start) + "ms");        
	}
	
	protected void loadGlobalOverlay(OTMultiUserRoot root) {
		try {
			if(root.getGroupOverlayURL() == null){
				return;
			}

			long start = System.currentTimeMillis();
			OTOverlay otOverlay = (OTOverlay) otrunk.getExternalObject(root.getGroupOverlayURL(), root.getOTObjectService());
	        if (otOverlay != null) {
    	        // set up the objectService for it
    	        OTObjectService objService = createObjectService(otOverlay, true);
    	        // associate it with the 'null' userobject in otrunk
    	        overlayManager.add(otOverlay, objService, null);
	        }
	        System.out.println("group overlay load time: " + 
	        	(System.currentTimeMillis() - start) + "ms");
		} catch (Exception e) {
	        logger.log(Level.WARNING, "Couldn't set up the group-wide overlay", e);
        }
	}
	
	protected OTObjectService createObjectService(OTOverlay overlay, boolean isGlobal) {
		// create an object service for the overlay
		OverlayImpl myOverlay = new OverlayImpl(overlay);
		if(isGlobal){
			globalOverlay = myOverlay;
		}
		CompositeDatabase db = new CompositeDatabase(otrunk.getDataObjectFinder(), myOverlay);
		if(!isGlobal){
			ArrayList overlays = new ArrayList();
			overlays.add(globalOverlay);
			db.setOverlays(overlays);
		}
	  	OTObjectService objService = otrunk.createObjectService(db);
	  	return objService;
	}

	public boolean getEmbedXHTMLView()
    {
	    return true;
    }
}
