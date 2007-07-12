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

package org.concord.otrunk;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import org.concord.framework.otrunk.OTController;
import org.concord.framework.otrunk.OTControllerRegistry;
import org.concord.framework.otrunk.OTControllerService;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;

public class OTControllerServiceImpl implements OTControllerService 
{
	/**
	 * key is a OTObject
	 * value is a realObject
	 */
	Map realObjectFromOTMap = new HashMap();

	/**
	 * key is the real object
	 * value is the OTController
	 */ 
	Map controllerFromRealMap = new WeakHashMap();
	
	/**
	 * key is the OTObject
	 * value is the OTController
	 */
	Map controllerFromOTMap = new WeakHashMap();
	
	OTObjectService objectService;
	OTControllerRegistry registry;
	
	OTControllerServiceImpl sharedService;
	
	public OTControllerServiceImpl(OTObjectService objectService, 
		OTControllerRegistry registry)
	{
		this.objectService = objectService;
		this.registry = registry;
	}
	
	public Object getRealObject(OTObject otObject) 
	{
		// Handle the trivial case.
		if(otObject == null) {
			return null;
		}
				
		Object realObject = getExistingRealObjectFromOTObject(otObject);

		if(realObject != null){
			return realObject;
		}		

		realObject = setupRealObject(otObject, realObject);
			
		return realObject;		
	}

	private OTController getExistingControllerFromOTObject(OTObject otObject)
	{
		// check to see if we already have a controller for this otObject
		OTController controller = (OTController)controllerFromOTMap.get(otObject);
		if (controller != null) {
			return controller;
		}
		
		// check to see if there is already shared controller for this otObject
		if(sharedService != null){
			return sharedService.getExistingControllerFromOTObject(otObject);
		}

		return null;
	}

	private OTController getExistingControllerFromRealObject(Object realObject)
	{
		// check if we already have a controller, if so then return its otObject
		OTController controller = (OTController)controllerFromRealMap.get(realObject);
		
		if(controller != null) {
			return controller;
		}

		// check to see if there is already shared controller for this otObject
		if(sharedService != null){
			return sharedService.getExistingControllerFromRealObject(realObject);
		}

		return null;		
	}
	
	private final OTController getControllerInternal(OTObject otObject, Object realObject)
	{
		// check to see if we already have a controller for this otObject
		OTController controller = getExistingControllerFromOTObject(otObject);
		if (controller != null) {
			// we already have a controller for this otObject
			return controller;
		}
		
		// need to find the OTController that can handle this OTObject
		Class otObjectClass = null;
		if(otObject != null) {
			otObjectClass = otObject.getClass();
		}
		Class realObjectClass = null;
		if(realObject != null) {
			realObjectClass = realObject.getClass();
		}
		
		controller =  createControllerInternal(otObjectClass, realObjectClass);
		
		return controller;
	}

	private OTController getAndInitializeController(OTObject otObject, Object realObject)
	{
		OTController controller = getControllerInternal(otObject, realObject);

		if(sharedService != null && controller.isRealObjectSharable(otObject, realObject)){
			controller.initialize(otObject, sharedService);
		} else {
			controller.initialize(otObject, this);
		}
		
		return controller;		
	}
	
	
	private final OTController createControllerInternal(Class otObjectClass, Class realObjectClass)
	{
		// need to find the OTController that can handle this OTObject
		// need to go through all the parents of the otObjectClass
		Class controllerClass = 
			registry.getControllerClassByOTObjectClass(otObjectClass);

		// try the first level of interfaces if the class can't be found
		// this should really go up the inheritance tree, both interfaces and
		// superclasses		
		// just doing the first level will handle the proxy classes
		Class [] interfaces = null;
		
		if(controllerClass == null) {
			// I believe this will return only the first level of interfaces
			interfaces = otObjectClass.getInterfaces();
			for(int i=0; i<interfaces.length; i++){				
				controllerClass = 
					registry.getControllerClassByOTObjectClass(interfaces[i]);
				if(controllerClass != null){
					break;
				}
			}
		}
		
		if(controllerClass == null){
			// Can't find a controller for this otObject
			String otObjectClassStr = "" + otObjectClass;
			if(Proxy.isProxyClass(otObjectClass) && 
					interfaces != null && interfaces.length > 0){
				otObjectClassStr = "" + interfaces[0];
			}
			throw new RuntimeException("Can't find a controller for this otObject: " +
					otObjectClassStr);
		}
		
		try {
			OTController controller = (OTController)controllerClass.newInstance();

			return controller;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;
	}
		
	private Object getExistingRealObjectFromOTObject(OTObject otObject) 
	{
		Reference ref = (Reference)realObjectFromOTMap.get(otObject);
		if(ref != null){
			return ref.get();	
		}

		if(sharedService != null){
			return sharedService.getExistingRealObjectFromOTObject(otObject);
		}
		
		return null;	
	}
	
	/**
	 * 
	 * @param controller
	 * @param otObject
	 * @param realObject
	 */
	private final Object setupRealObject(OTObject otObject, Object realObject)
	{
		OTController controller = getControllerInternal(otObject, realObject);

		/*
		 * The relationships between the controller, ot and real object should be stored
		 * before calling this method, to prevent infinite loops.  These loops will happen
		 * if a controller calls methods on the controller service in loadRealObject or 
		 * registerRealObject and there are circular references.  Having circular references 
		 * and calling methods on the controller service is allowed and should be supported.
		 */
		if(sharedService != null && controller.isRealObjectSharable(otObject, realObject)){
			realObject = sharedService.initializeAndStoreRelationships(controller, otObject, realObject);
		} else {
			realObject = initializeAndStoreRelationships(controller, otObject, realObject);
			
		}				
		
		controller.loadRealObject(realObject);
		controller.registerRealObject(realObject);
		
		return realObject;
	}
	
	
	public Object getRealObject(OTObject otObject, Object realObject) 
	{
		Object oldRealObject = getExistingRealObjectFromOTObject(otObject);

		if(oldRealObject != null){
			// TODO what should we do here?
			if(oldRealObject == realObject){
				System.err.println("otObject already had the object");
			} else {
				System.err.println("otObject already had a different object");				
			}
		}
		
		if(otObject == null){
			String realObjectDesc = "null";
			if(realObject != null){
				realObjectDesc = "" + realObject.getClass() + ": " + realObject;
			}
			throw new IllegalArgumentException("null otObject for: " + 
					realObjectDesc);
		}
		
		realObject = setupRealObject(otObject, realObject);
		
		return realObject;
	}

	public Class [] getRealObjectClasses(Class controllerClass) {
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

	public Class getOTObjectClass(Class controllerClass) {
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

	/**
	 * This should actually receive the class of the OTObject, that will help look up the correct
	 * OTController.  There could be mutiple controllers which handle a particular real object class. 
	 * 
	 * @see org.concord.framework.otrunk.OTControllerService#getOTObject(java.lang.Object)
	 */
	public OTObject getOTObject(Object realObject) 
	{
		// Handle the Trivial case
		if(realObject == null){
			return null;
		}
		
		// check if we already have a controller, if so then return its otObject
		OTController controller = getExistingControllerFromRealObject(realObject);
		
		if(controller != null) {
			return controller.getOTObject();
		}

		// Figure out which otObjectClass we should be using
		// this comes from the controller class we found
			// TODO this should look for matches up the inheritance tree.
		Class controllerClass = 
			registry.getControllerClassByRealObjectClass(realObject.getClass());
		
		if(controllerClass == null) {
			System.err.println("can't find controller class for realObject: " 
					+ realObject.getClass());
			return null;
		}

		Class otObjectClass = getOTObjectClass(controllerClass);

		// if we don't have a view then we don't have the otObject
		// so we need to make a new one.
		OTObject otObject = null;
		
		try {
			otObject = (OTObject)objectService.createObject(otObjectClass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		if(otObject == null) {
			System.err.println("can't create registered OTObject class: " +
					otObjectClass);
		}
		
		// instanciate the controller
		try {
			controller = (OTController)controllerClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		if(sharedService != null && controller.isRealObjectSharable(otObject, realObject)){
			sharedService.initializeAndStoreRelationships(controller, otObject, realObject);
		} else {
			initializeAndStoreRelationships(controller, otObject, realObject);
		}
		
		// save the object so the otObject will pickup all the properties
		// from the real object
		controller.saveRealObject(realObject);

		// in this case the realObject is already initialized
		controller.registerRealObject(realObject);
		
		return otObject;
	}

	private Object initializeAndStoreRelationships(OTController controller, OTObject otObject, Object realObject)
	{
		// initialize it
		controller.initialize(otObject, this);
					
		// check if the real object is null if it is then we need to create one
		// using the initizlied controller
		if(realObject == null){
			realObject = controller.createRealObject();
		}
		
		// store the relationship in the proper places
		// this should be done before methods on the controller are called
		// this should prevent infinite loops if there is a circular reference
		realObjectFromOTMap.put(otObject, new WeakReference(realObject));
		controllerFromRealMap.put(realObject, controller);
		controllerFromOTMap.put(otObject, controller);
		
		return realObject;
	}
	
	public void saveRealObject(Object realObject, OTObject otObject) 
	{
		OTController controller = getAndInitializeController(otObject, realObject);		
		controller.saveRealObject(realObject);
	}
	
	public void registerRealObject(Object realObject, OTObject otObject) 
	{
		OTController controller = getAndInitializeController(otObject, realObject);		
		controller.registerRealObject(realObject);		
	}
	
	public void registerControllerClass(Class viewClass) 
	{		
		registry.registerControllerClass(viewClass);
	}
	
	/**
	 * @see org.concord.framework.otrunk.OTControllerService#loadRealObject(org.concord.framework.otrunk.OTObject, java.lang.Object)
	 */
	public void loadRealObject(OTObject otObject, Object realObject)
	{
		OTController controller = getAndInitializeController(otObject, realObject);		
		controller.loadRealObject(realObject);
	}

	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTControllerService#dispose()
     */
    public void dispose()
    {
    	Vector disposedControllers = new Vector();

    	// There are 2 maps that contain references to controllers.
    	// They should be in sync so we only have to search one
    	// we won't dispose controllers in our shared service that is its job not ours
		Set entries = controllerFromRealMap.entrySet();
		Iterator iterator = entries.iterator();
		while(iterator.hasNext()){
			Entry entry = (Entry) iterator.next();
			OTController controller = (OTController) entry.getValue();
			if(disposedControllers.contains(controller)){
				continue;
			}
			controller.dispose(entry.getKey());
			disposedControllers.add(controller);
		}    	    	
    }
    
    protected void disposeValues(Map map, Vector disposedControllers)
    {
    }

	public OTControllerServiceImpl getSharedService()
    {
    	return sharedService;
    }

	public void setSharedService(OTControllerServiceImpl sharedService)
    {
    	this.sharedService = sharedService;
    }

	public OTControllerService createSubControllerService()
	{
		OTControllerServiceImpl subService = 
			new OTControllerServiceImpl(objectService, registry);
		subService.setSharedService(this);
		return subService;
	}
}
