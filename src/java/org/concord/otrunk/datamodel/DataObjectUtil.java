/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2006-05-05 16:00:32 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel;

import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;

public class DataObjectUtil
{
    /**
     * This does a shallow copy.  It copys the collection objects but 
     * all other resources are just added as references not copied. 
     * @param original
     * @param copy
     */
    public static void copyInto(OTDataObject original, OTDataObject copy)
    {
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
                    copyList.add(list.get(j));
                }
            } else if(resource instanceof OTResourceMap){
                OTResourceMap copyMap =
                    (OTResourceMap)copy.getResourceCollection(keys[i], 
                            OTResourceMap.class);                    
                OTResourceMap map = (OTResourceMap)resource;
                copyMap.removeAll();
                String [] mapKeys = map.getKeys();
                for(int j=0; j<mapKeys.length; j++){
                    copyMap.put(mapKeys[j], map.get(mapKeys[j]));
                }
            } else {
                copy.setResource(keys[i], resource);
            }
        }

    }
}
