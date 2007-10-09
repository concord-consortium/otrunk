/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
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
 * END LICENSE */

/*
 * Last modification information:
 * $Revision: 1.23 $
 * $Date: 2007-10-09 22:20:26 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTXMLString;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.BlobResource;
import org.concord.otrunk.datamodel.OTDataList;
import org.concord.otrunk.datamodel.OTDataMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.datamodel.OTDatabase;


/**
 * Exporter
 * Class name and description
 *
 * Date created: Nov 17, 2004
 *
 * @deprecated ExporterJDOM should be used instead.  This class does not handle ids and null objects correctly.
 * @author scott<p>
 *
 */
public class Exporter
{
	static OTDatabase otDb;
	static Vector writtenIds = null;
	static ArrayList writtenClasses = null;
	
	public static void export(File outputFile, OTDataObject rootObject, OTDatabase db)
	throws Exception
	{
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		
		export(outputStream, rootObject, db);
	}

	public static void export(OutputStream outputStream, OTDataObject rootObject, OTDatabase db)
	throws Exception
	{		
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		export(writer, rootObject, db);
	}
	
	public static void export(Writer writer, OTDataObject rootObject, OTDatabase db)
	throws Exception
	{	
		writtenIds = new Vector();
		writtenClasses = new ArrayList();
		
		// If this is a XMLDatabase pre-populate the written classes with the databases existing classes.
		// This preserves any imported classes that might not have been actually used in the otml file
		// these imported classes are currently the only way to load in packages, so they need to be preserved.
		if(db instanceof XMLDatabase){
			ArrayList importedClasses = ((XMLDatabase)db).getImportedOTObjectClasses();
			writtenClasses.addAll(importedClasses);
		}
		
		PrintWriter printStream = new PrintWriter(writer);
	
		otDb = db;
		
		StringWriter objectWriter = new StringWriter();
		PrintWriter objectPrintWriter = new PrintWriter(objectWriter);
		exportObject(objectPrintWriter, rootObject, 2);

		String objectString = objectWriter.toString();
				
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
	
	public static void printIndent(int indent, PrintWriter output)
	{
		String start = "";
		for(int i=0; i<indent; i++) {
			start += "  ";
		}
		output.print(start);
	}
	
	public static void indentPrint(int indent, String str, PrintWriter output)
	{
	    printIndent(indent, output);
	    
		output.println(str);
	}
	
	public static void exportCollectionItem(OTDataObject parentDataObj, 
			PrintWriter output, Object item, int indent)
	throws Exception
	{
		if(item instanceof OTID) {
			// this is an object reference
			// recurse
            exportID(output, parentDataObj, (OTID)item, indent);
		} else if(item instanceof OTDataList  ||
				item instanceof OTDataMap) {
			System.err.println("nested collections are illegal");
		} else {
			// this is a litteral reference in a list or map so we need the type
			String type = null;
			type = TypeService.getDataPrimitiveType(item.getClass());

			if(type == null){
				System.err.println("unknown list item type: " + item.getClass());				
				return;
			}
			printIndent(indent, output);
			output.print("<" + type + ">");

			String itemString;
			if(!(item instanceof BlobResource)) {
				itemString = item.toString();
			} else {
				BlobResource blob = (BlobResource)item;
				if(blob.getBlobURL() != null){
					itemString = blob.getBlobURL().toExternalForm(); 
				} else {
					itemString = BlobTypeHandler.base64(blob.getBytes());
				}
			}
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

    public static void exportID(PrintWriter output, OTDataObject parent, 
            OTID id, int indent)
    throws Exception
    {
        OTDataObject childObject = otDb.getOTDataObject(parent, id);
        if(childObject == null) {
            // our db doesn't contain this object
            // so write out a reference to it and hope that we can find 
            // it when we are loaded in.  
            // FIXME: This should be a little more careful.  The list of
            // databases we require should be saved.  So then we can check
            // if this external object will be resolvable on loading.
            indentPrint(indent, 
                    "<object" + " refid=\"" + id + "\"/>", output);
        } else {
            exportObject(output, childObject, indent);
        }
    }
    
	public static void exportObject(PrintWriter output, OTDataObject dataObj, int indent)
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
		
		String objectClass = OTrunkImpl.getClassName(dataObj);
		if(!writtenClasses.contains(objectClass)) {
			writtenClasses.add(objectClass);
		}
		
		int objectIndent = indent;
		String objectElement =  objectClass;
		if(objectElement == null) {
		    objectElement = "object";
		}
		indentPrint(objectIndent, "<" + objectElement + 
		        " id=\"" + id + "\">", output);
		indent++;
		String resourceKeys [] = dataObj.getResourceKeys();
		int resourceIndent = indent;
		
		
		for(int i=0; i<resourceKeys.length; i++) {
		    
		    // FIXME: we are ignoring special keys there should way
		    // to identify special keys.
			if(		resourceKeys[i].equals("currentRevision") ||
					resourceKeys[i].equals("localId")) {
				continue;
			}

			String resourceName = resourceKeys[i];
			Object resource = dataObj.getResource(resourceName);
			
			printIndent(resourceIndent, output);
			output.print("<" + resourceName + ">");
			if(resource instanceof OTID) {
				// this is an object reference
				// recurse
			    output.println();
                exportID(output, dataObj, (OTID)resource, resourceIndent+1);
				printIndent(resourceIndent, output);
			} else if(resource instanceof OTDataList) {
			    output.println();
			    OTDataList list = (OTDataList)resource;
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
			} else if(resource instanceof OTDataMap) {
			    output.println();
			    OTDataMap map = (OTDataMap)resource;
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
			} else if(resource instanceof BlobResource){
				BlobResource blob = (BlobResource) resource;
				Object blobUrl = blob.getBlobURL();
				if(blobUrl != null){
					output.print(blobUrl);
				} else {
					String base64Str = 
						BlobTypeHandler.base64(blob.getBytes());
					output.println();
					indentPrint(resourceIndent+1, base64Str, output);
					printIndent(resourceIndent, output);
				}
			} else if(resource == null){
			    System.err.println("Got null resource value");
			} else if(resource instanceof Integer ||
			        resource instanceof Float ||
			        resource instanceof Byte ||
			        resource instanceof Short ||
			        resource instanceof Boolean) {
			    output.print(resource.toString());
			} else if(resource instanceof OTXMLString) {
			    output.println();
				indentPrint(resourceIndent+1, 
				        ((OTXMLString)resource).getContent(), output);
				printIndent(resourceIndent, output);			    
			} else if(resource instanceof String && 
			        ((String)resource).length() < 30) {
			    // escape xml characters
			    String text = resource.toString();
			    String escapedText = escapeElementText(text);
			    output.print(escapedText);			    
			} else {
			    output.println();
			    // escape xml characters
			    String text = resource.toString();
			    String escapedText = escapeElementText(text);
				indentPrint(resourceIndent+1, escapedText, output);
				printIndent(resourceIndent, output);
			}
			output.println("</" + resourceKeys[i] + ">");
		}
		
		indentPrint(objectIndent, "</" + objectElement + ">", output);
	}
	
	public static String escapeElementText(String text)
	{
	    String newText = text.replaceAll("&", "&amp;");
	    newText = newText.replaceAll("<", "&lt;");
	    newText = newText.replaceAll(">", "&gt;");

	    return newText;
	}
}
