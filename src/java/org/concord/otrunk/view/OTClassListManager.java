package org.concord.otrunk.view;

import java.net.URL;
import java.util.Enumeration;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTBundle;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.OTServiceContext;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.wrapper.OTObjectSet;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.overlay.OTUserOverlayManager;
import org.concord.otrunk.user.OTUserObject;

public class OTClassListManager extends DefaultOTObject
    implements OTBundle
{
	public static interface ResourceSchema extends OTResourceSchema 
	{
		// list of users that are in this class. specify the URL or the list
		URL getUserListURL();
		OTObjectList getUserList();
	}
	private ResourceSchema resources;
	
	// the userListURL should point to an otrunk document with the main object being a OTObjectSet,
	// which encapsulates an OTObjectList of OTClassMember objects
	private URL userListURL;
	
	// userList is a list of OTClassMember objects
	private OTObjectList userList;
	private OTrunkImpl otrunk;
	
	private OTUserObject currentClassMember;

	private OTUserOverlayManager overlayManager;
	private boolean overlayManagerNeedsRegistered = false;
	
	public OTClassListManager(ResourceSchema resources)
    {
	    super(resources);
	    this.resources = resources;
	    this.otrunk = (OTrunkImpl) resources.getOTObjectService().getOTrunkService(OTrunk.class);
    }

	public void initializeBundle(OTServiceContext serviceContext)
	{
		userList = resources.getUserList();
		
		userListURL = resources.getUserListURL();
		if (userListURL != null) {
			initializeUserListFromURL();
		}
		
		overlayManager = (OTUserOverlayManager) serviceContext.getService(OTUserOverlayManager.class);
		if (overlayManager == null) {
			overlayManager = new OTUserOverlayManager(otrunk);
			overlayManagerNeedsRegistered = true;
		}
		
		processUserList();
	}

	public void registerServices(OTServiceContext serviceContext)
	{
		serviceContext.addService(OTClassListManager.class, this);
		if (overlayManagerNeedsRegistered) {
			serviceContext.addService(OTUserOverlayManager.class, overlayManager);
		}
	}
	
	private void initializeUserListFromURL() {
		try {
	        OTObjectSet set = (OTObjectSet) otrunk.getExternalObject(userListURL, resources.getOTObjectService());
	        
	        // because OTObjectList.addAll() is unsupported, iterate and add them individually
	        Enumeration objects = set.getObjects().getVector().elements();
	        while (objects.hasMoreElements()) {
	        	userList.add(objects.nextElement());
	        }
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}

	/**
     * @return OTObjectList - the userList
     */
    public OTObjectList getUserList()
    {
	    return userList;
    }
    
    private void processUserList() {
    	// for each user
    	Enumeration classMemberList = userList.getVector().elements();
    	while (classMemberList.hasMoreElements()) {
    		OTClassMember classMember = (OTClassMember) classMemberList.nextElement();
    		
    	// if current user is set, make it the current user
    	if (classMember.getIsCurrentUser()) {
    		this.currentClassMember = classMember.getUserObject();
    	}
    	// if overlay url is set, load and register overlay
    	if (classMember.getOverlayURL() != null) {
    		try {
	            overlayManager.add(classMember.getOverlayURL(), classMember, classMember.getUserObject(), false);
            } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
    	}
    	}
    }

}
