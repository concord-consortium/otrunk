/*
 * Last modification information:
 * $Revision: 1.7 $
 * $Date: 2005-03-31 21:07:26 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;


/**
 * Exporter
 * Class name and description
 *
 * Date created: Nov 17, 2004
 *
 * @author scott<p>
 *
 */
public class Exporter
{
	static OTDatabase otDb;
	static Vector writtenIds = null;
	static Vector writtenClasses = null;
	
	public static void export(File outputFile, OTDataObject rootObject, OTDatabase db)
	throws Exception
	{
		writtenIds = new Vector();
		writtenClasses = new Vector();
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		PrintStream printStream = new PrintStream(outputStream);
	
		otDb = db;
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		PrintStream objectPrintStream = new PrintStream(byteStream);
		exportObject(objectPrintStream, rootObject, 2);

		String objectString = byteStream.toString();
		
		printStream.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		printStream.println("<otrunk>");
		indentPrint(1, "<imports>", printStream);
		for(int i=0; i<writtenClasses.size(); i++) {
			indentPrint(2, "<import class=\"" + writtenClasses.get(i) + "\"/>", printStream);
		}
		
		indentPrint(1, "</imports>", printStream);
		
		indentPrint(1, "<objects>", printStream);
		indentPrint(0, objectString, printStream);
		indentPrint(1, "</objects>", printStream);
		printStream.println("</otrunk>");
		
		printStream.close();
		// write out the root data object using its otObjectClass name to find
		// its type
		
		// For each id-object resource look up its object and write it out, recording
		// the ids that have been written out before.
		
		// Can do this writting directly to the file, but then we'll need to track some
		// global indentation lets try that.
		
		// lets try recursion to start
		
	}
	
	public static void printIndent(int indent, PrintStream output)
	{
		String start = "";
		for(int i=0; i<indent; i++) {
			start += "  ";
		}
		output.print(start);
	}
	
	public static void indentPrint(int indent, String str, PrintStream output)
	{
	    printIndent(indent, output);
	    
		output.println(str);
	}
	
	public static void exportCollectionItem(OTDataObject parentDataObj, 
			PrintStream output, Object item, int indent)
	throws Exception
	{
		if(item instanceof OTID) {
			// this is an object reference
			// recurse
			OTDataObject childObject = otDb.getOTDataObject(parentDataObj, (OTID)item);
			exportObject(output, childObject, indent);
		} else if(item instanceof OTResourceList  ||
				item instanceof OTResourceMap) {
			System.err.println("nested collections are illegal");
		} else if(item instanceof byte []) {
			// this is blob
			// write out the blob and save a reference to it
			System.err.println("Got a blob reference????");
		} else {
			// this is a litteral reference in a list or map so we need the type
			String type = null;
			if(item instanceof String) {
				type = "string";
			} else if(item instanceof Float) {
				type = "float";
			} else if(item instanceof Boolean) {
				type = "boolean";
			} else {
				System.err.println("unknown list item type: " + item.getClass());
			}
			printIndent(indent, output);
			output.print("<" + type + ">");
			String itemString = item.toString();
			if(itemString.length() > 40) {
			    output.println();
			    indentPrint(indent+1,itemString, output);
			    printIndent(indent, output);
			} else {
			    output.print(itemString);
			}
			output.println("</" + type + ">");
		}
	}

	public static void exportObject(PrintStream output, OTDataObject dataObj, int indent)
	throws Exception
	{
		OTID id = dataObj.getGlobalId();
		if(writtenIds.contains(id)) {
			// we've seen this object for so just write a reference
			indentPrint(indent, 
					"<object" + " refid=\"" + id + "\"/>", output);
			return;
		} else {
			writtenIds.add(id);
		}
		// System.err.println("writting object: " + id);		
		
		String objectClass = (String)dataObj.getResource(OTrunkImpl.RES_CLASS_NAME);
		if(!writtenClasses.contains(objectClass)) {
			writtenClasses.add(objectClass);
		}
		
		int objectIndent = indent;
		String objectElement =  objectClass;
		if(objectElement == null) {
		    objectElement = "object";
		}
		objectElement += " id=\"" + id + "\"";
		indentPrint(objectIndent, "<" + objectElement + ">", output);
		indent++;
		String resourceKeys [] = dataObj.getResourceKeys();
		int resourceIndent = indent;
		
		
		for(int i=0; i<resourceKeys.length; i++) {
		    
		    // FIXME: we are ignoring special keys there should way
		    // to identify special keys.
			if(resourceKeys[i].equals(OTrunkImpl.RES_CLASS_NAME) ||
					resourceKeys[i].equals("currentRevision") ||
					resourceKeys[i].equals("localId")) {
				continue;
			}

			Object resource = null;
			if(dataObj instanceof XMLDataObject) {
			    XMLDataObject xmlDataObj = (XMLDataObject)dataObj;
			    if(xmlDataObj.isBlobResource(resourceKeys[i])){
			        XMLBlobResource blob = xmlDataObj.getBlobResource(resourceKeys[i]);
			        resource = blob.getBlobURL();
			    }			    
			} 
			
			if(resource == null) {
			    resource = dataObj.getResource(resourceKeys[i]);
			}
			
			printIndent(resourceIndent, output);
			output.print("<" + resourceKeys[i] + ">");
			if(resource instanceof OTID) {
				// this is an object reference
				// recurse
			    output.println();
				OTDataObject childObject = otDb.getOTDataObject(dataObj, (OTID)resource);
				exportObject(output, childObject, resourceIndent+1);
				printIndent(resourceIndent, output);
			} else if(resource instanceof OTResourceList) {
			    output.println();
				OTResourceList list = (OTResourceList)resource;
				for(int j=0;j<list.size(); j++) {
					Object listElement = list.get(j);
					if(listElement == null) {
						System.err.println("null list item (allowed??)");
						continue;
					}
					exportCollectionItem(dataObj, output, 
							listElement, resourceIndent+1);
				}
				printIndent(resourceIndent, output);
			} else if(resource instanceof OTResourceMap) {
			    output.println();
			    OTResourceMap map = (OTResourceMap)resource;
			    String [] mapKeys = map.getKeys();
			    int entryIndent = resourceIndent+1;
			    for(int j=0; j<mapKeys.length; j++) {
			        indentPrint(entryIndent, "<entry key=\"" + mapKeys[j] + "\">", output);
			        Object mapValue = map.get(mapKeys[j]);
			        if(mapValue != null) {
			            exportCollectionItem(dataObj, output, mapValue, entryIndent+1);
			        }
			        indentPrint(entryIndent, "</entry>", output);			        
			    }
			    printIndent(resourceIndent, output);
			} else if(resource instanceof byte []) {
				// this is non xml blob reference
				// write out the blob and save a reference to it
				System.err.println("Got a non xml blob reference????");
			} else if(resource == null){
			    System.err.println("Got null resource value");
			} else if(resource instanceof Integer ||
			        resource instanceof Float ||
			        resource instanceof Byte ||
			        resource instanceof Short ||
			        resource instanceof Boolean) {
			    output.print(resource.toString());
			} else if(resource instanceof String && 
			        ((String)resource).length() < 30) {
			    output.print(resource.toString());			    
			} else {
			    output.println();
				indentPrint(resourceIndent+1, resource.toString(), output);
				printIndent(resourceIndent, output);
			}
			output.println("</" + resourceKeys[i] + ">");
		}
		
		indentPrint(objectIndent, "</" + objectClass + ">", output);
	}
	

}
