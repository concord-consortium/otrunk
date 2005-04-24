
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
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
 */

/*
 * Last modification information:
 * $Revision: 1.13 $
 * $Date: 2005-04-24 15:44:55 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceCollection;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.OTRelativeID;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTIDFactory;
import org.concord.otrunk.datamodel.OTUUID;
import org.concord.otrunk.xml.jdom.JDOMDocument;


/**
 * XMLDatabase
 * Class name and description
 *
 * Date created: Nov 19, 2004
 *
 * @author scott<p>
 *
 */
public class XMLDatabase
	implements OTDatabase
{
	OTID rootId = null;
	
	Hashtable dataObjects = new Hashtable();
	Vector objectReferences = new Vector();
	
	// a map of xml file ids to UUIDs
	Hashtable localIdMap = new Hashtable();
	
	public XMLDatabase()
	{
	    // create an empty database with no root
	}
	
	public XMLDatabase(File xmlFile)
		throws Exception
	{
		this(new FileInputStream(xmlFile), xmlFile.toURL());
	}
	
	public XMLDatabase(URL xmlURL)
		throws Exception
	{
		this(xmlURL.openStream(), xmlURL);
	}

	/**
	 * 
	 */
	public XMLDatabase(InputStream xmlStream, URL contextURL)
		throws Exception
	{
		// parse the xml file...
		TypeService typeService = new TypeService(contextURL);
		ObjectTypeHandler objectTypeHandler = new ObjectTypeHandler(typeService, this);
		typeService.registerUserType("object", objectTypeHandler);

		JDOMDocument document = new JDOMDocument(xmlStream);
		
		OTXMLElement rootElement = document.getRootElement();
				
		Vector importedOTObjectClasses = new Vector();
		
		OTXMLElement importsElement = rootElement.getChild("imports");
		List imports = importsElement.getChildren();		
		for(Iterator iterator=imports.iterator();iterator.hasNext();) {
		    OTXMLElement currentImport=(OTXMLElement)iterator.next();
			String className = currentImport.getAttributeValue("class");
			importedOTObjectClasses.add(className);
		}		
		
		ReflectionTypeDefinitions.registerTypes(importedOTObjectClasses, typeService,
				this);		
		
		// Pass 1:
		// Load all the xml data objects in the file
		// This also makes a list of all these objects so we 
		// can handle them linearly in the next pass.
		OTXMLElement objects = rootElement.getChild("objects");
		List xmlObjects = objects.getChildren();
		if(xmlObjects.size() != 1) {
			throw new Exception("Can only load files that contain a single root object");
		}

		OTXMLElement rootObjectNode = (OTXMLElement)xmlObjects.get(0);		
		
		// Recusively load all the data objects
		XMLDataObject rootDataObject = (XMLDataObject)typeService.handleLiteralElement(rootObjectNode, "anon_root");
		
		System.err.println("loaded all the objects");
		
		// Need to handle local_id this will be stored as XMLDataObjectRef with in the
		// tree. this is what the objectReferences vector is for
		// each references stores the source object and the key within that object
		// where the object should be stored.  
		secondPass();
		
		setRoot(rootDataObject.getGlobalId());
		
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.datamodel.OTDatabase#setRoot(org.doomdark.uuid.UUID)
	 */
	public void setRoot(OTID rootId)
		throws Exception
	{
		this.rootId = rootId;
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.datamodel.OTDatabase#getRoot()
	 */
	public OTDataObject getRoot()
		throws Exception
	{
		return (OTDataObject)dataObjects.get(rootId);
	}

	protected XMLDataObject createDataObject(OTXMLElement element, String idStr)
		throws Exception
	{
		OTID id = null;
		if(idStr != null) {
			id = OTIDFactory.createOTID(idStr);
		}
		return createDataObject(element, id); 
	}
		
	protected XMLDataObject createDataObject(OTXMLElement element, OTID id)
		throws Exception
	{
		if(id == null) {
//		    String path = TypeService.elementPath(element);
//		    id = new OTXMLPathID(path);
			id = OTUUID.createOTUUID();
		}

    	XMLDataObject dataObject = new XMLDataObject(element, id, this);

    	Object oldValue = dataObjects.put(dataObject.getGlobalId(), dataObject);
    	if(oldValue != null) {
    		dataObjects.put(dataObject.getGlobalId(), oldValue);
    		throw new Exception("repeated unique id");
    	}

    	if(element != null) {
    		String localIdStr = element.getAttributeValue("local_id");
    		if(localIdStr != null && localIdStr.length() > 0) {
    			dataObject.setLocalId(localIdStr);

    			// this is probably a temporary hack
    			dataObject.setResource("localId", localIdStr);
    			Object oldId = localIdMap.put(localIdStr, dataObject.getGlobalId());
    			if(oldId != null) {
    				System.err.println("repeated local id: " + localIdStr);
    			}
    		}
    	}
    		
    	return dataObject;		
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.datamodel.OTDatabase#createDataObject()
	 */
	public OTDataObject createDataObject()
		throws Exception
	{
		return createDataObject(null, (OTID)null);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.datamodel.OTDatabase#createDataObject(org.doomdark.uuid.UUID)
	 */
	public OTDataObject createDataObject(OTID id)
		throws Exception
	{
		return createDataObject(null, id);		
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.datamodel.OTDatabase#createCollection(org.concord.otrunk.datamodel.OTDataObject, java.lang.Class)
	 */
	public OTResourceCollection createCollection(OTDataObject parent,
			Class collectionClass)
		throws Exception
	{
		if(collectionClass.equals(OTResourceList.class)) {
			return new XMLResourceList();
		} else if(collectionClass.equals(OTResourceMap.class)) {
			return new XMLResourceMap();
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.datamodel.OTDatabase#getOTDataObject(org.concord.otrunk.datamodel.OTDataObject, org.doomdark.uuid.UUID)
	 */
	public OTDataObject getOTDataObject(OTDataObject dataParent, OTID childID)
		throws Exception
	{
		// we are going to ignore the dataParent for now
		return (OTDataObject)dataObjects.get(childID);
	}

	/* (non-Javadoc)
     * @see org.concord.otrunk.datamodel.OTDatabase#contains(org.concord.framework.otrunk.OTID)
     */
    public boolean contains(OTID id)
    {
        return dataObjects.containsKey(id);
    }
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.datamodel.OTDatabase#close()
	 */
	public void close()
	{
		// TODO Auto-generated method stub
		
		// resave the xml file maybe???
	}

	public void secondPass()
		throws Exception
	{	
		Collection objects = dataObjects.values();
		
		OTDataObject otDObj = null;
		
		for(Iterator iter = objects.iterator(); iter.hasNext();){
			XMLDataObject xmlDObj = (XMLDataObject)iter.next();
			if(xmlDObj instanceof XMLDataObjectRef) {
				throw new Exception("Found a reference in object list");
			}
						
			Collection entries = xmlDObj.getResourceEntries(); 
			Vector removedKeys = new Vector();
			
			for(Iterator entriesIter = entries.iterator(); entriesIter.hasNext(); )  {
				Map.Entry resourceEntry = (Map.Entry)entriesIter.next();
				Object resourceValue = resourceEntry.getValue();
				Object newResourceValue = null;
				String resourceKey = (String)resourceEntry.getKey();
				if(resourceValue instanceof XMLDataObject) {
					newResourceValue = getOTID((XMLDataObject)resourceValue);
					if(newResourceValue == null) {
					    removedKeys.add(resourceKey);
					} else {
					    xmlDObj.setResource(resourceKey, newResourceValue);
					}
				} else if(resourceValue instanceof XMLResourceList) {
					XMLResourceList list = (XMLResourceList)resourceValue;
					for(int j=0; j<list.size(); j++) {
						Object oldElement = list.get(j);
						if(oldElement instanceof XMLDataObject) {
							OTID newElement = getOTID((XMLDataObject)oldElement);
							list.set(j, newElement);
						}
						if(oldElement instanceof XMLParsableString) {
							newResourceValue = ((XMLParsableString)oldElement).parse(localIdMap);
							list.set(j, newResourceValue);							
						}
					}
					// the resource list value doesn't need to be updated
				} else if(resourceValue instanceof XMLResourceMap) {
					XMLResourceMap map = (XMLResourceMap)resourceValue;
					String [] keys = map.getKeys();
					for(int j=0; j<keys.length; j++) {
						Object oldElement = map.get(keys[j]);
						
						// Check if the key is a local id reference
						// if it is then replace it with the string
						// representation of this key
						if(keys[j].startsWith("${")){
							OTID globalId = getGlobalId(keys[j]);
							if(globalId != null) {
								map.remove(keys[j]);
								keys[j] = globalId.toString();
								map.put(keys[j], oldElement);
							}
						}

						if(oldElement instanceof XMLDataObject) {
							OTID newElement = getOTID((XMLDataObject)oldElement);
							map.put(keys[j], newElement);
						}
						if(oldElement instanceof XMLParsableString) {
							newResourceValue = ((XMLParsableString)oldElement).parse(localIdMap);
							map.put(keys[j], newResourceValue);							
						}
					}
				} else if(resourceValue instanceof XMLParsableString) {
					// replace the local ids from the string
					newResourceValue = ((XMLParsableString)resourceValue).parse(localIdMap);
					xmlDObj.setResource(resourceKey, newResourceValue);
				}				
			}	
			
			// remove the keys that have null values
			// this can't be done in the previous loop because that screws up the
			// the Iterator
			for(int keyIndex=0; keyIndex<removedKeys.size(); keyIndex++){
			    xmlDObj.setResource((String)removedKeys.get(keyIndex), null);
			}
		}			
	}

	public OTID getGlobalId(String idStr)
	{
		if(idStr.startsWith("${")) {
			String localId = idStr.substring(2,idStr.length()-1);
			OTID globalId = (OTID)localIdMap.get(localId);
			if(globalId == null) {
				System.err.println("Can't find local id: " + localId);
			}
			return globalId;
		} else {
			return OTIDFactory.createOTID(idStr); 
		}		
	}
	
	public OTID getOTID(XMLDataObject xmlDObj)
	{
		if(xmlDObj instanceof XMLDataObjectRef) {
			String refId = ((XMLDataObjectRef)xmlDObj).getRefId();
			return getGlobalId(refId);
		}
		return xmlDObj.getGlobalId();		
	}	
	
	public OTID getRelativeOTID(OTID parent, String relativePath)
	{
	    String xmlIdString = parent.toString() + "/" + relativePath;
	    return new OTRelativeID(xmlIdString);
	}
	
}
