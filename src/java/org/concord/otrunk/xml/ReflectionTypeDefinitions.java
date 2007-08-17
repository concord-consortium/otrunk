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
 * $Revision: 1.22 $
 * $Date: 2007-08-17 13:21:29 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.concord.framework.otrunk.otcore.OTType;
import org.concord.otrunk.OTInvocationHandler;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.otcore.impl.OTClassImpl;
import org.concord.otrunk.otcore.impl.OTClassPropertyImpl;
import org.concord.otrunk.otcore.impl.OTCorePackage;


/**
 * XMLTypeDefinitions
 * Class name and description
 *
 * Date created: Nov 17, 2004
 *
 * @author scott<p>
 *
 */
public class ReflectionTypeDefinitions
{
	public static void registerTypes(List importedOTObjectClasses, TypeService typeService,
			XMLDatabase xmlDB)
	throws Exception
	{
	    registerTypes(importedOTObjectClasses, typeService, xmlDB, true);
	}
    
	public static void registerTypes(List importedOTObjectClasses, TypeService typeService,
			XMLDatabase xmlDB, boolean addShortcuts)
		throws Exception 
	{
		ClassLoader classloader = ReflectionTypeDefinitions.class.getClassLoader();
		Vector typeClasses = new Vector();
		
		for(int i=0; i<importedOTObjectClasses.size(); i++) {
	        String className = (String)importedOTObjectClasses.get(i);			
		    try {
		        Class typeClass = classloader.loadClass(className);
		        typeClasses.add(typeClass);
		    } catch (Exception e) {
		        System.err.println("Error importing class: " + className);
                System.err.println("  this class was listed as an import in the otml file");
		    }
		}		
		
		// These object handlers will be processed in a second pass 
		// to setup the OTClass structure
		ArrayList objectHandlersToProcess = new ArrayList();
		
		for(int i=0; i<typeClasses.size(); i++) {
			Class otObjectClass = (Class)typeClasses.get(i); 
			String className = otObjectClass.getName();
			
			// do our magic here to register the type
			// first figure out if it uses a seperate schema object
			// get that object, otherwise use the class itself
			Class resourceSchemaClass = null;
			if(otObjectClass.isInterface()){
				resourceSchemaClass = otObjectClass;
			} else {
				Constructor [] memberConstructors = otObjectClass.getConstructors();
				Constructor resourceConstructor = memberConstructors[0]; 
				Class [] params = resourceConstructor.getParameterTypes();
						
				// Check all the conditions for incorrect imports.
				if(memberConstructors.length > 1 || params == null || 
						params.length == 0 ||
						!OTResourceSchema.class.isAssignableFrom(params[0])) {
					System.err.println("Invalid constructor for OTrunk Object: " + className +
							"\n   If you are using an otml file check the import statements");
					throw new RuntimeException("OTObjects should only have 1 constructor" + "\n" +
							" whose first argument is the resource schema");
				}
				
				resourceSchemaClass = params[0];
			}

			if(resourceSchemaClass == null) {
				throw new RuntimeException("Can't find valid schema class for: " +
						className);
			}
			
			Vector resourceDefs = new Vector();
			addResources(resourceDefs, resourceSchemaClass, typeClasses, true);
			
			OTClass existingOTClass = OTrunkImpl.getOTClass(className);
			OTClass newOTClass = null;
			if(existingOTClass == null ){
				newOTClass = new OTClassImpl();
				OTrunkImpl.putOTClass(className, newOTClass);				
			}
			
			ResourceDefinition [] resourceDefsArray = new ResourceDefinition[resourceDefs.size()];
			for(int j=0; j<resourceDefsArray.length; j++) {
				ResourceDefinition resourceDef = (ResourceDefinition)resourceDefs.get(j);
				resourceDefsArray[j] = resourceDef;				
			}
			ObjectTypeHandler objectType = 
				new ObjectTypeHandler(
						className,
						className,
						null,
						resourceDefsArray,
						typeService,
						xmlDB);

			if(newOTClass != null){
				objectHandlersToProcess.add(objectType);				
			}
			
			typeService.registerUserType(className, objectType);
			
			if(addShortcuts) {
			    int lastDot = className.lastIndexOf(".");
			    String localClassName = className.substring(lastDot+1,className.length());
			    typeService.registerUserType(localClassName, objectType);
			}
		}
		
		for(int i=0; i<objectHandlersToProcess.size(); i++){
			ObjectTypeHandler objectType = (ObjectTypeHandler) objectHandlersToProcess.get(i);
			
			OTClass otClass = OTrunkImpl.getOTClass(objectType.getClassName());
			
			ResourceDefinition [] resourceDefsArray = objectType.getResourceDefinitions();
			for(int j=0; j<resourceDefsArray.length; j++){
				ResourceDefinition resourceDef = resourceDefsArray[j];
				String resourceName = resourceDef.getName();
				
				if(resourceName.equals("localId")){
					// skip this one
					continue;
				}
				
				OTType otType = null;
				String resourceType = resourceDef.getType();
				Class resourceTypeClass = resourceDef.getTypeClass();
				
				if(TypeService.BOOLEAN.equals(resourceType)){
					otType = OTCorePackage.BOOLEAN_TYPE;
				} else if(TypeService.DOUBLE.equals(resourceType)){
					otType = OTCorePackage.DOUBLE_TYPE;
				} else if(TypeService.FLOAT.equals(resourceType)){
					otType = OTCorePackage.FLOAT_TYPE;
				} else if(TypeService.INTEGER.equals(resourceType)){
					otType = OTCorePackage.INTEGER_TYPE;
				} else if(TypeService.LONG.equals(resourceType)){
					otType = OTCorePackage.LONG_TYPE;
				} else if(TypeService.STRING.equals(resourceType)){
					otType = OTCorePackage.STRING_TYPE;
				} else if(TypeService.XML_STRING.equals(resourceType)){
					otType = OTCorePackage.XML_STRING_TYPE;
				} else if(TypeService.OBJECT.equals(resourceType)){
					otType = OTrunkImpl.getOTClass(resourceTypeClass.getName());

					if(otType == null){
						// TODO For now we just add dummy classes we should do some more careful
						// type checking here,  
						System.err.println("Can't find OTClass for: " + resourceTypeClass.getName() + 
								" adding dummy OTClass");
						OTClass dummyClass = new OTClassImpl();
						OTrunkImpl.putOTClass(resourceTypeClass.getName(), dummyClass);
					} 
				} else if(TypeService.LIST.equals(resourceType)){
					if(resourceTypeClass.equals(OTResourceList.class)){
						otType = OTCorePackage.RESOURCE_LIST_TYPE;
					} else if(resourceTypeClass.equals(OTObjectList.class)){
						otType = OTCorePackage.OBJECT_LIST_TYPE;
					}
				} else if(TypeService.MAP.equals(resourceType)){
					if(resourceTypeClass.equals(OTResourceMap.class)){
						otType = OTCorePackage.RESOURCE_MAP_TYPE;
					} else if(resourceTypeClass.equals(OTObjectMap.class)){
						otType = OTCorePackage.OBJECT_MAP_TYPE;
					}
				} 
				
				OTClassProperty otClassProperty = new OTClassPropertyImpl(resourceName, otType, null);
				
				// might need to check for duplicates, but I think we fixed further upstream
				otClass.getProperties().add(otClassProperty);
			}
		}
	}
	
	public static void addResources(List resources, Class resourceSchemaClass,
	        List typeClasses, boolean processParents)
	{
		// Then look for all the getters and their types
		Method [] methods;
		if(processParents){
			methods = resourceSchemaClass.getMethods();
		} else {
			methods = resourceSchemaClass.getDeclaredMethods();
		}
		for(int j=0; j<methods.length; j++) {
			String methodName = methods[j].getName();
			if(methodName.equals("getGlobalId") ||
			        methodName.equals("getOTDatabase") ||
                    methodName.equals("getOTObjectService")) {
				continue;
			}
			
			if(!methodName.startsWith("get")) {
				continue;				
			}
			
			String resourceName = OTInvocationHandler.getResourceName(3,methodName);
			Class resourceClass = methods[j].getReturnType();
			String resourceType = TypeService.getObjectPrimitiveType(resourceClass);

			if(resourceType == null){
			    // This resource type might be an interface that one of the other
			    // typeClasses implements.  
			    for(int k=0; k<typeClasses.size(); k++){
			        Class typeClass = (Class)typeClasses.get(k);
			        if(resourceClass.isAssignableFrom(typeClass)){
			            resourceType = "object";
			        }
			    }
			}			

			if(resourceType == null){
				System.err.println("Warning: the field: " + resourceName + " on class: " + resourceSchemaClass + "\n" + 
                        "    has an unknown type: " + resourceClass + "\n"  +
                        "  There are no imported classes that implement this type");
                // in a strict assertion mode we might want to stop
                // here, but setting the type to object seems pretty safe
                resourceType = "object";
			}
			
			ResourceDefinition resourceDef = new ResourceDefinition(resourceName,
					resourceType, resourceClass, null);
			resources.add(resourceDef);
		}		
	}	
}
