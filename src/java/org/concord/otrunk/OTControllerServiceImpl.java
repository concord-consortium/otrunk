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
import java.util.ArrayList;
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
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectService;

public class OTControllerServiceImpl implements OTControllerService 
{
	/**
	 * key is a OTObject
	 * value is a realObject
	 */
	Map<OTObject, WeakReference<Object>> realObjectFromOTMap = 
		new HashMap<OTObject, WeakReference<Object>>();

	/**
	 * key is the real object
	 * value is the OTController
	 */ 
	Map<Object, OTController> controllerFromRealMap = new WeakHashMap<Object, OTController>();
	
	/**
	 * key is the OTObject
	 * value is the OTController
	 */
	Map<OTObject, OTController> controllerFromOTMap = new WeakHashMap<OTObject, OTController>();
	
	/**
	 * A map of services that can be used by controller.
	 */
	Map<Class<?>, Object> serviceMap = new HashMap<Class<?>, Object>();
	
	OTObjectService objectService;
	OTControllerRegistry registry;
	
	OTControllerServiceImpl sharedService;

	// Track if we are disposing
	private boolean disposing = false;
	
	public OTControllerServiceImpl(OTObjectService objectService, 
		OTControllerRegistry registry)
	{
		this.objectService = objectService;
		this.registry = registry;
	}
	
	/**
	 * @see OTControllerService.getRealObject
	 */
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

		// if we are in the middle of disposing and we don't already have a real object just return null
		if(disposing){			
			return null;
		}
		
		realObject = setupRealObject(otObject, null);
			
		return realObject;		
	}
	
	/**
	 * A helper method to get a Vector of real objects from an
	 * OTObjectList, so that we don't have to first get the OTObjectList
	 * and then create all the RealObjects ourselves.
	 * 
	 * @param otObjectList
	 * @return
	 */
	public Vector<Object> getRealObjects(OTObjectList otObjectList){
		Vector<OTObject> otVector = otObjectList.getVector();
		Vector<Object> objVector = new Vector<Object>();
		for (OTObject otObject : otVector) {
	        objVector.add(getRealObject(otObject));
        }
		return objVector;
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
		OTController controller;
		synchronized(controllerFromRealMap){
			controller = (OTController)controllerFromRealMap.get(realObject);
		}
		
		if(controller != null) {
			return controller;
		}

		// check to see if there is already shared controller for this otObject
		if(sharedService != null){
			return sharedService.getExistingControllerFromRealObject(realObject);
		}

		return null;		
	}

	/**
	 * You should check to see if the controller has already been created before
	 * calling this method.
	 * 
	 * @param otObject
	 * @param realObject
	 * @return
	 */
	private final OTController getControllerInternal(OTObject otObject, Object realObject)
	{
		// need to find the OTController that can handle this OTObject
		Class<? extends OTObject> otObjectClass = null;
		if(otObject != null) {
			otObjectClass = otObject.getClass();
		}
		Class<?> realObjectClass = null;
		if(realObject != null) {
			realObjectClass = realObject.getClass();
		}
		
		OTController controller =  createControllerInternal(otObjectClass, realObjectClass);
		
		return controller;
	}

	private OTController getAndInitializeController(OTObject otObject, Object realObject)
	{
		// check to see if we already have a controller for this otObject
		OTController controller = getExistingControllerFromOTObject(otObject);
		if (controller != null) {
			// we already have a controller for this otObject
			// we don't need to initialize it because the only way we could have found an
			// existing controller is if it was already initialized.
			return controller;
		} else {		
			controller = getControllerInternal(otObject, realObject);
		}
		
		if(sharedService != null && controller.isRealObjectSharable(otObject, realObject)){
			controller.initialize(otObject, sharedService);
		} else {
			controller.initialize(otObject, this);
		}
		
		return controller;		
	}
	
	
	public OTController getController(OTObject otObject)
	{
		OTController controller = getExistingControllerFromOTObject(otObject);
		if (controller != null) {
			// we already have a controller for this otObject, so we can skip the 
			// initialization performed below
			return controller;
		}

		controller = getControllerInternal(otObject, null);		
		
		if(sharedService != null && controller.isRealObjectSharable(otObject, null)){
			sharedService.initializeAndStoreRelationships(controller, otObject, null);
		} else {
			initializeAndStoreRelationships(controller, otObject, null);
		}
		
		return controller;
	}
	
	@SuppressWarnings("unchecked")
    private final OTController createControllerInternal(Class<? extends OTObject> otObjectClass, 
		Class<?> realObjectClass)
	{
		// need to find the OTController that can handle this OTObject
		// need to go through all the parents of the otObjectClass
		Class<? extends OTController> controllerClass = 
			registry.getControllerClassByOTObjectClass(otObjectClass);

		// try the first level of interfaces if the class can't be found
		// this should really go up the inheritance tree, both interfaces and
		// superclasses		
		// just doing the first level will handle the proxy classes
		Class<?> [] interfaces = null;
		
		if(controllerClass == null) {
			// I believe this will return only the first level of interfaces
			interfaces = otObjectClass.getInterfaces();
			for (Class<?> class1 : interfaces) {
				// skip non OTObject classes
				if(!OTObject.class.isAssignableFrom(class1)){
					continue;
				}
				controllerClass = 
					registry.getControllerClassByOTObjectClass((Class<? extends OTObject>) class1);
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
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}


		return null;
	}
		
	private Object getExistingRealObjectFromOTObject(OTObject otObject) 
	{
		Reference<?> ref = (Reference<?>)realObjectFromOTMap.get(otObject);
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
		// check to see if we already have a controller for this otObject
		OTController controller = getExistingControllerFromOTObject(otObject);
		if (controller != null) {
			// we already have a controller for this otObject
			// we might not need to do the following initialization but that is how it was
			// working before so I'm going to leave it that way for now.
		} else {		
			controller = getControllerInternal(otObject, realObject);
		}
		
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

	public Class<?> [] getRealObjectClasses(Class<? extends OTController> controllerClass) {
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
    public Class<? extends OTObject> getOTObjectClass(Class<? extends OTController> controllerClass) {
		try {
			Field field = controllerClass.getField("otObjectClass");
			return (Class)field.get(null);
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
		Class<? extends OTController> controllerClass = 
			registry.getControllerClassByRealObjectClass(realObject.getClass());
		
		if(controllerClass == null) {
			System.err.println("can't find controller class for realObject: " 
					+ realObject.getClass());
			return null;
		}

		Class<? extends OTObject> otObjectClass = getOTObjectClass(controllerClass);

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
		
		// Instantiate the controller
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
		// check if we are in the middle of disposing I'm not sure what to do here so for now
		// just print a stacktrace and keep going
		if(disposing){
			Exception exception = 
				new Exception("Initiializing a controller while in the middle of disposing this service");
			exception.printStackTrace();
		}
		
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
		realObjectFromOTMap.put(otObject, new WeakReference<Object>(realObject));
		synchronized(controllerFromRealMap){
			controllerFromRealMap.put(realObject, controller);
		}
		controllerFromOTMap.put(otObject, controller);
		
		return realObject;
	}
	
	/**
	 * This currently does not store the relationships between the 
	 * controller, otObject, and real object.
	 * It is not clear if it should or should not.
	 * 
	 * @see org.concord.framework.otrunk.OTControllerService#saveRealObject(java.lang.Object, org.concord.framework.otrunk.OTObject)
	 */
	public void saveRealObject(Object realObject, OTObject otObject) 
	{
		OTController controller = getAndInitializeController(otObject, realObject);		
		controller.saveRealObject(realObject);
	}
	
	/**
	 * This currently does not store the relationships between the 
	 * controller, otObject, and real object.
	 * It is not clear if it should or should not.
	 * 
	 * @see org.concord.framework.otrunk.OTControllerService#registerRealObject(java.lang.Object, org.concord.framework.otrunk.OTObject)
	 */
	public void registerRealObject(Object realObject, OTObject otObject) 
	{
		OTController controller = getAndInitializeController(otObject, realObject);		
		controller.registerRealObject(realObject);		
	}
	
	/**
	 * 
	 * @see org.concord.framework.otrunk.OTControllerService#registerControllerClass(java.lang.Class)
	 */
	public void registerControllerClass(Class<? extends OTController> viewClass) 
	{		
		registry.registerControllerClass(viewClass);
	}
	
	/**
	 * This currently does not store the relationships between the 
	 * controller, otObject, and real object.
	 * It is not clear if it should or should not.
	 * 
	 * @see org.concord.framework.otrunk.OTControllerService#loadRealObject(org.concord.framework.otrunk.OTObject, java.lang.Object)
	 */
	public void loadRealObject(OTObject otObject, Object realObject)
	{
		OTController controller = getAndInitializeController(otObject, realObject);		
		controller.loadRealObject(realObject);
	}

	/** 
     * @see org.concord.framework.otrunk.OTControllerService#dispose()
     */
    public void dispose()
    {
    	disposing  = true;
    	ArrayList<OTController> disposedControllers = new ArrayList<OTController>();

    	// There are 2 maps that contain references to controllers.
    	// They should be in sync so we only have to search one
    	// we won't dispose controllers in our shared service that is its job not ours
    	synchronized(controllerFromRealMap){
    		Set<Entry<Object, OTController>> entries = controllerFromRealMap.entrySet();    		
    		Iterator<Entry<Object, OTController>> iterator = entries.iterator();
    		
    		// Copy the entries so if any of the controller.dispose
    		// calls cause changes to the list a concurrency exception
    		// wouldn't be thrown.
    		ArrayList<Entry<Object, OTController>> entriesCopy = new ArrayList<Entry<Object, OTController>>();
    		while(iterator.hasNext()){
    			Entry<Object, OTController> entry = (Entry<Object, OTController>) iterator.next();
    			entriesCopy.add(entry);
    		}

    		iterator = entriesCopy.iterator();
    		while(iterator.hasNext()){
    			Entry<Object, OTController> entry = iterator.next();
    			OTController controller = (OTController) entry.getValue();
    			if(disposedControllers.contains(controller)){
    				continue;
    			}
    			controller.dispose(entry.getKey());
    			disposedControllers.add(controller);
    		}    
    	}
    }
    
	public OTControllerServiceImpl getSharedService()
    {
    	return sharedService;
    }

	public void setSharedService(OTControllerServiceImpl sharedService)
    {
    	this.sharedService = sharedService;
    }

	public OTControllerService createSubControllerService(OTObjectService subObjectService)
	{
		OTControllerServiceImpl subService = 
			new OTControllerServiceImpl(subObjectService, registry);
		subService.setSharedService(this);
		return subService;
	}

	public void addService(Class<?> serviceClass, Object service)
    {
		serviceMap.put(serviceClass, service);
	    
    }

	@SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass)
    {
		return (T) serviceMap.get(serviceClass);
    }
	
	public OTObjectService getObjectService()
	{
		return objectService;
	}
}
