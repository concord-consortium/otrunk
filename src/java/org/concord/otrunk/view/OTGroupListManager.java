package org.concord.otrunk.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.concord.otrunk.overlay.OTUserOverlayManagerFactory;
import org.concord.otrunk.user.OTUserObject;

public class OTGroupListManager extends DefaultOTObject
    implements OTBundle
{
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public static interface ResourceSchema extends OTResourceSchema 
	{
		// list of users that are in this class. specify the URL or the list
		URL getUserListURL();
		OTObjectList getUserList();
		URL getGroupDataURL();
	}
	private ResourceSchema resources;
	
	// the groupListURL should point to an otrunk document with the main object being a OTObjectSet,
	// which encapsulates an OTObjectList of OTGroupMember objects
	private URL groupListURL;
	private URL groupDataURL;
	private OTUserObject groupUserObject;
	
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
		
		groupDataURL = resources.getGroupDataURL();
		overlayManager = serviceContext.getService(OTUserOverlayManager.class);
		if (overlayManager == null) {
			initOverlayManager();
			serviceContext.addService(OTUserOverlayManager.class, overlayManager);
		}
		if (groupDataURL != null) {
			try {
	            groupUserObject = resources.getOTObjectService().createObject(OTUserObject.class);
	            if (isCurrentMemberSet()) {
	            	overlayManager.addReadOnly(groupDataURL, groupUserObject, false);
	            } else {
	            	overlayManager.addWriteable(groupDataURL, groupUserObject, false);
	            }
            } catch (Exception e) {
	            logger.log(Level.WARNING, "Couldn't set up group datastore.", e);
	            groupUserObject = null;
            }
		}
		processUserList(false);
	}

	public void registerServices(OTServiceContext serviceContext)
	{
		serviceContext.addService(OTGroupListManager.class, this);
	}
	
	private boolean isCurrentMemberSet() {
		for (OTObject obj : userList) {
			OTGroupMember mem = (OTGroupMember) obj;
			if (mem.getIsCurrentUser()) {
				return true;
			}
		}
		return false;
	}
	
	private void initOverlayManager()
    {
		URL autodetectURL = null;
		if (groupDataURL != null) {
			autodetectURL = groupDataURL;
		} else {
			Iterator<OTObject> iterator = userList.iterator();
			while (autodetectURL == null && iterator.hasNext()) {
				OTGroupMember mem = (OTGroupMember) iterator.next();
				autodetectURL = mem.getDataURL();
			}
		}
		overlayManager = OTUserOverlayManagerFactory.getUserOverlayManager(autodetectURL, otrunk);
    }

	private void initializeUserListFromURL() {
		try {
	        OTObjectSet set = (OTObjectSet) otrunk.getExternalObject(groupListURL, resources.getOTObjectService());
	        
	        // add non-duplicated objects
	        for (OTObject obj : set.getObjects()) {
	        	if (! userList.contains(obj)) {
	        		userList.add(obj);
	        	}
	        }
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
    
    public OTUserObject getGroupUser() {
    	return groupUserObject;
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
    					if (groupMember.getIsCurrentUser()) {
    						overlayManager.addWriteable(groupMember.getDataURL(), groupMember.getUserObject(), false);
    					} else {
    						overlayManager.addReadOnly(groupMember.getDataURL(), groupMember.getUserObject(), false);
    					}
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
    
    public boolean isGroupEnabled() {
    	return groupUserObject == null;
    }
    
    public OTGroupMember getMember(OTUserObject user) {
    	// get the root authored user object first, then run through the list
		try {
            user = otrunk.getRuntimeAuthoredObject(user);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Couldn't get the authored version of the object!", e);
        }
    	for (OTObject mem : userList) {
    		OTGroupMember member = (OTGroupMember) mem;
    		if (member.getUserObject().equals(user)) {
    			return member;
    		}
    	}
    	return null;
    }
    
    public ArrayList<OTGroupMember> getMembers(OTObjectList users) {
    	// OTObjectList list = resources.getOTObjectService().createObject(OTObjectList.class);
    	ArrayList<OTGroupMember> list = new ArrayList<OTGroupMember>();
    	for (OTObject user : users) {
    		list.add(getMember((OTUserObject) user));
    	}
    	return list;
    }

	public OTUserObject findUserById(String studentId)
    {
		for( OTObject obj: userList){
    		OTGroupMember groupMember = (OTGroupMember) obj;
    		OTUserObject user = groupMember.getUserObject();
    		if (user.getGlobalId().toExternalForm().equals(studentId)) {
    			return user;
    		}
	    }
	    return null;
    }

}
