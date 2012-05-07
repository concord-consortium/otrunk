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
 * $Revision: 1.3 $
 * $Date: 2007-10-22 01:50:38 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.overlay;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTPackage;
import org.concord.otrunk.datamodel.BlobResource;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDataObjectFinder;
import org.concord.otrunk.datamodel.OTDataObjectType;
import org.concord.otrunk.datamodel.OTDataPropertyReference;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTTransientMapID;
import org.concord.otrunk.datamodel.OTUUID;
import org.concord.otrunk.util.ReferenceUtil;
import org.concord.otrunk.xml.XMLDataObject;
import org.concord.otrunk.xml.XMLDataObjectRef;

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
    private static final Logger logger = Logger.getLogger(CompositeDatabase.class.getName());
    /**
     * This is a map from the global id of the base object to the compositeDataObject
     * The base object could be in the root database or it could be an object created 
     * in the overlay
     */
	protected HashMap<OTID, CompositeDataObject> dataObjectMap = 
		new HashMap<OTID, CompositeDataObject>();
    
	OTDataObjectFinder objectFinder;
	OTDatabase activeOverlayDb;
	Overlay activeOverlay;
	ArrayList<Overlay> middleOverlays;
	OTID databaseId;
	private boolean pullAllAttributesIntoCurrentLayer = false;
	
	private HashMap<OTID, ArrayList<OTDataPropertyReference>> incomingReferences = 
		new HashMap<OTID, ArrayList<OTDataPropertyReference>>();
	
	private HashMap<OTID, ArrayList<OTDataPropertyReference>> outgoingReferences = 
		new HashMap<OTID, ArrayList<OTDataPropertyReference>>();

	private OverlayListener overlayListener = new OverlayListener(){

		public void newDeltaObject(Overlay overlay, OTDataObject baseObject)
        {
			// see if we have a this baseObject in a our list, update its
			// middleObjects if we do
	    	CompositeDataObject compDataObject = dataObjectMap.get(baseObject.getGlobalId());
	        if(compDataObject == null) {
	        	return;
	        }

	        // reconstruct the middle deltas
	        OTDataObject[] middleDeltas = createMiddleDeltas(baseObject);
	        compDataObject.setMiddleDeltas(middleDeltas);
	        return;
        }
    	
    };
	
	public CompositeDatabase(OTDataObjectFinder objectFinder, Overlay activeOverlay)
	{
		this.objectFinder = objectFinder;
	    this.activeOverlayDb = activeOverlay.getOverlayDatabase();
	    this.activeOverlay = activeOverlay;
	    
	    databaseId = OTUUID.createOTUUID();
	}
	
	public void setOverlays(ArrayList<Overlay> overlays)
	{
	    this.middleOverlays = overlays;
	    for (Overlay overlay : overlays) {	        
	    	overlay.addOverlayListener(overlayListener);
	    }
	}
	
	public ArrayList<Overlay> getOverlays()
	{
		ArrayList<Overlay> overlays = new ArrayList<Overlay>();
	    for (Overlay overlay : middleOverlays) {	        
	    	overlays.add(overlay);
	    }
	    return overlays;
	}
	
	public Overlay getActiveOverlay() {
		return this.activeOverlay;
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
        OTDataObject childObject = activeOverlayDb.createDataObject(type);
        CompositeDataObject compositeDataObject = 
        	new CompositeDataObject(childObject, this, null, false);
        activeOverlay.registerNonDeltaObject(childObject);
        dataObjectMap.put(compositeDataObject.getGlobalId(), compositeDataObject);
        //System.out.println("v3. " + userDataObject.getGlobalId());
        return compositeDataObject;
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#createDataObject(org.concord.framework.otrunk.OTID)
     */
    public OTDataObject createDataObject(OTDataObjectType type, OTID id) throws Exception
    {
        // in this case we need to create a new state object and wrap it
        OTDataObject childObject = activeOverlayDb.createDataObject(type, id);
        CompositeDataObject userDataObject = 
        	new CompositeDataObject(childObject, this, null, false);
        activeOverlay.registerNonDeltaObject(childObject);
        dataObjectMap.put(id, userDataObject);
        //System.out.println("v3. " + userDataObject.getGlobalId());
        return userDataObject;
    }

    public OTDataObject getActiveDeltaObject(OTDataObject baseObject)
    {
    	OTDataObject deltaObject = activeOverlay.getDeltaObject(baseObject);
    	if(deltaObject instanceof XMLDataObject){
    		((XMLDataObject) deltaObject).setSaveNulls(true);
    	}
		return deltaObject;
    }
    
    public OTDataObject createActiveDeltaObject(OTDataObject baseObject)
    {
    	OTDataObject deltaObject = activeOverlay.createDeltaObject(baseObject);
    	if(deltaObject instanceof XMLDataObject){
    		((XMLDataObject) deltaObject).setSaveNulls(true);
    	}    	
		return deltaObject;
    }
    
    public OTDatabase getActiveOverlayDb() {
    	return activeOverlayDb;
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

    	CompositeDataObject userDataObject = dataObjectMap.get(childId);
        if(userDataObject != null) {
        	//System.out.println("v1. " + userDataObject.getGlobalId());
            return userDataObject;
        }
        
        if(activeOverlay.contains(childId)) {
            // the requested object is part of the overlay.
            // this object might have references. so we need to 
            // wrap it so the returned data object has us as
            // the database
            OTDataObject childObject = activeOverlayDb.getOTDataObject(null, childId);
            userDataObject = new CompositeDataObject(childObject, this, null, false);
        	//System.out.println("v3. " + userDataObject.getGlobalId());
            
            // save this object so if it is referenced again the same
            // dataobject is returned.
            dataObjectMap.put(childId, userDataObject);
            return userDataObject;
        }
        
        // this object isn't in the creationDb and we haven't accessed
        // it before so we need to make a new one
        // We use the OTDataObjectFinder object for this.  The code that setup
        // this composite database should pass in a objectFinder which only
        // finds objects this composite database should compose.  
        OTDataObject baseObject = objectFinder.findDataObject(childId);
        if(baseObject == null) {
            // we are in a bad state here.  there was a request for a child
            // object that we can't find.  Instead of making a bogus user
            // object lets throw a runtime exception
        	System.err.println("can't find user object: " + childId);
        	return null;
            //throw new RuntimeException("can't find user object: " + childId);
        }
        
        OTDataObject[] middleDeltas = createMiddleDeltas(baseObject);
        
        userDataObject = new CompositeDataObject(baseObject, this, middleDeltas, true);

        dataObjectMap.put(childId, userDataObject);
        
        // System.err.println("created userDataObject template-id: " + childId + 
        //         " userDataObject-id: " + userDataObject.getGlobalId());
        
    	//System.out.println("v5. " + userDataObject.getGlobalId());
        return userDataObject;
    }

	protected OTDataObject[] createMiddleDeltas(OTDataObject baseObject)
    {
	    OTDataObject middleDeltas [] = null;
        if(middleOverlays != null){
        	ArrayList<OTDataObject> middleDeltasList = new ArrayList<OTDataObject>();
        	// if we have middle overlays then we need to see if any of them have a delta for this
        	// object
        	for(int i=0; i<middleOverlays.size(); i++){
        		Overlay middleOverlay = middleOverlays.get(i);
        		OTDataObject middleDelta = middleOverlay.getDeltaObject(baseObject);
        		if(middleDelta != null){
        			middleDeltasList.add(middleDelta);
        		}
        	}
        	
        	if(middleDeltasList.size() > 0){
        		middleDeltas = new OTDataObject[middleDeltasList.size()];
        		middleDeltasList.toArray(middleDeltas);
        	}
        }
	    return middleDeltas;
    }
    
    public OTID resolveID(OTID id)
    {
        if(id instanceof OTTransientMapID) {
        	OTTransientMapID mappedID = (OTTransientMapID)id;
            if(getDatabaseId().equals(mappedID.getMapToken())) {
                // This is an id that came from our database
                // so get the relative part of the id which is 
                // either an id in the
                // authored database or an id of a brand new object in
                // the user database
                return mappedID.getMappedId();
            } else {
            	// this transient id is not in our database.  So probably this is a case where
            	// multiple overlays are being used. So this object is coming from another 
            	// composite database.
            	
            	// emit a warning and try just running with it.
//                OTID underlyingId = mappedID.getMappedId();
//            	OTDataObject dataObject = null;
//            	try {
//            		dataObject = objectFinder.findDataObject(underlyingId);
//            	} catch (Exception e) {
//            		e.printStackTrace();            		
//            	}
//            	URI dbURI = null;
//            	if(dataObject != null){
//            		OTDatabase database = dataObject.getDatabase();
//            		dbURI = database.getURI();
//            	}
//                logger.warning("Can't resolve id: " + mappedID.toInternalForm() + 
//            		"\n   whose underlying object is from database with uri: " + dbURI +
//            		"\n   attempting to load it via its mapped id.");
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
    	return new BlobResource(url);
    }

	/* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#getPackageClasses()
     */
    public ArrayList<Class<? extends OTPackage>> getPackageClasses()
    {
	    // TODO Auto-generated method stub
	    return null;
    }

	public URI getURI()
    {
		try {
	        return new URI("composite-db:/" + getDatabaseId());
        } catch (URISyntaxException e) {
	        e.printStackTrace();
        }
        
        return null;
    }

	public HashMap<OTID, CompositeDataObject> getDataObjects()
    {
	    return dataObjectMap;
    }

	public ArrayList<OTDataPropertyReference> getOutgoingReferences(OTID otid)
    {
		return outgoingReferences.get(otid);
    }

	public ArrayList<OTDataPropertyReference> getIncomingReferences(OTID otid)
    {
		return incomingReferences.get(otid);
    }
	
	public synchronized void pruneNonDeltaObjects() {
		activeOverlay.pruneNonDeltaObjects();
	}
	
	public void recordReference(OTDataObject parent, OTDataObject child, String property)
	{
		if (parent == null || child == null) {
			// can't reference "null"
			return;
		}

		if (child instanceof XMLDataObjectRef) {
			// this will be handled in the second pass
			return;
		}
		
		OTID parentID = parent.getGlobalId();
		OTID childID = child.getGlobalId();

		recordReference(parentID, childID, property);
	}
	
	public void recordReference(OTID parentID, OTID childID, String property) {
		if (parentID == null || childID == null) {
			// can't reference null
			return;
		}
		parentID = parentID.getMappedId();
		childID  = childID.getMappedId();
		
		logger.finer("Recording reference: " + parentID + " (" + property + ") --> " + childID);
		
		OTDataPropertyReference ref = new OTDataPropertyReference(parentID, childID, property);
		
		ArrayList<OTDataPropertyReference> parents = incomingReferences.get(childID);
		ArrayList<OTDataPropertyReference> children = outgoingReferences.get(parentID);
		if (parents == null) {
			parents = new ArrayList<OTDataPropertyReference>();
		}
		if (children == null) {
			children = new ArrayList<OTDataPropertyReference>();
		}
		if (! parents.contains(ref)) {
			parents.add(ref);
			incomingReferences.put(childID, parents);
		}
		
		if (! children.contains(ref)) {
			children.add(ref);
			outgoingReferences.put(parentID, children);
		}
	}
	
	public void removeReference(OTDataObject parent, OTDataObject child) {
		if (parent == null || child == null) {
			// can't reference null
			return;
		}
		
		OTID parentID = parent.getGlobalId();
		OTID childID = child.getGlobalId();

		removeReference(parentID, childID);
	}
	
	public void removeReference(OTID parentID, OTID childID) {
		if (parentID == null || childID == null) {
			// can't reference null
			return;
		}
		
		parentID = parentID.getMappedId();
		childID  = childID.getMappedId();
		
		logger.finer("Removing reference: " + parentID + " --> " + childID);	
		
		ArrayList<OTDataPropertyReference> parents = incomingReferences.get(childID);
		ArrayList<OTDataPropertyReference> children = outgoingReferences.get(parentID);

		if (parents != null) {
			OTDataPropertyReference refToRemove = null;
			for (OTDataPropertyReference ref : parents) {
				if (ref.getSource().equals(parentID) && ref.getDest().equals(childID)) {
					refToRemove = ref;
					break;
				}
			}
			
			if (refToRemove != null) {
				parents.remove(refToRemove);
				incomingReferences.put(childID, parents);
			}
		}
		
		if (children != null) {
			OTDataPropertyReference refToRemove = null;
			for (OTDataPropertyReference ref : children) {
				if (ref.getSource().equals(parentID) && ref.getDest().equals(childID)) {
					refToRemove = ref;
					break;
				}
			}
			
			if (refToRemove != null) {
				children.remove(refToRemove);
				outgoingReferences.put(parentID, children);
			}
		}
	}

	public boolean shouldPullAllAttributesIntoCurrentLayer()
    {
	    return pullAllAttributesIntoCurrentLayer;
    }

	public void setPullAllAttributesIntoCurrentLayer(boolean pullAllAttributesIntoCurrentLayer)
    {
	    this.pullAllAttributesIntoCurrentLayer = pullAllAttributesIntoCurrentLayer;
    }

	public void writeClosestCompositeParentKey(CompositeDataObject compositeDataObject)
    {
	    // Find closest composite parent
		// Set the resource key that leads to the composite object
		ArrayList<OTDatabase> dbs = new ArrayList<OTDatabase>();
		dbs.add(this);
		ArrayList<ArrayList<OTDataPropertyReference>> incomingReferences = ReferenceUtil.getIncomingReferences(compositeDataObject.getGlobalId().getMappedId(), true, dbs);
		ArrayList<OTDataPropertyReference> shortest = null;
		int shortestLength = Integer.MAX_VALUE;
		for (ArrayList<OTDataPropertyReference> refChain : incomingReferences) {
			if (refChain.size() < shortestLength) {
				OTDataObject parent = findDataObject(refChain.get(refChain.size()-1).getSource());
				if (parent instanceof CompositeDataObject && ((CompositeDataObject) parent).isComposite()) {
    				shortest = refChain;
    				shortestLength = refChain.size();
				}
			}
		}
		
		if (shortest != null) {
			OTDataPropertyReference ref = shortest.get(shortest.size()-1);
			OTID pId = ref.getSource();
			OTID cId = ref.getDest();
			String prop = ref.getProperty();
			
			// if (! prop.equals("unknown")) {
    			OTDataObject pDo = findDataObject(pId);
    			OTDataObject cDo = findDataObject(cId);
    			
    			if (pDo != null && cDo != null && prop != null) {
    				pDo.setResource(prop, null);
    				pDo.setResource(prop, cDo.getGlobalId());
    			}
			// }
		}
    }
	
	private OTDataObject findDataObject(OTID id) {
		// First try matching the id directly
		OTDataObject obj = dataObjectMap.get(id);
		if (obj != null) { return obj; }
		
		// Then try mapping the dataObjectMap's keys to mapped ids
		for (OTID k : dataObjectMap.keySet()) {
			OTID mk = k.getMappedId();
			if (mk.equals(id)) {
				return dataObjectMap.get(k);
			}
		}
		
		// Finally, check if the mapped id is different than the id
		OTID mId = id.getMappedId();
		if (mId.equals(id)) {
			return null;
		}
		// If it's different, try searching with the mapped id
		return findDataObject(mId);
	}

	public HashMap<OTID, ArrayList<OTDataPropertyReference>> getIncomingReferences()
    {
	    return incomingReferences;
    }

	public HashMap<OTID, ArrayList<OTDataPropertyReference>> getOutgoingReferences()
    {
	    return outgoingReferences;
    }

}
