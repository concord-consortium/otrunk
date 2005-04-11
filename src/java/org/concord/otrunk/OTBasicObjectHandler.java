
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
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
 */

/*
 * Last modification information:
 * $Revision: 1.7 $
 * $Date: 2005-04-11 23:27:42 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Vector;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTObject;
import org.concord.otrunk.datamodel.OTDataObject;


/**
 * OTBasicObjectHandler
 * Class name and description
 *
 * Date created: Nov 9, 2004
 *
 * @author scott<p>
 *
 */
public class OTBasicObjectHandler extends OTResourceSchemaHandler
{
    Vector changeListeners = new Vector();
    private boolean doNotifyListeners = true;
    OTObject otObject;
    private OTChangeEvent changeEvent;
    
	public OTBasicObjectHandler(OTDataObject dataObject, OTrunkImpl db)
	{
		super(dataObject, db, null);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable
	{
	    if(otObject == null) {
	        otObject = (OTObject)proxy;
	        changeEvent = new OTChangeEvent(otObject);
	    }
	    
	    if(otObject != proxy) {
	        throw new RuntimeException("Trying to use the same handler for 2 proxy objects");
	    }
	    
	    String methodName = method.getName();
		
		if(methodName.equals("setOTDatabase")) {
			throw new RuntimeException("shouldn't be calling setDataObject");
		}
		
		// skip the init call if this is a basic object that is being proxied
		if(methodName.equals("init")) {
			return null;
		}

		if(methodName.equals("addOTChangeListener")) {
		    // param OTChangeListener listener
		    
		    // should check to see if this listener is already
		    // added
		    WeakReference listenerRef = new WeakReference(args[0]);
		    changeListeners.add(listenerRef);
		    return null;
		}
		
		if(methodName.equals("removeOTChangeListener")) {
		    // param OTChangeListener listener		    
		    for(int i=0; i<changeListeners.size(); i++) {
		        WeakReference ref = (WeakReference)changeListeners.get(i);
		        if(args[0] == ref.get()) {
		            changeListeners.remove(i);
		            return null;
		        }
		    }
		    return null;
		}
		
		if(methodName.equals("setDoNotifyChangeListeners")) {
		    setDoNotifyListeners(((Boolean)args[0]).booleanValue());
		    return null;
		}

		if(methodName.equals("notifyOTChange")) {
		    notifyOTChange();
		    return null;
		}

		
		return super.invoke(proxy, method, args);
	}
	
	protected boolean setResource(String name, Object value)
	{
	    boolean changed = super.setResource(name, value);
	    if(changed && doNotifyListeners) {
	        notifyOTChange();
	    }
	    
	    return changed;
	}

	public void setDoNotifyListeners(boolean doNotify)
	{
	    doNotifyListeners = doNotify;
	}
	
    public void notifyOTChange()
    {
        Vector toBeRemoved = null;
        
        for(int i=0;i<changeListeners.size(); i++){
            WeakReference ref = (WeakReference)changeListeners.get(i);
            Object listener = ref.get();
            if(listener != null) {
                ((OTChangeListener)listener).stateChanged(changeEvent);
            } else {
                // the listener was gc'd so lets mark it to be removed
                if(toBeRemoved == null) {
                    toBeRemoved = new Vector();
                }
                toBeRemoved.add(ref);
            }
        }
        
        if(toBeRemoved != null) {
            for(int i=0; i<toBeRemoved.size(); i++) {
                changeListeners.remove(toBeRemoved.get(i));
            }
        }
    }
}
