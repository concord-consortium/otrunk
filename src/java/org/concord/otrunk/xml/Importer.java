/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2004-11-22 23:10:05 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.concord.otrunk.OTrunk;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTResourceList;
import org.concord.otrunk.datamodel.OTResourceMap;
import org.doomdark.uuid.UUID;


/**
 * Test
 * Class name and description
 *
 * Date created: Oct 4, 2004
 *
 * @author scott<p>
 *
 */
public class Importer
{
	public static Vector fileImport(File objectsFile, File importFile, 
			OTDatabase db, OTrunk otrunk)
		throws Exception
	{
		TypeService typeService = new TypeService(importFile.toURL());
		/**
		 * deprecated this class so I'm not fixing it yet
		 *
		ObjectTypeHandler objectTypeHandler = new ObjectTypeHandler(typeService);
		typeService.registerUserType("object", objectTypeHandler);
		

		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(importFile);
		
		Element rootElement = document.getRootElement();
	
		Vector importedOTObjectClasses = new Vector();
		
		Element importsElement = rootElement.getChild("imports");
		List imports = importsElement.getChildren();		
		for(Iterator iterator=imports.iterator();iterator.hasNext();) {
			Element currentImport=(Element)iterator.next();
			String className = currentImport.getAttributeValue("class");
			importedOTObjectClasses.add(className);
		}		
		
		ReflectionTypeDefinitions.registerTypes(importedOTObjectClasses, typeService);		
		
		// Pass 1:
		// Load all the xml data objects in the file
		// This also makes a list of all these objects so we 
		// can handle them linearly in the next pass.
		Element objects = rootElement.getChild("objects");
		List xmlObjects = objects.getChildren();
		Vector rootXmlDataObjects = new Vector();		
		for(Iterator iterator=xmlObjects.iterator();
			iterator.hasNext();) {
			Object current=iterator.next();
			Element node = (Element)current;
			Object object = typeService.handleLiteralElement(node);
			rootXmlDataObjects.add(object);
			System.out.println(object);
		}

		// Pass 2:
		// go through each object and create a data object
		// in the otrunk database.  Make a map of local ids to
		// global ids in the otrunk database.
		Vector xmlDataObjects = typeService.getDataObjects();
		Hashtable localIdMap = new Hashtable();
		Hashtable importMap = new Hashtable();
		
		for(int i=0; i<xmlDataObjects.size(); i++) {
			XMLDataObject xmlDObj = (XMLDataObject)xmlDataObjects.get(i);
			String localId = xmlDObj.getLocalId();

			OTDataObject otDObj = null;
			
			UUID globalId = xmlDObj.getGlobalId();
			if(globalId != null){
				otDObj = db.getOTDataObject(null, globalId);
				// TODO should compare some fundmental resources between the xml
				// element and this object.  For example if the object class changes
				// probably it should get a new ID.
				if(otDObj == null) {
					otDObj = db.createDataObject(globalId);
				}
			}
				
			if(otDObj == null) {			
				if(xmlDObj instanceof XMLDataObjectRef) {
					continue;
				}
				
				// todo real importing then the new object created should have
				// the same global id that it had before.
				otDObj = db.createDataObject();
				String globalIdStr = otDObj.getGlobalId().toString();
				xmlDObj.getElement().setAttribute(new Attribute("id", globalIdStr));
			}
			
			if(localId != null) {
				localIdMap.put(localId, otDObj.getGlobalId());
			}			
			
			importMap.put(xmlDObj, otDObj);
		}

		// Pass 3:
		// populate the values of the otrunk data objects with the 
		// values from the xml data objects.  This also resolves 
		// any local ids that have ben used in the xml data objects.
		Vector otDataObjects = new Vector();

		importObjects(xmlDataObjects, importMap, localIdMap, db);
		
		for(int i=0; i<rootXmlDataObjects.size(); i++) {
			XMLDataObject dObj = (XMLDataObject)(rootXmlDataObjects.get(i));

			OTDataObject importedDataObject = (OTDataObject)importMap.get(dObj); 
			OTObject importedObject = otrunk.getOTObject(importedDataObject.getGlobalId());
			otDataObjects.add(importedObject);			
		}
		
		XMLOutputter outputter = new XMLOutputter();
		FileOutputStream outStream = new FileOutputStream(importFile);
		outputter.output(document, outStream);
		outStream.close();
		
		
		return otDataObjects;
		*/
		return null;
	}
	
	public static void importObjects(Vector objects,
			Hashtable importMap, Hashtable localIdMap, 
			OTDatabase db)
		throws Exception
	{	
		OTDataObject otDObj = null;
		
		for(int i=0; i<objects.size(); i++) {
			XMLDataObject xmlDObj = (XMLDataObject)objects.get(i);
			if(xmlDObj instanceof XMLDataObjectRef) {
				// need to resolve the id of the reference
				// it could be local or global
				getUUID(importMap, localIdMap, db, xmlDObj);
				continue;
			}
			
			// TODO should check if the id of the object exists.
			otDObj = (OTDataObject)importMap.get(xmlDObj);
			
			Collection entries = xmlDObj.getResourceEntries(); 

			for(Iterator entriesIter = entries.iterator(); entriesIter.hasNext(); )  {
				Map.Entry resourceEntry = (Map.Entry)entriesIter.next();
				Object resourceValue = resourceEntry.getValue();
				Object newResourceValue = null;
				if(resourceValue instanceof XMLDataObject) {
					newResourceValue = getUUID(importMap, localIdMap, db,
							(XMLDataObject)resourceValue);
				} else if(resourceValue instanceof XMLResourceList) {
					XMLResourceList oldList = (XMLResourceList)resourceValue;
					OTResourceList newList = (OTResourceList)db.createCollection(otDObj, OTResourceList.class);
					for(int j=0; j<oldList.size(); j++) {
						Object oldElement = oldList.get(j);
						Object newElement = null;
						if(oldElement instanceof XMLDataObject) {
							newElement = getUUID(importMap, localIdMap, db,
									(XMLDataObject)oldElement);
						} else {
							newElement = oldElement;
						}
						newList.add(newElement);
					}
					newResourceValue = newList;
				} else if(resourceValue instanceof XMLResourceMap) {
					OTResourceMap newMap = (OTResourceMap)db.createCollection(otDObj, OTResourceMap.class);
					newResourceValue = newMap;
				} else if(resourceValue instanceof XMLParsableString) {
					System.out.println("got parsable string");
					newResourceValue = ((XMLParsableString)resourceValue).parse(localIdMap);
				} else {
					newResourceValue = resourceValue;
				}
				
				// TODO don't set the resource here
				// save the "new" resource in a hashtable
				// then after all the "new" resources have been set
				// go through the existing object and clear resources
				// that are no longer used.  And update ones that have
				// changed.
				otDObj.setResource((String)resourceEntry.getKey(), newResourceValue);
			}			
		}			
	}

	public static UUID getGlobalId(String idStr, Hashtable localIdMap)
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
	
	public static UUID getUUID(Hashtable importMap, Hashtable localIdMap,
			OTDatabase db, XMLDataObject xmlDObj)
	{
		OTDataObject newChildDO = (OTDataObject)importMap.get(xmlDObj);
		if(newChildDO == null) {
			// this is probably a reference
			if(xmlDObj instanceof XMLDataObjectRef) {
				String refId = ((XMLDataObjectRef)xmlDObj).getRefId();
				UUID globalId = getGlobalId(refId, localIdMap);
				try {
					newChildDO = db.getOTDataObject(null, globalId);
					
					if(newChildDO == null) {
						System.err.println("Can't find data object in existing database: " +
								globalId);
						// This could still be an object that hasn't been seen yet
						// in the import file
					}
					importMap.put(xmlDObj, newChildDO);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				
			}
		}
		return newChildDO.getGlobalId();		
	}	
}
