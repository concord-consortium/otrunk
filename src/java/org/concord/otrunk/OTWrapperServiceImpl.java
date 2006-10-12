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
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTWrapper;
import org.concord.framework.otrunk.OTWrapperService;

public class OTWrapperServiceImpl implements OTWrapperService {
	Map realObjectMap = new HashMap();

	Map wrapperMap = new WeakHashMap();
	
	OTObjectService objectService;

	// The key in this map is the class that can be wrapped
	// the value is the class of the wrapper.
	Map wrapperClasses = new HashMap();
	
	public OTWrapperServiceImpl(OTObjectService objectService){
		this.objectService = objectService;
	}
	
	public Object getRealObject(OTWrapper wrapper) {
		Object realObject = internalGetRealObject(wrapper);
		
		if(realObject != null){
			return realObject;
		}

		realObject = wrapper.createRealObject();

		setupRealObject(wrapper, realObject);

		return realObject;
	}

	private final Object internalGetRealObject(OTWrapper wrapper) {
		Reference ref = (Reference)realObjectMap.get(wrapper);
		if(ref == null){
			return null;			
		}
		
		return ref.get();	
	}
	
	private final void setupRealObject(OTWrapper wrapper, Object realObject){
		// Save this object now to prevent infinite loops if
		// there is a circular reference
		realObjectMap.put(wrapper, new WeakReference(realObject));
		wrapperMap.put(realObject, wrapper);

		wrapper.loadRealObject(this, realObject);
		
		wrapper.registerRealObject(this, realObject);		
	}
	
	public Object getRealObject(OTWrapper wrapper, Object realObject) {
		Object oldRealObject = internalGetRealObject(wrapper);
		
		if(oldRealObject != null){
			// TODO what should we do here?
			if(oldRealObject == realObject){
				System.err.println("wrapper already had the object");
			} else {
				System.err.println("wrapper already had a different object");				
			}
		}
				
		setupRealObject(wrapper, realObject);
		
		return realObject;
	}

	public Class [] getRealObjectClasses(Class wrapperClass) {
		try {
			Field field = wrapperClass.getField("realObjectClasses");
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

	public OTWrapper getWrapper(Object realObject) 
	{
		// lookup the class of this object in the wrapperClass map
		// TODO this should look for matches up the inheritance tree.
		Class wrapperClass = (Class)wrapperClasses.get(realObject.getClass());
		
		return getWrapper(realObject, wrapperClass);
	}
	
	public OTWrapper getWrapper(Object realObject, Class wrapperClass)
	{
		OTWrapper wrapper = (OTWrapper)wrapperMap.get(realObject); 
		
		if(wrapper != null) {
			return wrapper;
		}
		
		if(wrapperClass == null) {
			System.err.println("can't find wrapper class for realObject: " 
					+ realObject.getClass());
			return null;
		}

		try {
			wrapper = (OTWrapper)objectService.createObject(wrapperClass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		if(wrapper == null) {
			System.err.println("can't create registered wrapper class: " +
					wrapperClass);
		}
		
		// store the relationship in the proper places
		// this should be done before methods on the wrapper are called
		// this should prevent infinite loops if there is a circular reference
		wrapperMap.put(realObject, wrapper);
		realObjectMap.put(wrapper, new WeakReference(realObject));
		
		// in this case the realObject is already initialized
		wrapper.registerRealObject(this, realObject);

		// save the object so the wrapper can pickup all the properties
		// from the real object
		wrapper.saveRealObject(this, realObject);

		return wrapper;
	}

	public void registerWrapperClass(Class wrapperClass) {
		Class [] realObjectClasses = getRealObjectClasses(wrapperClass);

		for(int i=0; i<realObjectClasses.length; i++) {
			wrapperClasses.put(realObjectClasses[i], wrapperClass);
		}
	}

}
