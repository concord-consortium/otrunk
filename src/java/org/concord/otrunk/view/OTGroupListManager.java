package org.concord.otrunk.view;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTBundle;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.OTServiceContext;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.wrapper.OTObjectSet;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.overlay.OTUserOverlayManager;

public class OTGroupListManager extends DefaultOTObject
    implements OTBundle
{
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public static interface ResourceSchema extends OTResourceSchema 
	{
		// list of users that are in this class. specify the URL or the list
		URL getUserListURL();
		OTObjectList getUserList();
	}
	private ResourceSchema resources;
	
	// the groupListURL should point to an otrunk document with the main object being a OTObjectSet,
	// which encapsulates an OTObjectList of OTGroupMember objects
	private URL groupListURL;
	
	// userList is a list of OTClassMember objects
	private OTObjectList userList;
	private OTrunkImpl otrunk;
	
	private OTGroupMember currentGroupMember;

	private OTUserOverlayManager overlayManager;
	
	public OTGroupListManager(ResourceSchema resources)
    {
	    super(resources);
	    this.resources = resources;
	    this.otrunk = (OTrunkImpl) resources.getOTObjectService().getOTrunkService(OTrunk.class);
    }

	public void initializeBundle(OTServiceContext serviceContext)
	{
		userList = resources.getUserList();
		
		groupListURL = resources.getUserListURL();
		if (groupListURL != null) {
			initializeUserListFromURL();
		}
		
		processUserList(false);
	}

	public void registerServices(OTServiceContext serviceContext)
	{
		serviceContext.addService(OTGroupListManager.class, this);
		overlayManager = serviceContext.getService(OTUserOverlayManager.class);
		if (overlayManager == null) {
			overlayManager = new OTUserOverlayManager(otrunk);
			serviceContext.addService(OTUserOverlayManager.class, overlayManager);
			// logger.info("Overlay manager registered as service");
		}
	}
	
	private void initializeUserListFromURL() {
		try {
	        OTObjectSet set = (OTObjectSet) otrunk.getExternalObject(groupListURL, resources.getOTObjectService());
	        
	        userList.addAll(set.getObjects());
        } catch (Exception e) {
	        logger.log(Level.SEVERE, "Couldn't initialize class list from URL: " + groupListURL, e);
        }
	}

	/**
     * @return OTObjectList - the userList
     */
    public OTObjectList getUserList()
    {
	    return userList;
    }
    
    private void processUserList(boolean reload) {
    	// logger.info("processing users...");
    	// for each user
    	
    	for(OTObject obj: userList){
    		OTGroupMember groupMember = (OTGroupMember) obj;

    		// if current user is set, make it the current user
    		if (groupMember.getIsCurrentUser()) {
    			this.currentGroupMember = groupMember;
    		}
    		// if overlay url is set, load and register overlay
    		if (groupMember.getDataURL() != null) {
    			try {
    				if (reload) {
    					// to avoid problems where the overlayManager still references different versions of the overlay, we'll remove the old ones first
    					overlayManager.reload(groupMember.getUserObject());
    				} else {
    					overlayManager.add(groupMember.getDataURL(), groupMember.getUserObject(), false);
    				}
    			} catch (Exception e) {
    				logger.log(Level.WARNING, "Couldn't load overlay for user: " + groupMember.getName(), e);
    			}
    		}
    	}
    }
    
    public OTGroupMember getCurrentGroupMember() {
    	return this.currentGroupMember;
    }
    
    public void reloadAll() {
    	processUserList(true);
    }

}
