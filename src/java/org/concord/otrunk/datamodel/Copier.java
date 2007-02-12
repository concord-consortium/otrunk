/**
 * 
 */
package org.concord.otrunk.datamodel;

import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;

/**
 * @author scott
 *
 */
public class Copier 
{
	OTDatabase otDb;
	OTDataObject root;
	
	Vector toBeCopied;

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
    
	public Copier(OTDatabase otDb)
	{
		this.otDb = otDb;
		this.toBeCopied = new Vector();
	}
	
    private boolean contains(OTID id)
    {
    	for(int i=0; i<toBeCopied.size(); i++){
    		CopyEntry entry = (CopyEntry)toBeCopied.get(i);
    		if(entry.original.getGlobalId().equals(id)){
    			return true;
    		}
    	}
    	return false;
    }
    
    private Object handleChild(Object child, int maxDepth) 
    throws Exception
    {
		// check if the value is an OTID
		// and if we have more depth to go
		if(child instanceof OTID && (maxDepth == -1 || maxDepth > 0)){
			// check if this id has already been seen 
			if(!contains((OTID)child)){
				OTDataObject itemObj = 
					otDb.getOTDataObject(root, (OTID)child);
				OTDataObject itemCopy = 
					otDb.createDataObject();
				int copyMaxDepth = -1;
				if(maxDepth != -1){
					copyMaxDepth = maxDepth-1; 
				}
				CopyEntry itemCopyEntry = 
					new CopyEntry(itemObj, copyMaxDepth, 
							itemCopy);
				toBeCopied.add(itemCopyEntry);
				
				// put this new list object 
				child = itemCopy.getGlobalId();
			}
		}
		
		return child;
    }
    
    public static void copyInto(OTDataObject source, OTDataObject dest,
    		int maxDepth) 
    throws Exception
    {
    	Copier copier = new Copier(dest.getDatabase());
    	copier.internalCopyInto(source, dest, maxDepth);    	
    }
    
    public void internalCopyInto(OTDataObject source, OTDataObject dest,
    		int maxDepth) throws Exception
    {
    	toBeCopied.add(new CopyEntry(source, maxDepth, dest));
    	OTDatabase otDb = dest.getDatabase();
    	
    	int currentIndex = 0;
    	while(currentIndex < toBeCopied.size()){
    		CopyEntry entry = (CopyEntry)toBeCopied.get(currentIndex);
    		OTDataObject original = entry.original; 
    		OTDataObject copy = entry.copy;
    		if(copy == null) {
    			copy = otDb.createDataObject();
    		}
    		
    		String [] keys = original.getResourceKeys();
    		for(int i=0; i<keys.length; i++){
    			Object resource = original.getResource(keys[i]);

    			if(resource instanceof OTResourceList){
    				OTResourceList copyList =
    					(OTResourceList)copy.getResourceCollection(keys[i], 
    							OTResourceList.class);
    				OTResourceList list = (OTResourceList)resource;
    				copyList.removeAll();
    				for(int j=0; j<list.size(); j++){
    					Object listItem = list.get(j);
    					
    					listItem = handleChild(listItem, entry.maxDepth);
    					copyList.add(listItem);
    				}
    			} else if(resource instanceof OTResourceMap){
    				OTResourceMap copyMap =
    					(OTResourceMap)copy.getResourceCollection(keys[i], 
    							OTResourceMap.class);                    
    				OTResourceMap map = (OTResourceMap)resource;
    				copyMap.removeAll();
    				String [] mapKeys = map.getKeys();
    				for(int j=0; j<mapKeys.length; j++){
    					Object item = map.get(mapKeys[j]);
    					item = handleChild(item, entry.maxDepth);    					
    					copyMap.put(mapKeys[j], item);
    				}
    			} else {
    				resource = handleChild(resource, entry.maxDepth);    
    				copy.setResource(keys[i], resource);
    			}
    		}
    		currentIndex++;
    	}
    }

}
