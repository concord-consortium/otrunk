package org.concord.otrunk.otcore.impl;

import java.util.ArrayList;

import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;


public class OTClassImpl extends OTTypeImpl
	implements OTClass
{
	private ArrayList superTypes = new ArrayList();
	
	private ArrayList properties = new ArrayList();

	Class constructorSchemaClass;
		
	public OTClassImpl(Class javaClass)
	{
		super(javaClass);
	}
	
	public void setConstructorSchemaClass(Class schemaClass)
	{
		this.constructorSchemaClass = schemaClass;
	}
		
	public Class getConstructorSchemaClass()
    {
    	return constructorSchemaClass;
    }

	public Class getSchemaInterface()
	{
		Class schemaClass = getConstructorSchemaClass();
		if(schemaClass != null){
			return schemaClass;
		}
		
		return getInstanceClass();
	}
	
	public ArrayList getOTAllClassProperties()
	{		
		ArrayList allProperties = new ArrayList(properties);		
		
		ArrayList allSuperTypes = getAllSuperTypes();
		for(int i=0; i < allSuperTypes.size(); i++){
			ArrayList superProperties = ((OTClassImpl)allSuperTypes.get(i)).getOTClassProperties();
			for(int j=0; j < superProperties.size(); j++){
				OTClassProperty superProperty = (OTClassProperty) superProperties.get(j);
				String superPropName = superProperty.getName();
				boolean foundExistingProperty = false;
				for(int k=0; k < allProperties.size(); k++){
					OTClassProperty existingProperty = (OTClassProperty) allProperties.get(k);
					if(existingProperty.getName().equals(superPropName)){
						foundExistingProperty = true;
						break;
					}
				}
				
				if(!foundExistingProperty){
					allProperties.add(superProperty);
				}
			}
		}

		return allProperties;
	}
		
	public ArrayList getOTClassProperties()
	{
		return properties;
	}	

	public OTClassProperty getProperty(String resourceName)
	{
		// TODO We should make a hashMap of these to speed this up.	

		OTClassProperty property = getPropertyInternal(resourceName);
		
		if(property != null){
			return property;
		}

		ArrayList allSuperTypes = getAllSuperTypes();
		for(int i=0; i < allSuperTypes.size(); i++){
			property = ((OTClassImpl)allSuperTypes.get(i)).getPropertyInternal(resourceName);
			if(property != null){
				return property;
			}
		}
		
		return null;
	}

	protected OTClassProperty getPropertyInternal(String resourceName)
	{
		for(int i=0; i<properties.size(); i++){
			OTClassProperty property = (OTClassProperty) properties.get(i);
			if(property.getName().equals(resourceName)){
				return property;
			}
		}

		return null;
	}
	
	public ArrayList getAllSuperTypes()
	{
		ArrayList allSuperTypes = new ArrayList(superTypes);
		
		for(int i=0; i < allSuperTypes.size(); i++){			
			ArrayList superSuperTypes = ((OTClass)allSuperTypes.get(i)).getOTSuperTypes();
			for(int j=0; j < superSuperTypes.size(); j++){
				OTClass superType = (OTClass) superSuperTypes.get(j);
				if(allSuperTypes.contains(superType)){
					continue;
				}
				allSuperTypes.add(superType);
			}
		}

		return allSuperTypes;
	}
	
	public ArrayList getOTSuperTypes()
	{
		return superTypes;
	}
}
