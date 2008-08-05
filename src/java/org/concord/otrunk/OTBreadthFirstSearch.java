package org.concord.otrunk;

import java.util.ArrayList;

import org.concord.framework.otrunk.OTCollection;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;

/**
 * OTBreadthFirstSearch <br>
 * This is not functional yet.
 * <p>
 * Date created: Aug 5, 2008
 * 
 * @author scytacki<p>
 *
 */
public class OTBreadthFirstSearch
{
	OTObject root;
	
	public OTBreadthFirstSearch(OTObject root)
    {	
		this.root = root;
    }
	
	/**
	 * This will not be efficient, but it good at least for illustration purposes.
	 * It also might not handle the bodyText of compound documents correctly.
	 * 
	 * @param klass
	 * @param root
	 * @return
	 */
	public OTObject findFirstBreadthFirst()
	{
		/*
		String className = klass.getName();
        OTClass otClass = OTrunkImpl.getOTClass(className);
        
        if(otClass == null){
        	return null;
        }
        */
		OTClass otClass = root.otClass();
		ArrayList otClassProperties = otClass.getOTAllClassProperties();
		
		for(int i=0; i<otClassProperties.size(); i++){
			OTClassProperty prop = (OTClassProperty) otClassProperties.get(i);
			if(prop.isPrimitive()){
				continue;
			}
			
			if(prop.isList() || prop.isMap()){
				OTCollection collection = (OTCollection) root.otGet(prop);
				if(collection == null || 
						collection instanceof OTResourceCollection ||
						collection.size() == 0){
					continue;
				}
				
				if(collection instanceof OTObjectList){
					OTObjectList list = (OTObjectList) collection;
					for(int j=0; j<list.size(); j++){
						OTObject child = list.get(j);
						
					}
				}
			}
		}

		throw new UnsupportedOperationException("This method isn't finished yet");
	}

}
