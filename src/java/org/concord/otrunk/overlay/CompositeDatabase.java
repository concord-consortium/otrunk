/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
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

/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2007-08-01 14:08:55 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.overlay;

import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.otrunk.datamodel.BlobResource;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDataObjectType;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTTransientMapID;
import org.concord.otrunk.datamodel.OTUUID;

/**
 * OTTemplateDatabase
 * Class name and description
 *
 * Date created: Apr 21, 2005
 *
 * @author scott<p>
 *
 */
public class CompositeDatabase
    implements OTDatabase
{
    /**
     * This is a map from the global id of the base object to the compositeDataObject
     * The base object could be in the root database or it could be an object created 
     * in the overlay
     */
	protected Hashtable dataObjectMap = new Hashtable();
    
	OTDatabase rootDb;
	OTDatabase overlayDb;
	Overlay overlay;
	OTID databaseId;
	
	public CompositeDatabase(OTDatabase rootDb, Overlay overlay)
	{
	    this.rootDb = rootDb;
	    this.overlayDb = overlay.getOverlayDatabase();
	    this.overlay = overlay;
	    
	    databaseId = OTUUID.createOTUUID();
	}
	
	public OTID getDatabaseId()
	{
	    return databaseId;
	}
	
    /* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#setRoot(org.concord.framework.otrunk.OTID)
     */
    public void setRoot(OTID rootId) throws Exception
    {
        // Do nothing
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#getRoot()
     */
    public OTDataObject getRoot() throws Exception
    {
        // Do nothing
        return null;
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#createDataObject()
     */
    public OTDataObject createDataObject(OTDataObjectType type) throws Exception
    {        
        // in this case we need to create a new state object and wrap it
        OTDataObject childObject = overlayDb.createDataObject(type);
        CompositeDataObject compositeDataObject = 
        	new CompositeDataObject(childObject, this, false);
        //System.out.println("v3. " + userDataObject.getGlobalId());
        return compositeDataObject;
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#createDataObject(org.concord.framework.otrunk.OTID)
     */
    public OTDataObject createDataObject(OTDataObjectType type, OTID id) throws Exception
    {
        // in this case we need to create a new state object and wrap it
        OTDataObject childObject = overlayDb.createDataObject(type, id);
        CompositeDataObject userDataObject = 
        	new CompositeDataObject(childObject, this, false);
        //System.out.println("v3. " + userDataObject.getGlobalId());
        return userDataObject;
    }

    public OTDataObject getActiveDeltaObject(OTDataObject baseObject)
    {
    	return overlay.getDeltaObject(baseObject);
    }
    
    public OTDataObject createActiveDeltaObject(OTDataObject baseObject)
    {
    	return overlay.createDeltaObject(baseObject);
    }
    
    /**
     * FIXME:  This method uses the state database to determine whether
     * an object can be directly changed or it needs to be wrapped by 
     * a template (user object) and just that object is changed. 
     * This should not be dependent on the state database because in some
     * cases there will not be a separate database just for the state 
     * objects.  So in this case there should be another way to know if
     * an object should be directly changed when it is access through
     * this database.  The reference map should probably be used for 
     * this.
     * 
     * @see org.concord.otrunk.datamodel.OTDatabase#getOTDataObject(org.concord.otrunk.datamodel.OTDataObject, org.concord.framework.otrunk.OTID)
     */
    public OTDataObject getOTDataObject(OTDataObject dataParent, OTID childId)
            throws Exception
    {
    	childId = resolveID(childId);

    	CompositeDataObject userDataObject = (CompositeDataObject)dataObjectMap.get(childId);
        if(userDataObject != null) {
        	//System.out.println("v1. " + userDataObject.getGlobalId());
            return userDataObject;
        }
        
        if(overlay.contains(childId)) {
            // the requested object is part of the overlay.
            // this object might have references. so we need to 
            // wrap it so the returned data object has us as
            // the database
            OTDataObject childObject = overlayDb.getOTDataObject(null, childId);
            userDataObject = new CompositeDataObject(childObject, this, false);
        	//System.out.println("v3. " + userDataObject.getGlobalId());
            
            // save this object so if it is referenced again the same
            // dataobject is returned.
            dataObjectMap.put(childId, userDataObject);
            return userDataObject;
        }
        
        // this object isn't in the creationDb and we haven't accessed
        // it before so we need to make a new one
        OTDataObject baseObject = rootDb.getOTDataObject(null, childId);
        if(baseObject == null) {
            // we are in a bad state here.  there was a request for a child
            // object that we can't find.  Instead of making a bogus user
            // object lets throw a runtime exception
        	System.err.println("can't find user object: " + childId);
        	return null;
            //throw new RuntimeException("can't find user object: " + childId);
        }
         userDataObject = new CompositeDataObject(baseObject, this, true);

        dataObjectMap.put(childId, userDataObject);
        
        // System.err.println("created userDataObject template-id: " + childId + 
        //         " userDataObject-id: " + userDataObject.getGlobalId());
        
    	//System.out.println("v5. " + userDataObject.getGlobalId());
        return userDataObject;
    }
    
    public OTID resolveID(OTID id)
    {
        if(id instanceof OTTransientMapID) {
        	OTTransientMapID mappedID = (OTTransientMapID)id;
            if(getDatabaseId() == mappedID.getMapToken()) {
                // This is an id that came from our database
                // so get the relative part of the id which is 
                // either an id in the
                // authored database or an id of a brand new object in
                // the user database
                return mappedID.getMappedId();
            }
        }
        
        return id;
    }

    
    /* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#contains(org.concord.framework.otrunk.OTID)
     */
    public boolean contains(OTID id)
    {
        // we only contain one id which is our database id 
        // this id will be used by OTUserDataObject in their
        // globalIds
        
        return id.equals(databaseId);
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#close()
     */
    public void close()
    {
        // do nothing

    }

	/* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#createBlobResource(java.net.URL)
     */
    public BlobResource createBlobResource(URL url)
    {
    	return overlayDb.createBlobResource(url);
    }

	/* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#getPackageClasses()
     */
    public Vector getPackageClasses()
    {
	    // TODO Auto-generated method stub
	    return null;
    }

}
