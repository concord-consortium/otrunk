/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-11-22 23:05:40 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTResourceCollection;
import org.concord.otrunk.datamodel.OTResourceList;
import org.concord.otrunk.datamodel.OTResourceMap;
import org.concord.otrunk.datamodel.fs.FsDataObject;
import org.concord.otrunk.datamodel.fs.FsResourceList;
import org.concord.otrunk.datamodel.fs.FsResourceMap;
import org.doomdark.uuid.EthernetAddress;
import org.doomdark.uuid.NativeInterfaces;
import org.doomdark.uuid.UUID;
import org.doomdark.uuid.UUIDGenerator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


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
	UUID rootId = null;
	
	Hashtable dataObjects = new Hashtable();
	Vector objectReferences = new Vector();
	
	// a map of xml file ids to UUIDs
	Hashtable localIdMap = new Hashtable();
	
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

		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(xmlStream);
		
		Element rootElement = document.getRootElement();
	
		Vector importedOTObjectClasses = new Vector();
		
		Element importsElement = rootElement.getChild("imports");
		List imports = importsElement.getChildren();		
		for(Iterator iterator=imports.iterator();iterator.hasNext();) {
			Element currentImport=(Element)iterator.next();
			String className = currentImport.getAttributeValue("class");
			importedOTObjectClasses.add(className);
		}		
		
		ReflectionTypeDefinitions.registerTypes(importedOTObjectClasses, typeService,
				this);		
		
		// Pass 1:
		// Load all the xml data objects in the file
		// This also makes a list of all these objects so we 
		// can handle them linearly in the next pass.
		Element objects = rootElement.getChild("objects");
		List xmlObjects = objects.getChildren();
		if(xmlObjects.size() != 1) {
			throw new Exception("Can only load files that contain a single root object");
		}

		Element rootObjectNode = (Element)xmlObjects.get(0);		
		
		// Recusively load all the data objects
		XMLDataObject rootDataObject = (XMLDataObject)typeService.handleLiteralElement(rootObjectNode);
		
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
	public void setRoot(UUID rootId)
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

	protected XMLDataObject createDataObject(Element element, String id)
		throws Exception
	{
		return createDataObject(element, new UUID(id));
	}
		
	protected XMLDataObject createDataObject(Element element, UUID id)
		throws Exception
	{
		if(id == null) {
			UUIDGenerator generator = UUIDGenerator.getInstance();
	    	EthernetAddress hwAddress = NativeInterfaces.getPrimaryInterface();
	    	id = generator.generateTimeBasedUUID(hwAddress);			
		}

    	XMLDataObject dataObject = new XMLDataObject(element, id);

    	Object oldValue = dataObjects.put(dataObject.getGlobalId(), dataObject);
    	if(oldValue != null) {
    		dataObjects.put(dataObject.getGlobalId(), oldValue);
    		throw new Exception("repeated unique id");
    	}

    	if(element != null) {
    		String localIdStr = element.getAttributeValue("local_id");
    		if(localIdStr != null && localIdStr.length() > 0) {
    			dataObject.setLocalId(localIdStr);
    			localIdMap.put(localIdStr, dataObject.getGlobalId());
    		}
    	} else {
    		System.err.println("null element in createDataObject");
    	}
    		
    	return dataObject;		
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.datamodel.OTDatabase#createDataObject()
	 */
	public OTDataObject createDataObject()
		throws Exception
	{
		return createDataObject(null, (UUID)null);
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.datamodel.OTDatabase#createDataObject(org.doomdark.uuid.UUID)
	 */
	public OTDataObject createDataObject(UUID id)
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
	public OTDataObject getOTDataObject(OTDataObject dataParent, UUID childID)
		throws Exception
	{
		// we are going to ignore the dataParent for now
		return (OTDataObject)dataObjects.get(childID);
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

			for(Iterator entriesIter = entries.iterator(); entriesIter.hasNext(); )  {
				Map.Entry resourceEntry = (Map.Entry)entriesIter.next();
				Object resourceValue = resourceEntry.getValue();
				Object newResourceValue = null;
				if(resourceValue instanceof XMLDataObject) {
					newResourceValue = getUUID((XMLDataObject)resourceValue);
					xmlDObj.setResource((String)resourceEntry.getKey(), newResourceValue);
				} else if(resourceValue instanceof XMLResourceList) {
					XMLResourceList oldList = (XMLResourceList)resourceValue;
					for(int j=0; j<oldList.size(); j++) {
						Object oldElement = oldList.get(j);
						if(oldElement instanceof XMLDataObject) {
							UUID newElement = getUUID((XMLDataObject)oldElement);
							oldList.set(j, newElement);
						} 
					}
					// the resource list value doesn't need to be updated
				} else if(resourceValue instanceof XMLResourceMap) {
					// FIXME should parse the map just like the list 
				} else if(resourceValue instanceof XMLParsableString) {
					// replace the local ids from the string
					newResourceValue = ((XMLParsableString)resourceValue).parse(localIdMap);
					xmlDObj.setResource((String)resourceEntry.getKey(), newResourceValue);
				} 					
			}			
		}			
	}

	public UUID getGlobalId(String idStr)
	{
		if(idStr.startsWith("${")) {
			String localId = idStr.substring(2,idStr.length()-1);
			UUID globalId = (UUID)localIdMap.get(localId);
			if(globalId == null) {
				System.err.println("Can't find local id: " + localId);
			}
			return globalId;
		} else {
			return new UUID(idStr);
		}		
	}
	
	public UUID getUUID(XMLDataObject xmlDObj)
	{
		if(xmlDObj instanceof XMLDataObjectRef) {
			String refId = ((XMLDataObjectRef)xmlDObj).getRefId();
			return getGlobalId(refId);
		}
		return xmlDObj.getGlobalId();		
	}	
	
}
