/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-04-24 15:44:55 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.user;

import java.util.Hashtable;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.OTRelativeID;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTIDFactory;
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
public class OTTemplateDatabase
    implements OTDatabase
{
	protected Hashtable userDataObjectMap = new Hashtable();
	protected Hashtable mappedIdCache = new Hashtable();
	OTDatabase rootDb;
	OTDatabase stateDb;
	OTReferenceMap map;
	OTID databaseId;
	
	public OTTemplateDatabase(OTDatabase rootDb, OTDatabase stateDb,
	        OTReferenceMap map)
	{
	    this.rootDb = rootDb;
	    this.stateDb = stateDb;
	    this.map = map;
	    
	    databaseId = OTUUID.createOTUUID();
	}
	
	OTID getDatabaseId()
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
    public OTDataObject createDataObject() throws Exception
    {
        // Do nothing
        return null;
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#getRelativeOTID(org.concord.framework.otrunk.OTID, java.lang.String)
     */
    public OTID getRelativeOTID(OTID parent, String relativePath)
    {
        String idStr = parent.toString() + "/" + relativePath;
        return new OTRelativeID(idStr);
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#createDataObject(org.concord.framework.otrunk.OTID)
     */
    public OTDataObject createDataObject(OTID id) throws Exception
    {
        // Do nothing
        return null;
    }

    public OTDataObject getStateObject(OTDataObject template)
    {
        OTDataObject doObject = map.getStateObject(template, stateDb);
        return doObject;
    }
    
    public OTDataObject createStateObject(OTDataObject template)
    {
        return map.createStateObject(template, stateDb);        
    }
    
    /* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#getOTDataObject(org.concord.otrunk.datamodel.OTDataObject, org.concord.framework.otrunk.OTID)
     */
    public OTDataObject getOTDataObject(OTDataObject dataParent, OTID childId)
            throws Exception
    {
        OTUserDataObject userDataObject = (OTUserDataObject)userDataObjectMap.get(childId);
        if(userDataObject != null) {
            return userDataObject;
        }
        
        userDataObject = (OTUserDataObject)mappedIdCache.get(childId);
        if(userDataObject != null) {
            return userDataObject;
        }
        
        
        if(stateDb.contains(childId)) {
            // the requested object is in the creationDb
            // this object might have references. so we need to 
            // wrap it so the returned data object has us as
            // the database
            userDataObject = new OTUserDataObject(null, this);
            OTDataObject childObject = stateDb.getOTDataObject(null, childId);
            userDataObject.setStateObject(childObject);
            return userDataObject;
        }
        
        // this object isn't in the creationDb and we haven't 
        OTDataObject templateObject = rootDb.getOTDataObject(null, childId);
        userDataObject = new OTUserDataObject(templateObject, this);

        mappedIdCache.put(childId, userDataObject);
        userDataObjectMap.put(userDataObject.getGlobalId(), userDataObject);
        
        // System.err.println("created userDataObject template-id: " + childId + 
        //         " userDataObject-id: " + userDataObject.getGlobalId());
        
        return userDataObject;
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

}
