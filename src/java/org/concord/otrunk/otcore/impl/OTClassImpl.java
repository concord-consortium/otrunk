package org.concord.otrunk.otcore.impl;

import java.util.ArrayList;

import org.concord.framework.otrunk.otcore.OTClass;
import org.concord.framework.otrunk.otcore.OTClassProperty;


public class OTClassImpl extends OTTypeImpl
	implements OTClass
{
	private OTClassImpl superType;
	
	private ArrayList properties = new ArrayList();
	String javaClassName;
	
	public ArrayList getProperties()
	{
		return properties;
	}
		

	public OTClassProperty getProperty(String resourceName)
	{
		// TODO We should make a hashMap of these to speed this up.	
		for(int i=0; i<properties.size(); i++){
			OTClassProperty property = (OTClassProperty) properties.get(i);
			if(property.getName().equals(resourceName)){
				return property;
			}
		}

		if(superType == null){
			return null;
		}
		
		return superType.getProperty(resourceName);
	}

	protected Class createInstanceClass() 
	{
		try {
			return Class.forName(javaClassName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
