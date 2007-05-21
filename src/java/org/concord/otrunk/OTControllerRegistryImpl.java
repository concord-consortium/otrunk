package org.concord.otrunk;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.concord.framework.otrunk.OTController;
import org.concord.framework.otrunk.OTControllerRegistry;

public class OTControllerRegistryImpl implements OTControllerRegistry
{
	// This maps from the class that can be managed to 
	// the OTController which can handle it.  This can be a one to
	// many map.  So the values are Lists.
	Map controllerClassesFromRealObject = new HashMap();
	
	// This maps from the otObject class that can be manged to 
	// the list of OTControllers which can handle it.
	Map controllerClassesFromOTObject = new HashMap();

	Vector controllerClasses = new Vector();

	/* (non-Javadoc)
     * @see org.concord.otrunk.OTControllerRegistry#registerControllerClass(java.lang.Class)
     */
	public void registerControllerClass(Class viewClass) {
		if(controllerClasses.contains(viewClass)){
			return;
		}

		controllerClasses.add(viewClass);
		
		Class [] classes;
		classes = getRealObjectClasses(viewClass);
		for(int i=0; i<classes.length; i++) {
			controllerClassesFromRealObject.put(classes[i], viewClass);
		}

		Class klass = getOTObjectClass(viewClass);
		controllerClassesFromOTObject.put(klass, viewClass);		
	}

	public final static Class [] getRealObjectClasses(Class controllerClass) {
		try {
			Field field = controllerClass.getField("realObjectClasses");
			return (Class [])field.get(null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public final static Class getOTObjectClass(Class controllerClass) {
		try {
			if(!OTController.class.isAssignableFrom(controllerClass)){
				throw new IllegalArgumentException("controllerClass doesn't implement "+ OTController.class);
			}
			
			Field field = controllerClass.getField("otObjectClass");
			return (Class)field.get(null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			System.err.println("invalid controller class: " + controllerClass);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	/* (non-Javadoc)
     * @see org.concord.otrunk.OTControllerRegistry#getControllerClassByOTObjectClass(java.lang.Class)
     */
	public Class getControllerClassByOTObjectClass(Class otObjectClass)
    {
		return (Class)controllerClassesFromOTObject.get(otObjectClass);
    }

	/* (non-Javadoc)
     * @see org.concord.otrunk.OTControllerRegistry#getControllerClassByRealObjectClass(java.lang.Class)
     */
	public Class getControllerClassByRealObjectClass(Class realObjectClass)
    {
		return (Class)controllerClassesFromRealObject.get(realObjectClass);
    }

}
