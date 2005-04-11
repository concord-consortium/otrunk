
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
 * $Revision: 1.3 $
 * $Date: 2005-04-11 15:01:08 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


/**
 * SchemaGenerator
 * Class name and description
 *
 * Date created: Dec 9, 2004
 *
 * @author scott<p>
 *
 */
public class SchemaGenerator
{
	public static void main(String args[])
		throws Exception
	{
		File xmlFile = new File(args[0]);
		FileInputStream xmlStream = new FileInputStream(xmlFile);
		
		URL contextURL = xmlFile.toURL();
		
		// parse the xml file...
		TypeService typeService = new TypeService(contextURL);
		ObjectTypeHandler objectTypeHandler = new ObjectTypeHandler(typeService, null);
		typeService.registerUserType("object", objectTypeHandler);

		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(xmlStream);
		
		Element rootElement = document.getRootElement();

		
		Vector importedOTObjectClasses = new Vector();
				
		Element importsElement = rootElement;
		List imports = importsElement.getChildren();		
		for(Iterator iterator=imports.iterator();iterator.hasNext();) {
			Element currentImport=(Element)iterator.next();
			String className = currentImport.getAttributeValue("class");
			importedOTObjectClasses.add(className);
		}		
		
		ReflectionTypeDefinitions.registerTypes(importedOTObjectClasses, typeService,
				null, false);	
		
		// the type service should now have all registers types so we can write out
		// the schema
		Hashtable typeMap = typeService.getHandlerMap();

		Vector printedResources = new Vector();
		
		Set entries = typeMap.entrySet();
		for(Iterator entryItr = entries.iterator(); entryItr.hasNext();) {
			Map.Entry entry = (Map.Entry)entryItr.next();
			if(entry.getValue() instanceof ObjectTypeHandler){
				if(entry.getKey().equals("object")) {
					continue;
				}

				printedResources.clear();
				
				System.out.println("" + entry.getKey());
				
				ObjectTypeHandler objectType = (ObjectTypeHandler)entry.getValue();
				ResourceDefinition [] resDefs = objectType.getResourceDefinitions();
				if(resDefs == null) {
					System.out.println("   no resource definitions ");
				}
				for(int i=0; i<resDefs.length; i++) {
					String resName = resDefs[i].name;
					if(printedResources.contains(resName)) {
						continue;
					}
					int numSpaces = 20 - resName.length();
					String spaces = "";
					for(int j=0; j<numSpaces; j++) spaces += " ";
					System.out.println("  " + resName + spaces + " : " +
							resDefs[i].type);
					printedResources.add(resName);
				}
				
				System.out.println();
			}
		}	
	}
	
}
