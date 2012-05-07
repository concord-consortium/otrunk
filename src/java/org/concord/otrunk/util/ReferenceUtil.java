package org.concord.otrunk.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTID;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDataPropertyReference;
import org.concord.otrunk.datamodel.OTDatabase;

public class ReferenceUtil
{
    private static final Logger logger = Logger.getLogger(ReferenceUtil.class.getName());
    
    public static ArrayList<ArrayList<OTDataPropertyReference>> getIncomingReferences(OTID objectID, List<OTDatabase> dbs) {
    	return getIncomingReferences(objectID, null, false, null, dbs);
    }
    
    public static ArrayList<ArrayList<OTDataPropertyReference>> getIncomingReferences(OTID objectID, boolean getIndirectReferences, List<OTDatabase> dbs) {
    	return getIncomingReferences(objectID, null, getIndirectReferences, null, dbs);
    }
    
    public static ArrayList<ArrayList<OTDataPropertyReference>> getIncomingReferences(OTID objectID, Class<?> filterClass, boolean getIndirectReferences, ArrayList<OTID> excludeIDs, List<OTDatabase> dbs) {
    	ArrayList<OTDataPropertyReference> path = new ArrayList<OTDataPropertyReference>();
    	return getReferences(true, objectID, filterClass, getIndirectReferences, path, excludeIDs, dbs);
    }
    
    public static ArrayList<ArrayList<OTDataPropertyReference>> getReferences(boolean incoming, OTID objectID, Class<?> filterClass, boolean getIndirectReferences, ArrayList<OTDataPropertyReference> currentPath, ArrayList<OTID> excludeIDs, List<OTDatabase> dbs) {
    	ArrayList<ArrayList<OTDataPropertyReference>> allParents = new ArrayList<ArrayList<OTDataPropertyReference>>();
    	if (excludeIDs == null) {
    		excludeIDs = new ArrayList<OTID>();
    	}
    	// XXX Should we be searching all databases?
    	synchronized(dbs) {
        	for (OTDatabase db : dbs) {
            	try {
        	        ArrayList<OTDataPropertyReference> parents = null;
        	        if (incoming) {
        	        	parents = db.getIncomingReferences(objectID);
        	        } else {
        	        	parents = db.getOutgoingReferences(objectID);
        	        }
        	        if (parents != null) {
            	        logger.finest("Found " + parents.size() + " references");
            	        for (OTDataPropertyReference reference : parents) {
            	        	/* FIXME by skipping objects we've seen already, it's possible that we're not including all of the possible paths to an object.
            	        	 * For instance, if A indirectly references D through both B and C, only one of A -> B -> D or A -> C -> D will be returned.
            	        	 * So while this code *will* find all the correct endpoints, it won't necessarily reflect all of the possible paths between those endpoints.
            	        	 */
            	        	OTID pId;
            	        	if (incoming) {
            	        		pId= reference.getSource();
            	        	} else {
            	        		pId = reference.getDest();
            	        	}
            	        	if (! excludeIDs.contains(pId)) {
            	        		logger.finest("Found reference id: " + pId);
            	        		excludeIDs.add(pId);
            	        		
            	        		ArrayList<OTDataPropertyReference> pPath = (ArrayList<OTDataPropertyReference>) currentPath.clone();
            	        		pPath.add(reference);
            	        		
                    	        OTDataObject parentObj = db.getOTDataObject(null, pId);
                    	        if (parentObj != null) {
                        	        if (filterClass != null) {
                        	        	logger.finest("Filter class: " + filterClass.getSimpleName() + ", parent class: " + parentObj.getType().getClassName());
                        	        }
                        	        if (filterClass == null || filterClass.isAssignableFrom(Class.forName(parentObj.getType().getClassName()))) {
                        	        	logger.finest("Found a matching parent: " + parentObj.getGlobalId());
                        	        	allParents.add(pPath);
                        	        }
                    	        	if (getIndirectReferences) {
                    	        		logger.finest("recursing");
                    	        		allParents.addAll(getReferences(incoming, pId, filterClass, true, pPath, excludeIDs, dbs));
                    	        		logger.finest("unrecursing");
                    	        	}
                    	        } else {
                    	        	logger.warning("Had parent id but no real object!: " + pId);
                    	        }
            	        	} else {
            	        		logger.finest("Already seen this id: " + pId);
            	        	}
            	        }
        	        } else {
        	        	logger.finest("null parents");
        	        }
                } catch (Exception e) {
        	        // TODO Auto-generated catch block
                	logger.log(Level.WARNING, "Error finding parents", e);
                }
        	}
    	}
    	return allParents;
    }
    
    public static ArrayList<ArrayList<OTDataPropertyReference>> getIncomingReferences(OTID objectID, Class<?> filterClass, boolean getIndirectReferences, List<OTDatabase> dbs) {
    	logger.finer("Finding references for: " + objectID + " with class: " + (filterClass == null ? "null" : filterClass.getName()) + " and recursion: " + getIndirectReferences);
    	ArrayList<ArrayList<OTDataPropertyReference>> parents = getIncomingReferences(objectID, filterClass, getIndirectReferences, null, dbs);
    	logger.finer("found " + parents.size() + " matching parents");
    	return parents;
    }
    
    public static ArrayList<ArrayList<OTDataPropertyReference>> getOutgoingReferences(OTID objectID, List<OTDatabase> dbs) {
    	return getOutgoingReferences(objectID, null, false, null, dbs);
    }
    
    public static ArrayList<ArrayList<OTDataPropertyReference>> getOutgoingReferences(OTID objectID, boolean getIndirectReferences, List<OTDatabase> dbs) {
    	return getOutgoingReferences(objectID, null, getIndirectReferences, null, dbs);
    }
    
    public static ArrayList<ArrayList<OTDataPropertyReference>> getOutgoingReferences(OTID objectID, Class<?> filterClass, boolean getIndirectReferences, ArrayList<OTID> excludeIDs, List<OTDatabase> dbs) {
    	return getReferences(false, objectID, filterClass, getIndirectReferences, new ArrayList<OTDataPropertyReference>(), excludeIDs, dbs);
    }
    
    public static ArrayList<ArrayList<OTDataPropertyReference>> getOutgoingReferences(OTID objectID, Class<?> filterClass, boolean getIndirectReferences, List<OTDatabase> dbs) {
    	logger.finest("Finding references for: " + objectID + " with class: " + (filterClass == null ? "null" : filterClass.getName()) + " and recursion: " + getIndirectReferences);
    	ArrayList<ArrayList<OTDataPropertyReference>> parents = getOutgoingReferences(objectID, filterClass, getIndirectReferences, null, dbs);
    	logger.finest("found " + parents.size() + " matching parents");
    	return parents;
    }
}
