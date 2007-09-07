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
 * $Revision: 1.6 $
 * $Date: 2007-09-07 02:04:11 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.concord.framework.otrunk.otcore.OTType;
import org.concord.otrunk.otcore.impl.ReflectiveOTClassFactory;
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
		
		// parse the xml file...
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(xmlStream);
		
		Element rootElement = document.getRootElement();
		
		ArrayList importedOTObjectClasses = new ArrayList();
				
		Element importsElement = rootElement;
		List imports = importsElement.getChildren();		
		for(Iterator iterator=imports.iterator();iterator.hasNext();) {
			Element currentImport=(Element)iterator.next();
			String className = currentImport.getAttributeValue("class");
			Class importClass = Class.forName(className);
			importedOTObjectClasses.add(importClass);
		}		
		
		ArrayList referrencedOTClasses = ReflectiveOTClassFactory.singleton.loadClasses(importedOTObjectClasses);
						
		for(int i=0; i<referrencedOTClasses.size(); i++){
			OTClass otClass = (OTClass) referrencedOTClasses.get(i);

			System.out.println("" + otClass.getInstanceClass().getName());

			System.out.print("     extends ");
			ArrayList superTypes = otClass.getOTSuperTypes();
			for(int j=0; j<superTypes.size(); j++){
				OTClass superType = (OTClass) superTypes.get(j);
				System.out.print(superType.getInstanceClass().getName() + " ");
			}
			System.out.println();
			
			ArrayList properties = otClass.getOTClassProperties();
			if(properties == null || properties.size() == 0){
				System.out.println("   no class properties");
			}
			for(int j=0; j<properties.size(); j++) {
				OTClassProperty otProperty = (OTClassProperty) properties.get(j);
				String resName = otProperty.getName();
				int numSpaces = 20 - resName.length();
				String spaces = "";
				for(int k=0; k<numSpaces; k++) spaces += " ";
				OTType otType = otProperty.getType();
				String typeName = "null type";
				if(otType != null){
					Class typeClass = otType.getInstanceClass();
					typeName = typeClass.getName();
				}
				System.out.println("  " + resName + spaces + " : " +
						typeName);
			}
			
			System.out.println();
			
		}
	}
	
}
