package org.concord.otrunk;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.datamodel.BlobResource;
import org.concord.otrunk.datamodel.DataObjectUtil;
import org.concord.otrunk.datamodel.OTDataList;
import org.concord.otrunk.datamodel.OTDataMap;
import org.concord.otrunk.datamodel.OTDataObject;
import org.concord.otrunk.view.OTViewerHelper;

public class OTObjectInternal
{
	protected OTObjectServiceImpl objectService;
	
    Vector changeListeners = new Vector();

    /**
     * This is for debugging purposes it contains a mapping from
     * the weak reference object to the toString of the listener it
     * referenced.  This way when the listener is gc'd we can printout
     * its "label" (toString value).
     */
    Map changeListenerLabels;
    
    /**
     * This can be used by a user of an object to turn off the listening
     * 
     */
    protected boolean doNotifyListeners = true;
    
    /**
     * An internal variable to speed up skipping of the listener notification
     */
    protected boolean hasListeners = false;
    protected OTChangeEvent changeEvent;
    protected OTObject changeEventSource;

	protected OTDataObject dataObject;

	public OTObjectInternal(OTDataObject dataObject, OTObjectServiceImpl objectService)
    {
		this.objectService = objectService;
    	this.dataObject = dataObject;
    }	

	/* (non-Javadoc)
     * @see org.concord.otrunk.OTObjectInternal#getOTObjectService()
     */
    public OTObjectService getOTObjectService()
    {
        return objectService;    	
    }
    
	/* (non-Javadoc)
     * @see org.concord.otrunk.OTObjectInternal#setEventSource(org.concord.framework.otrunk.OTObject)
     */
    public void setEventSource(OTObject src)
    {
    	changeEventSource = src;
    }

	/* (non-Javadoc)
     * @see org.concord.otrunk.OTObjectInternal#setDoNotifyListeners(boolean)
     */
	public void setDoNotifyListeners(boolean doNotify)
	{
	    doNotifyListeners = doNotify;
	}
	
    /* (non-Javadoc)
     * @see org.concord.otrunk.OTObjectInternal#notifyOTChange(java.lang.String, java.lang.String, java.lang.Object)
     */
    public void notifyOTChange(String property, String operation, 
    	Object value)
    {
    	if(!doNotifyListeners || !hasListeners){
    		return;
    	}

    	Vector toBeRemoved = null;

    	if(changeEvent == null) {
    		changeEvent = new OTChangeEvent(changeEventSource);
    	}

    	changeEvent.setProperty(property);
    	changeEvent.setOperation(operation);
    	changeEvent.setValue(value);

    	for(int i=0;i<changeListeners.size(); i++){
    		WeakReference ref = (WeakReference)changeListeners.get(i);
    		Object listener = ref.get();
    		if(OTInvocationHandler.traceListeners){
    			System.out.println("sending stateChanged " + changeEvent.getDescription() +
    					" to " + listener);
    		}
    		if(listener != null) {
    			((OTChangeListener)listener).stateChanged(changeEvent);
    		} else {
    			// the listener was gc'd so lets mark it to be removed
    			if(toBeRemoved == null) {
    				toBeRemoved = new Vector();
    			}
    			if(OTInvocationHandler.traceListeners){
    				System.out.println("otChangeListener garbage collected:" +
    						changeListenerLabels.get(ref));
    			}
    			toBeRemoved.add(ref);
    		}
    	}

    	// clear the value so it doesn't remain around and 
    	// so it can be garbage collected
    	changeEvent.setValue(null);

    	if(toBeRemoved != null) {
    		for(int i=0; i<toBeRemoved.size(); i++) {
    			changeListeners.remove(toBeRemoved.get(i));
    		}
    		if(changeListeners.size() == 0){
    			hasListeners = false;
    		}
    	}
    }

	/* (non-Javadoc)
     * @see org.concord.otrunk.OTObjectInternal#addOTChangeListener(org.concord.framework.otrunk.OTChangeListener)
     */
	public void addOTChangeListener(OTChangeListener changeListener)
    {
		if(changeListener == null){
			throw new IllegalArgumentException("changeListener cannot be null");
		}
		
	    WeakReference listenerRef = new WeakReference(changeListener);
	    changeListeners.add(listenerRef);
	    
	    // debugging instrumentation
	    // ignore instances of the tracelistener
	    if(OTInvocationHandler.traceListeners &&
	    		!(changeListener instanceof TraceListener)){
	    	System.out.print("addOTChangeListener(obj:" + changeEventSource + 
	    			" (" + System.identityHashCode(changeEventSource) + ")");
	    	System.out.println(", listener:" + changeListener + 
	    			" (" + System.identityHashCode(changeListener) +") )");

	    	if(changeListenerLabels == null){
	    		changeListenerLabels = new HashMap();
	    		changeListenerLabels.put(listenerRef, "" + changeListener);
	    	}
	    }
	    hasListeners = true;
    }

	/* (non-Javadoc)
     * @see org.concord.otrunk.OTObjectInternal#removeOTChangeListener(org.concord.framework.otrunk.OTChangeListener)
     */
	public void removeOTChangeListener(OTChangeListener changeListener)
	{
	    if(OTInvocationHandler.traceListeners){
	    	System.out.println("removeOTChangeListener(obj:" + changeEventSource + 
	    			", listener:" + changeListener);
	    }

	    // param OTChangeListener listener		    
	    for(int i=0; i<changeListeners.size(); i++) {
	        WeakReference ref = (WeakReference)changeListeners.get(i);
	        if(changeListener == ref.get()) {
	            changeListeners.remove(i);

	            return;
	        }
	    }
	    if(changeListeners.size() == 0) {
	    	hasListeners = false;
	    }
	}

    public OTObject getOTObject(OTID childID) throws Exception
    {
    	return objectService.getOTObject(childID);
    }

    /* (non-Javadoc)
     * @see org.concord.otrunk.OTObjectInternal#setResource(java.lang.String, java.lang.Object)
     */
    public boolean setResource(String name, Object value)
	{
		if(value instanceof OTObject) {
			OTObject child = (OTObject)value;
			OTID childId = child.getGlobalId();
			value = childId;
		} else if(value instanceof byte[]) {
			value = new BlobResource((byte[])value);
		} else if(value instanceof URL){
			value = new BlobResource((URL)value);
		}
		
		// Check to see if it is equal before we go further
	    Object oldValue = getResourceValue(name);
	    if(oldValue != null && oldValue.equals(value)){
	        return false;
	    }

		// setResource should only return true if the dataObject was 
		// actually changed with this call
		if(setResourceInternal(name, value)){
			notifyOTChange(name, OTChangeEvent.OP_SET, value);			
		}
		
		return true;
	}
   
    public OTID getGlobalId()
    {
    	return dataObject.getGlobalId();
    }

	public boolean isResourceSet(String resourceName)
    {
        Object resourceValue = dataObject.getResource(resourceName);
        return resourceValue != null;
    
    }

	public Object getResourceValue(String resourceName)
    {
    	return dataObject.getResource(resourceName);
    }

	public OTResourceMap getResourceMap(String resourceName)
    {
        OTDataMap map = (OTDataMap)dataObject.getResourceCollection(
                resourceName, OTDataMap.class);
        return new OTResourceMapImpl(resourceName, map, this);
    }

	public OTObjectMap getObjectMap(String resourceName)
    {
    	OTDataMap map = (OTDataMap)dataObject.getResourceCollection(
    			resourceName, OTDataMap.class);
    	return new OTObjectMapImpl(resourceName, map, this);
    }

	public OTResourceList getResourceList(String resourceName)
    {
        OTDataList list = (OTDataList)dataObject.getResourceCollection(
                resourceName, OTDataList.class);
        return new OTResourceListImpl(resourceName, list, this);
    }

	public OTObjectList getObjectList(String resourceName)
    {
    	OTDataList list = (OTDataList)dataObject.getResourceCollection(
                resourceName, OTDataList.class);
        return new OTObjectListImpl(resourceName, list, this);
    }

	public boolean setResourceInternal(String name, Object value)
    {
    	return dataObject.setResource(name, value);
    }

	public String getOTClassName()
    {
    	return OTrunkImpl.getClassName(dataObject);
    }

	public void copyInto(OTObjectInternal targetObject)
    {
        // copy the dataObject
        DataObjectUtil.copyInto(dataObject, ((OTObjectInternal)targetObject).dataObject);        
    }
	
	public String internalToString()
	{
		return getOTClassName() + "@" +  getGlobalId();
	}
	
	protected void finalize()
	    throws Throwable
	{
	    if(OTViewerHelper.isTrace()){
	    	System.out.println("finalizing object: " + internalToString());
	    }
	}
}