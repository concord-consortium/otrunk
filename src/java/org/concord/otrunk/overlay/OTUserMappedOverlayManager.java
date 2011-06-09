package org.concord.otrunk.overlay;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.wrapper.OTObjectSet;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.net.HTTPRequestException;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.util.MultiThreadedProcessor;
import org.concord.otrunk.util.MultiThreadedProcessorRunnable;
import org.concord.otrunk.xml.XMLDatabase;

public class OTUserMappedOverlayManager
    extends OTUserOverlayManager
{
	private static final Logger logger = Logger.getLogger(OTUserMappedOverlayManager.class.getName());
	public OTUserMappedOverlayManager(OTrunkImpl otrunk)
    {
	    super(otrunk);
	    tempObjService = otrunk.createTemporaryObjectService(null);
    }

	private HashMap<OTUserObject, OTObjectToOverlayReferenceMap> userToOverlayReferenceMaps = new HashMap<OTUserObject, OTObjectToOverlayReferenceMap>();
	private OTObjectService tempObjService;
	
	/**
	 * 
	 * @param user
	 * @param original
	 * @param reference - if null, gets the most recent entry
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public <T extends OTObject> T getOTObject(OTUserObject user, T original, OTOverlayReference reference) {
		writeLock();
		try {
    		OTID authoredId = getAuthoredId(original);
    		
    		if (reference == null) {
    			reference = findLastReference(user, original);
    		}
    		
    		if (reference != null) {
    			URL overlayURL = reference.getOverlayURL();

    			if (! overlayToObjectServiceMap.containsKey(overlayURL)) {
    				loadOverlay(overlayURL, false);
    			}
    			OTObjectService objService = overlayToObjectServiceMap.get(overlayURL);
    			try {
    	            return (T) objService.getOTObject(authoredId);
                } catch (Exception e) {
    	            logger.log(Level.SEVERE, "Can't load object (" + authoredId + ") from object service with url: " + overlayURL, e);
                }
    		}
    		try {
    			return (T) tempObjService.getOTObject(authoredId);
    		} catch (Exception e) {
    			logger.log(Level.SEVERE, "Can't load object (" + authoredId + ") from temp object service", e);
    		}
    		
	        return null;
		} finally {
			writeUnlock();
		}
	}
	
	private OTOverlayReference findLastReference(OTUserObject user, OTObject object) {
		return findReference(user, object, UserSubmission.MOST_RECENT_SUBMISSION);
	}
	
	private OTOverlayReference findReference(OTUserObject user, OTObject object, int submissionNumber) {
		ArrayList<OTOverlayReference> allReferences = findAllReferences(user, object);

		if (allReferences.size() > 0) {
    		if (submissionNumber == UserSubmission.MOST_RECENT_SUBMISSION) {
    			OTOverlayReference ref = allReferences.get(allReferences.size()-1);
    			return ref;
    		}
    		for (OTOverlayReference ref : allReferences) {
    			if (ref.getNumber() == submissionNumber) {
    				return ref;
    			}
    		}
		}
		return null;
	}
	
	private ArrayList<OTOverlayReference> findAllReferences(OTUserObject user, OTObject object) {
		ArrayList<OTOverlayReference> references = new ArrayList<OTOverlayReference>();
		readLock();
		try {
			if (userToOverlayReferenceMaps.containsKey(user)) {
    			OTID authoredId = getAuthoredId(object);
    			OTObjectToOverlayReferenceMap referenceMap = userToOverlayReferenceMaps.get(user);
    			
    			OTObjectSet set = (OTObjectSet) referenceMap.getObjectToOverlayMap().getObject(authoredId.toExternalForm());
    			int size = (set == null) ? 0 : set.getObjects().size();
    			if (size > 0) {
        			OTObjectList referenceList = set.getObjects();
        			for (OTObject refObj : referenceList) {
        				OTOverlayReference ref = (OTOverlayReference) refObj;
        				references.add(ref);
        			}
    			}
    		}
		} finally {
			readUnlock();
		}

		// make sure they are sorted from oldest to newest
		Collections.sort(references, new Comparator<OTOverlayReference>() {
			public int compare(OTOverlayReference a, OTOverlayReference b)
            {
	            return Integer.valueOf(a.getNumber()).compareTo(Integer.valueOf(b.getNumber()));
            }
		});
		return references;
	}

	@Override
    public void addReadOnly(URL referenceMapURL, OTUserObject userObject, boolean isGlobal)
	    throws Exception
	{
		writeLock();
		try {
    		OTObjectToOverlayReferenceMap referenceMap = loadRemoteObject(referenceMapURL, OTObjectToOverlayReferenceMap.class);
    		userToOverlayReferenceMaps.put(userObject, referenceMap);
    		userToOverlayMap.put(userObject, referenceMapURL);
    		
    		// ensure this user is only readable
    		writeableUsers.remove(userObject);
    		readOnlyUsers.add(userObject);
		} finally {
			writeUnlock();
		}
	}
	
	@Override
    public void addWriteable(URL referenceMapURL, OTUserObject userObject, boolean isGlobal)
	    throws Exception
	{
		writeLock();
		try {
    		OTObjectToOverlayReferenceMap referenceMap = loadRemoteObject(referenceMapURL, OTObjectToOverlayReferenceMap.class);
    		userToOverlayReferenceMaps.put(userObject, referenceMap);
    		userToOverlayMap.put(userObject, referenceMapURL);
    		
    		// ensure this user is only writeable
    		readOnlyUsers.remove(userObject);
    		writeableUsers.add(userObject);
		} finally {
			writeUnlock();
		}
	}

	@Override
    public <T extends OTObject> T getOTObject(OTUserObject userObject, T object) throws Exception
	{
		return getOTObject(userObject, object, null);
	}
	
	@Override
    public <T extends OTObject> T getOTObject(UserSubmission submission, T object) throws Exception
	{
		if (submission.getSubmissionNumber() == 0) {
			return null;
		}
		OTOverlayReference reference = findReference(submission.getUser(), object, submission.getSubmissionNumber());
		return getOTObject(submission.getUser(), object, reference);
	}
	
	@Override
	public <T extends OTObject> ArrayList<T> getAllOTObjects(final OTUserObject userObject, final T object) throws Exception {
		final ArrayList<T> list = new ArrayList<T>();
		final ArrayList<OTOverlayReference> allReferences = findAllReferences(userObject, object);
		
		// use 3 threads to speed things up
		MultiThreadedProcessorRunnable<OTOverlayReference> objectLoadingTask = new MultiThreadedProcessorRunnable<OTOverlayReference>(){
			public void process(OTOverlayReference ref) {
				T newObject = getOTObject(userObject, object, ref);
				synchronized(list) {
					list.add(newObject);
				}
            }
	    };
	    int numThreads = (allReferences.size() > 3) ? 3 : 1;
	    MultiThreadedProcessor<OTOverlayReference> processor = new MultiThreadedProcessor<OTOverlayReference>(allReferences, numThreads, objectLoadingTask);
	    processor.process();
		return list;
	};
	
	@Override
	protected OTObjectService getObjectService(OTUserObject userObject, OTObject object)
	{
		OTObject authoredObject = getAuthoredObject(object);
	    return getOTObject(userObject, authoredObject, null).getOTObjectService();
	}
	
	@Override
	protected Set<OTUserObject> getAllUsers()
	{
		readLock();
		try {
			return userToOverlayReferenceMaps.keySet();
		} finally {
			readUnlock();
		}
	}

	@Override
    public void remove(OTUserObject userObject)
	{
		writeLock();
		try {
    		userToOverlayReferenceMaps.remove(userObject);
    		super.remove(userObject);
		} finally {
			writeUnlock();
		}
	}
	
	private void removeReference(URL referenceURL) {
		writeLock();
		try {
    		OTObjectService objService = overlayToObjectServiceMap.get(referenceURL);
    	    
    		otrunk.removeObjectService((OTObjectServiceImpl) objService);
    		overlayToObjectServiceMap.remove(referenceURL);
		} finally {
			writeUnlock();
		}
	}

	@Override
    public void reload(OTUserObject userObject) throws Exception
	{
		userObject = getAuthoredObject(userObject);
		OTObjectToOverlayReferenceMap otObjectToOverlayReferenceMap;
		readLock();
		try {
    		if (! readOnlyUsers.contains(userObject)) {
    			return;
    		}
    		otObjectToOverlayReferenceMap = userToOverlayReferenceMaps.get(userObject);
    		if (otObjectToOverlayReferenceMap == null) {
    			return;
    		}
		} finally {
			readUnlock();
		}
		
		XMLDatabase xmlDb = getXMLDatabase(otObjectToOverlayReferenceMap.getOTObjectService());
		if (doesDbNeedReloaded(xmlDb)) {
			writeLock();
			try {
    			remove(userObject);
    			addReadOnly(xmlDb.getSourceURL(), userObject, false);
			} finally {
				writeUnlock();
			}
			// TODO we could potentially also figure out which object changed and include that in our notification
			notifyListeners(userObject);
		}
	}

	@Override
	public void remoteSave(OTUserObject user, OTObject object) throws Exception
	{
		remoteSave(user, object, false);
	}
	
    @Override
    public void remoteSave(OTUserObject user, OTObject object, boolean forceSave) throws Exception
	{
		writeLock();
		try {
    		if (! writeableUsers.contains(user)) {
    			return;
    		}
    		if (forceSave || isObjectModified(user, object)) {
        		incrementSubmitCount(object);
        		setSubmitTimestamp(object);
        		OTOverlayReference ref = spawnOverlay(user, object);
        		addReferenceToMap(user, object, ref);
    		}
		} finally {
			writeUnlock();
		}
	}

	private void addReferenceToMap(OTUserObject user, OTObject object, OTOverlayReference ref) throws HTTPRequestException, Exception
    {
		writeLock();
		try {
    	    // add overlay reference to the user's overlay reference map
    		OTID authoredId = getAuthoredId(object);
    		OTObjectToOverlayReferenceMap map = userToOverlayReferenceMaps.get(user);
    		OTObjectSet otObjectSet = (OTObjectSet)map.getObjectToOverlayMap().getObject(authoredId.toExternalForm());
    		if (otObjectSet == null) {
    			otObjectSet = map.getOTObjectService().createObject(OTObjectSet.class);
    			map.getObjectToOverlayMap().putObject(authoredId.toExternalForm(), otObjectSet);
    		}
    		if (! otObjectSet.getObjects().contains(ref)) {
        		otObjectSet.getObjects().add(ref);
        		// save overlay reference database
        		actualRemoteSave(map.getOTObjectService());
    		}
		} finally {
			writeUnlock();
		}
    }
	
	private OTOverlayReference spawnOverlay(OTUserObject user, OTObject object) throws Exception {
		writeLock();
		try {
    		// generate next overlay url
    		OTOverlayReference overlayRef = generateNextOverlayReference(user, object);
    		// create overlay for object
    		OTObject obj = getOTObject(user, object, overlayRef);
    		OTObjectService objService = obj.getOTObjectService();
    		// copy object into overlay
    		if (copyObjectIntoOverlay(user, object, obj)) {
    			// save overlay
    			actualRemoteSave(objService);
    		}
    		return overlayRef;
		} finally {
			writeUnlock();
		}
	}

	private OTOverlayReference generateNextOverlayReference(OTUserObject user, OTObject object)
    {
		readLock();
		try {
    		OTID authoredId = getAuthoredId(object);
    	    OTOverlayReference lastRef = findLastReference(user, object);
    	    int number = 1;
    	    if (lastRef != null) {
    	    	number = lastRef.getNumber()+1;
    	    }
    	    String suffix = "-" + authoredId.toExternalForm() + "-" + number;
    	    suffix = suffix.replaceAll("[^a-zA-Z0-9\\-]+", "-") + ".otml";
    	    OTOverlayReference newRef;
            try {
    	        newRef = userToOverlayReferenceMaps.get(user).getOTObjectService().createObject(OTOverlayReference.class);
            } catch (Exception e1) {
    	        e1.printStackTrace();
    	        return null;
            }
    	    newRef.setNumber(number);
    	    URL url = null;
    	    try {
    	    	// create as URI first so that it can do any escaping of invalid url chars when it converts to a URL
    	        URL mapUrl = userToOverlayMap.get(user);
    	        URI referenceUri = new URI(mapUrl.toExternalForm().replaceFirst("\\.otml", suffix));
				url = referenceUri.toURL();
            } catch (Exception e) {
    	        e.printStackTrace();
    	        return null;
            }
    	    newRef.setOverlayURL(url);
    	    return newRef;
		} finally {
			readUnlock();
		}
    }

	@Override
    public int getSubmissionNumber(OTUserObject user, OTObject object)
        throws Exception
    {
		OTOverlayReference ref = findReference(user, object, UserSubmission.MOST_RECENT_SUBMISSION);
		if (ref == null) {
			return 0;
		}
	    return ref.getNumber();
    }
	
	@Override
    public int[] getAllSubmissionNumbers(OTUserObject user, OTObject object)
        throws Exception
    {
		ArrayList<OTOverlayReference> refs = findAllReferences(user, object);
		if (refs == null || refs.size() < 1) {
			return new int[] { };
		}
		int[] nums = new int[refs.size()];
		for (int i = 0; i < refs.size(); i++) {
			nums[i] = refs.get(i).getNumber();
		}
	    return nums;
    }
}
