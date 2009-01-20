package org.concord.otrunk;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.concord.framework.otrunk.OTController;
import org.concord.framework.otrunk.OTControllerRegistry;
import org.concord.framework.otrunk.OTObject;

public class OTControllerRegistryImpl implements OTControllerRegistry
{
	// This maps from the class that can be managed to 
	// the OTController which can handle it.  This can be a one to
	// many map.  So the values are Lists.
	Map<Class<?>, Class<? extends OTController>> controllerClassesFromRealObject = 
		new HashMap<Class<?>, Class<? extends OTController>>();
	
	// This maps from the otObject class that can be managed to 
	// the list of OTControllers which can handle it.
	Map<Class<? extends OTObject>, Class<? extends OTController>> controllerClassesFromOTObject = 
		new HashMap<Class<? extends OTObject>, Class<? extends OTController>>();

	ArrayList<Class<? extends OTController>> controllerClasses = new ArrayList<Class<? extends OTController>>();

	/* (non-Javadoc)
     * @see org.concord.otrunk.OTControllerRegistry#registerControllerClass(java.lang.Class)
     */
	public void registerControllerClass(Class<? extends OTController> controllerClass) {
		if(controllerClasses.contains(controllerClass)){
			return;
		}

		controllerClasses.add(controllerClass);
		
		Class<?> [] classes = getRealObjectClasses(controllerClass);
		for(int i=0; i<classes.length; i++) {
			controllerClassesFromRealObject.put(classes[i], controllerClass);
		}

		Class<? extends OTObject> klass = getOTObjectClass(controllerClass);
		controllerClassesFromOTObject.put(klass, controllerClass);		
	}

	public final static Class<?> [] getRealObjectClasses(Class<? extends OTController> controllerClass) {
		try {
			Field field = controllerClass.getField("realObjectClasses");
			return (Class [])field.get(null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}

    @SuppressWarnings("unchecked")
    public final static Class<? extends OTObject> getOTObjectClass(Class<? extends OTController> controllerClass) {
		try {
			Field field = controllerClass.getField("otObjectClass");
			return (Class<? extends OTObject>)field.get(null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			System.err.println("invalid controller class: " + controllerClass);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/* (non-Javadoc)
     * @see org.concord.otrunk.OTControllerRegistry#getControllerClassByOTObjectClass(java.lang.Class)
     */
	public Class<? extends OTController> getControllerClassByOTObjectClass(Class<? extends OTObject> otObjectClass)
    {
		return controllerClassesFromOTObject.get(otObjectClass);
    }

	/* (non-Javadoc)
     * @see org.concord.otrunk.OTControllerRegistry#getControllerClassByRealObjectClass(java.lang.Class)
     */
	public Class<? extends OTController> getControllerClassByRealObjectClass(Class<?> realObjectClass)
    {
		return controllerClassesFromRealObject.get(realObjectClass);
    }

}
