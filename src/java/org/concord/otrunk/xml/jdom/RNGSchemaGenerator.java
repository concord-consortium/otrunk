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
 * $Revision: 1.8 $
 * $Date: 2007-09-07 02:04:12 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml.jdom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.concord.framework.otrunk.otcore.OTType;
import org.concord.otrunk.otcore.impl.OTCorePackage;
import org.concord.otrunk.otcore.impl.ReflectiveOTClassFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class RNGSchemaGenerator
{
    // Set this to false if you want to generate a schema that
    // restricts simple types to attributes.  Otherwise
    // simple types can be either elements or attributes
    public static boolean AMBIGUOUS_ATTRIBUTES = false;

    // Set this to false if you want only the short object names
    // supported.  This will invalidate some of the otml files 
    // but it will make it easier to author.  For example if this
    // is true then objects can be called:
    // org.concord.data.state.OTDataStore or OTDataStore
    // if it is false then only OTDataStore works
    public static boolean AMBIGUOUS_OBJECT_NAMES = false;

    // Set this to false if you want to force a consistent way of 
    // referencing objects.  Currently it can be done in one of 
    // two ways
    // if the resource name is myObject then you can do:
    // 1. <myObject><object refid="blah"/></myObject>
    // 2. <myObject refid="blah"/>
    // If this boolean is false then only number 1 is allowed
    public static boolean AMBIGUOUS_OBJECT_REFERENCES = false;
    
    // If this is set to false then something like:
    // <OTGraph>
    //   <name/>
    // </OTGraph>  
    // would be illegal.  However some of our content has this format
    // so setting this to true allows these empty elements
    // currently this only allows empty object resource elements
    public static boolean ALLOW_EMPTY_RESOURCE_ELEMENTS = false;
    
    // Turning this off allows xmllint to give better error messages
    // With it off invalid documents can be created because multiple
    // resources with the same name can be added.  This will be ignored
    // if STRICT_ORDERING is true.
    public static boolean USE_INTERLEAVE = true;
    
    // If this is true then a strict ordering of the resources in each
    // object is enforced. This will not be the same order created when 
    // an document is created by the XMLDatabase
    public static boolean STRICT_ORDERING = true;
    
    public static Namespace RNG_NAMESPACE = 
        Namespace.getNamespace("http://relaxng.org/ns/structure/1.0");
    
    public static void main(String [] args)
    {
        
        // create the jdom document        
        Element grammar = createRNGElement("grammar");
        grammar.setAttribute("datatypeLibrary", "http://www.w3.org/2001/XMLSchema-datatypes");
        Document doc = new Document(grammar);

        Element include = createRNGElement("include");
        include.setAttribute("href", "otrunk_base.rng");
        grammar.addContent(include);
        
        Element otObjectDefine = createRNGElement("define");
        otObjectDefine.setAttribute("name", "otObject");
        include.addContent(otObjectDefine);
        
        Element otObjectChoice = createChoice(otObjectDefine);

        Element basicObjectDef = createElementDef(otObjectChoice, "object");
        createAttributeDef(basicObjectDef, "refid");
        
        try {
            File xmlFile = new File(args[0]);
            File outFile = null;
            if (args[1] != null) {
            	outFile = new File(args[1]);
            }
            if (xmlFile.isDirectory()) {
            	String cmd = "ruby ./schema/collect_imports.rb " + args[0];
            	Process p = Runtime.getRuntime().exec(cmd);
            	final InputStream errStream = p.getErrorStream();
            	final InputStream outStream = p.getInputStream();
            	Thread processPrinter = (new Thread(){
            		byte [] buffer = new byte [1000];
            		public void run()
            		{
            			while(!isInterrupted()){
            				try {
	                            sleep(100);
                            } catch (InterruptedException e1) {
                            	// interrupt the thread again so the interrupted flag is set, and then continue
                            	// on with one last read before exiting.
                            	interrupt();
                            }
            				int numRead;
            				try {
            					numRead = errStream.read(buffer, 0, 1000);
            					if(numRead > 0){
            						System.err.write(buffer, 0, numRead);
            					}
            				} catch (IOException e) {
            					// TODO Auto-generated catch block
            					e.printStackTrace();
            				}

            				try {
            					numRead = outStream.read(buffer, 0, 1000);
            					if(numRead > 0){
            						System.out.write(buffer, 0, numRead);
            					}
            				} catch (IOException e) {
            					// TODO Auto-generated catch block
            					e.printStackTrace();
            				}
            			}
            		}
            	});
            	processPrinter.start();            	
            	p.waitFor();
            	processPrinter.interrupt();
            	
            	xmlFile = new File("/tmp/all-otrunk.xml");
            }
            FileInputStream xmlStream = new FileInputStream(xmlFile);
            
            // parse the xml file...
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(xmlStream);
            
            Element rootElement = document.getRootElement();
            
            
            Vector importedOTObjectClasses = new Vector();
            
            Element importsElement = rootElement;
            List imports = importsElement.getChildren();        
            for(Iterator iterator=imports.iterator();iterator.hasNext();) {
                Element currentImport=(Element)iterator.next();
                String className = currentImport.getAttributeValue("class");
                try{
                	Class importClass = Class.forName(className);
                	importedOTObjectClasses.add(importClass);
                } catch (ClassNotFoundException e){
                	System.err.println("Can't find class: " + className + " skipping it");
                }
            }       
            
    		ArrayList referrencedOTClasses = ReflectiveOTClassFactory.singleton.loadClasses(importedOTObjectClasses);

    		for(int i=0; i<referrencedOTClasses.size(); i++){
    			OTClass otClass = (OTClass) referrencedOTClasses.get(i);
    		
    			Element objectDef = createOTObjectDef(otClass);

    			otObjectChoice.addContent(objectDef);
            }   
            
            
            
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            if (outFile != null) {
            	outputter.output(doc, new FileOutputStream(outFile));
            	String path = outFile.getCanonicalPath();
            	String cmd = "java -jar ../thirdparty/trang.jar -I rng -O xsd " + outFile.getCanonicalPath() + " " + path.substring(0,path.lastIndexOf('.')) + ".xsd";
            	Runtime.getRuntime().exec(cmd);
            } else {
            	outputter.output(doc, System.out);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static Element createOTObjectDef(OTClass otClass)
    {
        Vector printedResources = new Vector();

        String name = otClass.getInstanceClass().getName();
        int lastDot = name.lastIndexOf(".");
        String shortName = name.substring(lastDot+1,name.length());

        Element objectDef = createRNGElement("element");
        if(AMBIGUOUS_OBJECT_NAMES){
            // This format doesn't work with xmllint
            // either I've got it wrong it it is parsing it wrong
            // a format that does seem to work is to separate out the content
            // from the element name in a define.  Then create two elements
            // one with each name.
            
            Element choiceNameClass = createChoice(objectDef);
            Element longNameClass = createRNGElement("name");
            longNameClass.setText(name);
            choiceNameClass.addContent(longNameClass);
            Element shortNameClass = createRNGElement("name");
            shortNameClass.setText(shortName);
            choiceNameClass.addContent(shortNameClass);
        } else {
            objectDef.setAttribute("name", shortName);
        }

        createAttributeDef(objectDef, "local_id", true, true);

        Element resourcesParentElement;
        if(STRICT_ORDERING) {
            resourcesParentElement = objectDef;
        } else {
            if(USE_INTERLEAVE) {
                resourcesParentElement = createRNGElement("interleave");
                objectDef.addContent(resourcesParentElement);
            } else {
                Element zeroOrMore = createZeroOrMore(objectDef);
                resourcesParentElement = createChoice(zeroOrMore);
            }
        }
        
        ArrayList properties = otClass.getOTAllClassProperties();
        if(properties == null || properties.size() == 0) {
            return objectDef;
        }
        
        for(int i=0; i<properties.size(); i++) {
        	OTClassProperty property = (OTClassProperty) properties.get(i);

        	
        	String resName = property.getName();
            if(printedResources.contains(resName)) {
                continue;
            }

            OTType otType = property.getType();
            Element resourceDefElement = null;
            
            if(resName.equals("localId")) {
                // skip local id definitions because
                // we add that option to all objects
                continue;
            } else if(otType == OTCorePackage.STRING_TYPE){
                resourceDefElement = createSimpleResourceDef(resName, "text");
            } else if(otType == OTCorePackage.XML_STRING_TYPE){
                resourceDefElement = createElementDef(null, resName);
                createPatternRef(resourceDefElement, "anyXMLFragment");
            } else if(otType == OTCorePackage.BOOLEAN_TYPE){
                resourceDefElement = createSimpleResourceDef(resName, "boolean");                
            } else if(otType == OTCorePackage.FLOAT_TYPE){
                resourceDefElement = createSimpleResourceDef(resName, "float");                                
            } else if(otType == OTCorePackage.DOUBLE_TYPE){
                resourceDefElement = createSimpleResourceDef(resName, "double");                                
            } else if(otType == OTCorePackage.INTEGER_TYPE){                
                // we want to allow hex values like 0xFFFFF making this
                // basic text seems to be the only way to do that
                resourceDefElement = createSimpleResourceDef(resName, "text");                                                
            } else if(otType == OTCorePackage.LONG_TYPE){                
                // we want to allow hex values like 0xFFFFF make this
                // basic text seems to be the only way to do that
                resourceDefElement = createSimpleResourceDef(resName, "text");                                                
            } else if(otType == OTCorePackage.BLOB_TYPE){
                resourceDefElement = createSimpleResourceDef(resName, "text");                
            } else if(otType == OTCorePackage.OBJECT_LIST_TYPE ||
            		otType == OTCorePackage.RESOURCE_LIST_TYPE){
                resourceDefElement = createElementDef(null, resName);
                createPatternRef(resourceDefElement, "listContents");          
            } else if(otType == OTCorePackage.OBJECT_MAP_TYPE ||
            		otType == OTCorePackage.RESOURCE_MAP_TYPE){
                resourceDefElement = createElementDef(null, resName);
                createPatternRef(resourceDefElement, "mapContents");                
            } else if(otType instanceof OTClass){
                resourceDefElement = createElementDef(null, resName);
                if(AMBIGUOUS_OBJECT_REFERENCES ||
                        ALLOW_EMPTY_RESOURCE_ELEMENTS) {
                    Element choice = createChoice(resourceDefElement);
                    if(ALLOW_EMPTY_RESOURCE_ELEMENTS) {
                        choice.addContent(createRNGElement("empty"));
                    }
                    
                    if(AMBIGUOUS_OBJECT_REFERENCES) {
                        createAttributeDef(choice, "refid");
                    }
                    
                    createPatternRef(choice, "otObject");               
                } else {
                    createPatternRef(resourceDefElement, "otObject");                                   
                }
            }            
                        
            printedResources.add(resName);
            
            if(resourceDefElement != null) {
                // right now all elements are optional
                if(!STRICT_ORDERING && USE_INTERLEAVE){
                    Element optional = createRNGElement("optional");
                    resourcesParentElement.addContent(optional);
                    
                    optional.addContent(resourceDefElement);
                } else {
                    resourcesParentElement.addContent(resourceDefElement);
                }
            }
        }
                
        return objectDef;
    }
    
    public static Element createSimpleResourceDef(String name, String type)
    {
        Element typeDef = null;
        if(type.equals("text")) {
            typeDef = createRNGElement("text");
        } else {
            typeDef = createRNGElement("data");
            typeDef.setAttribute("type", type);
        }
        
        if(AMBIGUOUS_ATTRIBUTES) {
            Element choice = createRNGElement("choice");
            Element attributeDef = createAttributeDef(choice, name, false, true);
            attributeDef.addContent((Element)typeDef.clone());                
            
            Element elementDef = createElementDef(choice, name);
            elementDef.addContent((Element)typeDef.clone()); 
            return choice;
        } else {
            Element attributeDef = createAttributeDef(null, name, false, true);
            attributeDef.addContent((Element)typeDef.clone());
            return attributeDef;
        }       
    }
    
    public static Element createPatternRef(Element parentDef, String name)
    {
        Element patternRef = createRNGElement("ref");
        patternRef.setAttribute("name", name);
        if(parentDef != null) {
            parentDef.addContent(patternRef);
        }
        return patternRef;
    }
    
    public static Element createElementDef(Element parentDef, String name)
    {
        Element elementDef = createRNGElement("element");
        elementDef.setAttribute("name", name);
        if(parentDef != null) {
            parentDef.addContent(elementDef);
        }
        return elementDef;
    }

    public static Element createAttributeDef(Element elementDef, String name)
    {
        return createAttributeDef(elementDef, name, true, true);
    }
    
    public static Element createAttributeDef(Element elementDef, String name, 
            boolean isText, boolean optional)
    {
        Element attributeDef = createRNGElement("attribute");
        attributeDef.setAttribute("name", name);
        if(isText) {
            Element textDef = createRNGElement("text");
            attributeDef.addContent(textDef);
        }

        if(optional) {
            Element optionalEl = createRNGElement("optional");
            optionalEl.addContent(attributeDef);
            attributeDef = optionalEl;
        }
        
        if(elementDef != null) {
            elementDef.addContent(attributeDef);
        }
        return attributeDef;
    }
    
    public static Element createZeroOrMore(Element parentDef)
    {
        Element zeroOrMore = createRNGElement("zeroOrMore");
        parentDef.addContent(zeroOrMore);
        return zeroOrMore;
    }

    public static Element createChoice(Element parentDef)
    {
        Element choice = createRNGElement("choice");
        parentDef.addContent(choice);
        return choice;
    }
    
    public static Element createRNGElement(String name)
    {
        return new Element(name, RNG_NAMESPACE);
    }
}
