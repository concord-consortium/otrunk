/**
 * 
 */
package org.concord.otrunk.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTXMLString;
import org.concord.otrunk.overlay.CompositeDataObject;

/**
 * @author scott
 *
 */
public class Copier 
{
	OTDatabase destinationDb;
	OTDatabase sourceDb;
	OTDataObject root;
	
	ArrayList<CopyEntry> toBeCopied;
	ArrayList<CopyEntry> recurseOnly;
	private OTDataList orphanList;
	private OTExternalIDProvider idProvider;
	private OTDataObjectFinder dataObjectFinder;
	private boolean onlyModifications;
	private static final Logger logger = Logger.getLogger(Copier.class.getCanonicalName());

    private static class CopyEntry
    {    	
    	OTDataObject original;
    	int maxDepth;
    	OTDataObject copy;
    	
    	public CopyEntry(OTDataObject original, int maxDepth, OTDataObject copy)
    	{
    		this.original = original;
    		this.maxDepth = maxDepth;
    		this.copy = copy;
    	}    	
    }
	
	public Copier(OTDatabase sourceDb, OTDatabase destinationDb, OTDataList orphanDataList, 
		OTExternalIDProvider idProvider, OTDataObjectFinder dataObjectFinder, boolean onlyModifications)
	{
		this.destinationDb = destinationDb;
		this.sourceDb = sourceDb;
		this.toBeCopied = new ArrayList<CopyEntry>();
		this.recurseOnly = new ArrayList<CopyEntry>();
		this.orphanList = orphanDataList;
		this.idProvider = idProvider;
		this.dataObjectFinder = dataObjectFinder;
		this.onlyModifications = onlyModifications;
	}
	
	/**
	 * This returns any object in the toBeCopiedList which has the same
	 * otid as the one we're passing in. If idProvider == null, this may
	 * fail to return an object because it will be comparing a globalid to
	 * a transient id.
	 * 
	 * @param originalId
	 * @return
	 */
    private CopyEntry getCopyEntry(OTID originalId)
    {
    	for(int i=0; i<toBeCopied.size(); i++){
    		CopyEntry entry = toBeCopied.get(i);
    		String entryObjectExternalID;
    		String originalExternalID;
    		
    		if (idProvider != null){
        		entryObjectExternalID = idProvider.getExternalID(entry.original.getGlobalId());
        		originalExternalID = idProvider.getExternalID(originalId);
    		} else {
    			entryObjectExternalID = entry.original.getGlobalId().toString();
    			originalExternalID = originalId.toString();
    		}
    		
    		if(entryObjectExternalID.equals(originalExternalID)){
    			return entry;
    		}
    	}
    	return null;
    }
    
     private Object handleChild(Object child, int maxDepth) 
    throws Exception
    {
		// check if the value is an OTID
		// and if we have more depth to go
		if(child instanceof OTID && (maxDepth == -1 || maxDepth > 0)){
			// check if this id has already been seen
			CopyEntry itemCopyEntry = getCopyEntry((OTID)child);
			
			if(itemCopyEntry == null){
				OTDataObject itemObj = 
					sourceDb.getOTDataObject(root, (OTID)child);
				if(itemObj == null){
					itemObj = dataObjectFinder.findDataObject((OTID)child);
					if(itemObj == null) {
						throw new IllegalStateException("Can't find child id: " + child);
					}
				}
				
				int copyMaxDepth = -1;
				
				if(maxDepth != -1){
					copyMaxDepth = maxDepth-1; 
				}
				
				if (onlyModifications) {
    				OTID itemID = itemObj.getGlobalId();
    				if (itemID instanceof OTTransientMapID) {
    					itemID = ((OTTransientMapID)itemID).getMappedId();
    				}
    				
    				if (itemObj instanceof CompositeDataObject) {
    					CompositeDataObject compositeObj = (CompositeDataObject) itemObj;
    					OTDataObject itemCopy;
    					boolean onlyRecurse = false;
    					if (! compositeObj.isModified()) {
        					logger.finer("Not modified: only a reference");
        					// don't copy the object if it's not modified and onlyModifications is true
        					// instead, include an object reference to the original
        					itemCopy = destinationDb.getOTDataObject(root, itemID);
        					onlyRecurse = true;
    					} else if ( compositeObj.getActiveDeltaObject() != null) {
    						logger.finer("Modified: changed copy");
    						// if the object has an active delta object, then it's only modified (not newly created) so if we request the data object from the destination db, it will handle creating the deltamap for us
    						itemCopy = destinationDb.getOTDataObject(root, itemID);
    					} else {
    						logger.finer("Modified: full copy");
    						// the object is newly created *or* we're forcing a full copy
    						itemCopy = destinationDb.createDataObject(itemObj.getType());
    					}
    					itemCopyEntry = new CopyEntry(itemObj, copyMaxDepth, itemCopy);
        				toBeCopied.add(itemCopyEntry);
        				if (onlyRecurse) {
        					recurseOnly.add(itemCopyEntry);
        				}
    				} else {
    					OTDataObject itemCopy =  destinationDb.createDataObject(itemObj.getType()); 
    					itemCopyEntry = new CopyEntry(itemObj, copyMaxDepth, itemCopy); 
    					toBeCopied.add(itemCopyEntry); 
    				}
    				
				} else {
					OTDataObject itemCopy =  destinationDb.createDataObject(itemObj.getType()); 
					itemCopyEntry = new CopyEntry(itemObj, copyMaxDepth, itemCopy); 
					toBeCopied.add(itemCopyEntry); 
				}
			} else {
				// we should not return here, it should just use the copy's global id
			}

			// put this new list object 
			child = itemCopyEntry.copy.getGlobalId();
		}
		
		return child;
    }
    
    public static void copyInto(OTDataObject source, OTDataObject dest,
	    OTDataList orphanDataList, int maxDepth, OTExternalIDProvider idProvider, OTDataObjectFinder dataObjectFinder, boolean onlyModifications)
	    throws Exception
	{
		Copier copier =
		    new Copier(source.getDatabase(), dest.getDatabase(), orphanDataList, idProvider, dataObjectFinder, onlyModifications);
		copier.internalCopyInto(source, dest, maxDepth);
	}
    
    public void internalCopyInto(OTDataObject source, OTDataObject dest,
    		int maxDepth) throws Exception
    {
    	toBeCopied.add(new CopyEntry(source, maxDepth, dest));
    	OTDatabase otDb = dest.getDatabase();
    	
    	int currentIndex = 0;
    	while(currentIndex < toBeCopied.size()){
    		CopyEntry entry = toBeCopied.get(currentIndex);
    		boolean onlyRecurse = recurseOnly.contains(entry);
    		OTDataObject original = entry.original; 
    	//	logger.fine(original+" ; "+original.getType());
    		OTDataObject copy = entry.copy;
    		if(copy == null) {
    			copy = otDb.createDataObject(original.getType());
    		}
    		
    		String [] keys = original.getResourceKeys();
    		
    		// This is used to handle a hack for strings which reference objects
    		ArrayList<String> secondPassKeys = new ArrayList<String>();
    		
    		logger.fine("Processing object: " + original.getGlobalId());
    		
    		for(int i=0; i<keys.length; i++){
    			logger.fine("Processing attribute: " + keys[i]);
    			Object resource = original.getResource(keys[i]);
    			boolean keyModified = false;
    			if (onlyModifications) {
        			if (original instanceof CompositeDataObject) {
        				if (((CompositeDataObject) original).isModified()) {
            				OTDataObject deltaObj = ((CompositeDataObject) original).getActiveDeltaObject();
            				if (deltaObj != null && deltaObj.containsKey(keys[i])) {
            					logger.finer("Delta object contains key!");
            					keyModified = true;
            				} else if (deltaObj == null) {
            					// composite data object without an active delta object means it was newly created
            					logger.finer("Original is newly created! Key counts as modified!");
            					keyModified = true;
            				}
            			}
        			}
    			}

    			if(resource instanceof OTDataList){
    				OTDataList list = (OTDataList)resource;
    				
    				// if onlyModifications is set, only copy the list if something has changed
    				if (onlyModifications) {
    					boolean modifiedList = false;
    					ArrayList<Object> tempList = new ArrayList<Object>();
    					for(int j=0; j<list.size(); j++){
        					Object listItem = list.get(j);
        					listItem = handleChild(listItem, entry.maxDepth);
        					tempList.add(listItem);
        					if (listItem instanceof CompositeDataObject && ((CompositeDataObject) listItem).isModified()) {
        						modifiedList = true;
        					}
    					}
    					logger.finer("List is modified: " + (modifiedList || keyModified));
    					if (modifiedList || keyModified) {
    						// the list was modified so copy all the listItems
    						OTDataList copyList =
    	    					copy.getResourceCollection(keys[i], 
    	                    		OTDataList.class);
    	    				
    	    				copyList.removeAll();
    	    				for(Object o : tempList){
    	    					copyList.add(o);
    	    				}
    					}
    				} else {
        				OTDataList copyList =
        					copy.getResourceCollection(keys[i], 
                        		OTDataList.class);
        				
        				copyList.removeAll();
        				for(int j=0; j<list.size(); j++){
        					Object listItem = list.get(j);
        					
        					listItem = handleChild(listItem, entry.maxDepth);
        					copyList.add(listItem);
        				}
    				}
    			} else if(resource instanceof OTDataMap){
    				// if onlyModifications is set, only process the map if something has changed
    				OTDataMap map = (OTDataMap)resource;
    				String [] mapKeys = map.getKeys();
    				if (onlyModifications) {
    					boolean modifiedMap = false;
    					HashMap<String, Object> tempMap = new HashMap<String, Object>();
    					for(int j=0; j<mapKeys.length; j++){
        					Object item = map.get(mapKeys[j]);
        					item = handleChild(item, entry.maxDepth);
        					tempMap.put(mapKeys[j], item);
        					if (item instanceof CompositeDataObject && ((CompositeDataObject) item).isModified()) {
        						modifiedMap = true;
        					}
    					}
    					logger.finer("Map is modified: " + (modifiedMap || keyModified));
    					if (modifiedMap || keyModified) {
    						// the map was modified so copy all the listItems
    						OTDataMap copyMap =
    	    					copy.getResourceCollection(keys[i], 
    	    						OTDataMap.class);
    	    				
    						copyMap.removeAll();
    	    				for(Entry<String, Object> o : tempMap.entrySet()){
    	    					copyMap.put(o.getKey(), o.getValue());
    	    				}
    					}
    				} else {
        				OTDataMap copyMap =
        					copy.getResourceCollection(keys[i], 
                        		OTDataMap.class);                    
        				copyMap.removeAll();
        				
        				for(int j=0; j<mapKeys.length; j++){
        					Object item = map.get(mapKeys[j]);
        					item = handleChild(item, entry.maxDepth);
        					copyMap.put(mapKeys[j], item);
        				}
    				}
    			} else if(resource instanceof OTXMLString) {
    				if (onlyModifications) {
        				if ((! onlyRecurse) && keyModified) {
        					secondPassKeys.add(keys[i]);
        				}
    				} else {
    					secondPassKeys.add(keys[i]);
    				}
    			} else {
    				resource = handleChild(resource, entry.maxDepth);
    				if (! onlyRecurse) {
    					copy.setResource(keys[i], resource);
    				}
    			}
    		}
    		
    		for(int i=0; i<secondPassKeys.size(); i++){
    			logger.finer("loop secondpass");
    			String key = secondPassKeys.get(i);
    			Object resource = original.getResource(key);

    			logger.finer("text copying " + key);

    			// This is a hack, if we had namespaces it wouldn't be as
				// much of a hack.
				OTXMLString xmlString = (OTXMLString) resource;
				
				// search for the refid="" in the string and copy all the 
				// referenced objects.
				String copiedString = updateXMLString("refid", xmlString.getContent(), otDb, key, entry);
				
				// search for the refid="" in the string and copy all the 
				// referenced objects.
				copiedString = updateXMLString("href", copiedString, otDb, key, entry);
				    	
				OTXMLString copiedXmlString = new OTXMLString(copiedString);
				copy.setResource(key, copiedXmlString);

    		}
    		currentIndex++;
    	}
    }

    public String updateXMLString(String attributeName, String xmlStringContent, OTDatabase otDb,
    	String key, CopyEntry entry) throws Exception
    {
    	OTDataObject original = entry.original;
		Pattern pattern = Pattern.compile(attributeName + "=\"([^\"]*)\"");
		Matcher matcher = pattern.matcher(xmlStringContent);
		StringBuffer copiedStringBuf = new StringBuffer();
		while(matcher.find()){
			String otidStr = matcher.group(1);

			// FIXME this is hack to handle external urls
			if("href".equals(attributeName) && 
					(otidStr.startsWith("http")
						|| otidStr.startsWith("file"))){
				matcher.appendReplacement(copiedStringBuf, "$0");
				continue;
			}

			// create an OTID from this id
			OTID otid = OTIDFactory.createOTID(otidStr);
			
			// check if this id has aready been copied
			CopyEntry copyEntry = getCopyEntry(otid);
			
			OTID copiedId = null;
			if(copyEntry != null){
				copiedId = copyEntry.copy.getGlobalId();
			} else {
				// If we are here it is because we didn't already store this 
				// copied object somewhere.  
				
				// We should first check if the otid is a valid id, if not then it is
				// an invalid match
				OTDataObject matchedDataObject = dataObjectFinder.findDataObject(otid);
				if(matchedDataObject == null){
					// this id isn't in our database or it is invalid
					// print a warning and continue
					logger.warning("Can't find object to copy: " + otid + " skipping object");
					
					// put the same string back in. 
					matcher.appendReplacement(copiedStringBuf, "$0");
					
					continue;
					
				}

				
				// If we have an orphanList then the object can be stored there
				// if we not then print a warning message and use the original
				// object.
				
				
				
				if(orphanList == null){
					String msg = "Cannot copy objects referenced from " +
					"xml strings, that are not stored somewhere else";
					msg += "\n" + "  original object: " + otid;
					msg += "\n" + "  object referencing it: " + original;
					msg += "\n" + "  string property: " + key;
					logger.warning(msg);
					
					// Just use the original id
					copiedId = otid;							
				} else {
					copiedId = (OTID)handleChild(otid, entry.maxDepth);							
					orphanList.add(copiedId);
				}						
			}
								
			// replace the id with the new id
			String copiedExternalId;
			if (idProvider != null){
				copiedExternalId = idProvider.getExternalID(copiedId);
    		} else {
    			copiedExternalId = copiedId.toExternalForm();
    		}
			matcher.appendReplacement(copiedStringBuf, 
				  attributeName + "=\"" + copiedExternalId + "\"");
			
			// now the next problem is where to store this copied object
			// if it was already handled we don't need to figure out 
			// where to store it.  If we had a record of the containment
			// we could use that figure out where to store this copy
		}
		matcher.appendTail(copiedStringBuf);

		return copiedStringBuf.toString();
    }
    
}
