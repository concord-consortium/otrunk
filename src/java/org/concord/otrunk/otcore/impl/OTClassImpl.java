package org.concord.otrunk.otcore.impl;

import java.util.ArrayList;

import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;


public class OTClassImpl extends OTTypeImpl
	implements OTClass
{
	private ArrayList<OTClass> superTypes = new ArrayList<OTClass>();
	
	private ArrayList<OTClassProperty> properties = new ArrayList<OTClassProperty>();

	Class<?> constructorSchemaClass;
		
	public OTClassImpl(Class<?> javaClass)
	{
		super(javaClass);
	}
	
	public void setConstructorSchemaClass(Class<?> schemaClass)
	{
		this.constructorSchemaClass = schemaClass;
	}
		
	public Class<?> getConstructorSchemaClass()
    {
    	return constructorSchemaClass;
    }

	public Class<?> getSchemaInterface()
	{
		Class<?> schemaClass = getConstructorSchemaClass();
		if(schemaClass != null){
			return schemaClass;
		}
		
		return getInstanceClass();
	}
	
	public ArrayList<OTClassProperty> getOTAllClassProperties()
	{		
		ArrayList<OTClassProperty> allProperties = new ArrayList<OTClassProperty>(properties);		
		
		ArrayList<OTClass> allSuperTypes = getAllSuperTypes();
		for (OTClass superType : allSuperTypes) {
			ArrayList<OTClassProperty> superProperties = superType.getOTClassProperties();
			for (OTClassProperty superProperty : superProperties) {
				String superPropName = superProperty.getName();
				boolean foundExistingProperty = false;
				for (OTClassProperty existingProperty : allProperties) {
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
		
	public ArrayList<OTClassProperty> getOTClassProperties()
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

		ArrayList<OTClass> allSuperTypes = getAllSuperTypes();
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
	
	public ArrayList<OTClass> getAllSuperTypes()
	{
		ArrayList<OTClass> allSuperTypes = new ArrayList<OTClass>(superTypes);
		
		for(int i=0; i < allSuperTypes.size(); i++){			
			ArrayList<OTClass> superSuperTypes = allSuperTypes.get(i).getOTSuperTypes();
			for(int j=0; j < superSuperTypes.size(); j++){
				OTClass superType = superSuperTypes.get(j);
				if(allSuperTypes.contains(superType)){
					continue;
				}
				allSuperTypes.add(superType);
			}
		}

		return allSuperTypes;
	}
	
	public ArrayList<OTClass> getOTSuperTypes()
	{
		return superTypes;
	}
}
