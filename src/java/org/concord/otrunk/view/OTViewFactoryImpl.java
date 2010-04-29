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

/*
 * Created on Jan 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.otrunk.view;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.view.OTRequestedViewEntryAware;
import org.concord.framework.otrunk.view.OTView;
import org.concord.framework.otrunk.view.OTViewContext;
import org.concord.framework.otrunk.view.OTViewContextAware;
import org.concord.framework.otrunk.view.OTViewConversionService;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewEntryAware;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.otrunk.OTrunkUtil;

/**
 * @author scytacki
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OTViewFactoryImpl implements OTViewFactory, OTLifecycleNotifying
{
	private static final Logger logger =
        Logger.getLogger(OTViewFactoryImpl.class.getCanonicalName());
	
    OTViewFactoryImpl parent;
    ArrayList<InternalViewEntry> viewMap = new ArrayList<InternalViewEntry>();
    ArrayList<OTViewBundle> viewBundles = new ArrayList<OTViewBundle>();
    OTViewContextImpl viewContext;
	private String mode;
	ArrayList<OTLifecycleListener> lifecycleListeners = new ArrayList<OTLifecycleListener>();
    
    public OTViewFactoryImpl(OTViewBundle viewBundle)
    {
        this.viewBundles.add(viewBundle);
        
        // read in all the viewEntries and create a vector 
        // of class entries.
        OTObjectList viewEntries = viewBundle.getViewEntries();
        
        for(int i=0; i<viewEntries.size(); i++) {
            OTViewEntry entry = (OTViewEntry)viewEntries.get(i);
            addViewEntry(entry);
        }
        
        initServices();
    }
    
    protected OTViewFactoryImpl(OTViewFactoryImpl parent)
    {
        this.parent = parent;
        
        initServices();
    }
    
    protected void initServices()
    {    	
    	OTViewContext viewContextParent = null;
    	if(parent != null){
    		viewContextParent = parent.getViewContext();
    	}
        viewContext = new OTViewContextImpl(this, viewContextParent);
        
        // we might have a view conversion service registered already, so don't override it. 
        if(viewContext.getViewService(OTViewConversionService.class) == null){
            viewContext.addViewService(OTViewConversionService.class, 
            	new OTViewConversionServiceImpl());
        }
    }
    
    /**
     * This should be called after the bundles have been added and any view services
     * have been added to the viewContext
     */
    public void contextSetupComplete()
    {
    	for(InternalViewEntry viewEntry: viewMap){        	
        	try {
    	        Method otInit = viewEntry.viewClass.getMethod("otInit", OTViewContext.class);
    	        otInit.invoke(null, getViewContext());
            } catch (NoSuchMethodException e) {
            	// this class doesn't have an otInit method
            } catch (NoClassDefFoundError e){
            	// this class can't be loaded because of a missing dependency
            	logger.warning("Missing dependency : " + e.getMessage() + 
            		" for view class: " + viewEntry.viewClass.getCanonicalName());
            } catch (Throwable t) {
            	t.printStackTrace();
            }
    	}
    	notifyLifecycleListeners(OTLifecycleEvent.INITIALIZATION_COMPLETE);
    }
    
    class InternalViewEntry {
        Class<? extends OTObject> objectClass;
        Class<? extends OTView> viewClass;
        OTID otEntryID;

        /**
         * The object service is used so the otview entry can be overridden by
         * the layering system. 
         * 
         * @param objService
         * @return
         */
        OTViewEntry getOTViewEntry(OTObjectService objService)
        {
        	OTViewEntry otViewEntry;
            try {
	            otViewEntry = (OTViewEntry) objService.getOTObject(otEntryID);
            } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            return null;
            }
        	return otViewEntry;
        }
    }

    public OTViewFactory createChildViewFactory()
    {
    	return new OTViewFactoryImpl(this);
    }
    
    /**
	 * @see org.concord.otrunk.view.OTViewFactory#getView(org.concord.framework.otrunk.OTObject, java.lang.Class)
	 */
    public <T extends OTView> T getView(OTObject otObject, Class<T> viewInterface)
    {
    	return getView(otObject, viewInterface, null);
    }

	/**
	 * @see org.concord.framework.otrunk.view.OTViewFactory#getView(org.concord.framework.otrunk.OTObject, java.lang.Class, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
    public <T extends OTView> T getView(OTObject otObject, Class<T> viewInterface, String modeStr) 
	{		
		InternalViewEntry entry = getViewInternal(otObject, viewInterface);
		if(entry == null) {
			// we did not find the view using its specific viewInterface, but perhaps there is a view
			// with a different interface and the mode provide a view with the correct interface
			// for efficiency we should check if a view mode will be used.  
			entry = getViewInternal(otObject, (Class<?>)null);
			if(entry == null) {
				return null;
			}
		}

		OTObjectService objService = otObject.getOTObjectService();

		OTViewEntry viewEntry = entry.getOTViewEntry(objService);
		OTView view = getView(otObject, viewEntry, modeStr);
		
		// We need to do a final check here because we might have ignored the passed in viewInterface
		// above.
		if(!viewInterface.isInstance(view)){
			return null;
		}
		
		return (T)view; 
	}
    
    protected void initView(OTView view, OTViewEntry viewEntry)
    {
    	if(view instanceof OTViewContextAware) {
    		((OTViewContextAware)view).setViewContext(viewContext);
    	}

    	if(view instanceof OTViewEntryAware) {
    		((OTViewEntryAware)view).setViewEntry(viewEntry);
    	}            
    }
    
	public OTView getView(OTObject otObject, OTViewEntry viewEntry) 
	{
		return getView(otObject, viewEntry, null);		
	}
	
	private OTView getViewInternal(OTObject otObject, OTViewEntry viewEntry) 
	{
		// because we have the view entry we don't need to actually
		// look up this view.
		
        String viewClassStr = viewEntry.getViewClass();
        String objClassStr = viewEntry.getObjectClass();        
        
        ClassLoader loader = getClass().getClassLoader();
		
        try {
            Class<?> objectClass = loader.loadClass(objClassStr);

            if(!objectClass.isInstance(otObject)){
        		throw new RuntimeException("viewEntry: " + viewEntry + 
        				" cannot handle otObject: " + otObject);
        	}
            
            if(viewClassStr == null){
            	throw new RuntimeException("viewEntry " + viewEntry + 
            			" has a null viewClass");
            }
            
            OTView view = null;
            Class<?> viewClass = loader.loadClass(viewClassStr);
            view = (OTView)viewClass.newInstance();

            initView(view, viewEntry);
        	return view;                       
        } catch (ClassNotFoundException e) {
            logger.warning(
            	"Can't find view: " + viewClassStr + " for object: " + objClassStr + "\n" +
                "  error: " + e.toString());
        } catch (InstantiationException e) {
        	e.printStackTrace();
        } catch (IllegalAccessException e) {
        	e.printStackTrace();
        }
		
		return null;
	}
    
	/**
	 * null is allowed for the viewInterface.  
	 * 
	 * @param otObject
	 * @param viewInterface
	 * @return
	 */
    private InternalViewEntry getViewInternal(OTObject otObject, Class<?> viewInterface)
    {
    	for(InternalViewEntry entry: viewMap){
            if(entry.objectClass.isInstance(otObject) &&
            		(viewInterface == null || viewInterface.isAssignableFrom(entry.viewClass))) {
            	return entry;
            }
        }

        // can't find the view in our own list
        // check parent
        if(parent != null) {
            return parent.getViewInternal(otObject, viewInterface);
        }
        
        return null;        
    }
    
    public void addViewEntry(OTViewEntry entry)
    {
    	InternalViewEntry internalEntry = createInternalViewEntry(entry);
    	if(internalEntry == null){
    		return;
    	}
    	
        viewMap.add(internalEntry);
    }
    
    @SuppressWarnings("unchecked")
    protected InternalViewEntry createInternalViewEntry(OTViewEntry entry)
    {
        String objClassStr = entry.getObjectClass();
        String viewClassStr = entry.getViewClass();

        ClassLoader loader = getClass().getClassLoader();
        
        try {
            InternalViewEntry internalEntry = new InternalViewEntry();
            internalEntry.objectClass = (Class<? extends OTObject>) loader.loadClass(objClassStr);
            
            if(viewClassStr != null){
            	internalEntry.viewClass = (Class<? extends OTView>) loader.loadClass(viewClassStr);
            }

            internalEntry.otEntryID = entry.getGlobalId();
            return internalEntry;
        } catch (ClassNotFoundException e) {
            logger.warning(
            	"Can't find view: " + viewClassStr + " for object: " + objClassStr + "\n" +
                "  error: " + e.toString());
        }
        
        return null;
    }
    
    
    /* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewFactory#addViewEntry(java.lang.Class, java.lang.Class)
	 */
    public void addViewEntry(OTViewEntry entry, boolean addToTop)
    {
    	InternalViewEntry internalEntry = createInternalViewEntry(entry);
    	if(internalEntry == null){
    		return;
    	}

        if (addToTop){
        	viewMap.add(0, internalEntry);
        } else {
        	viewMap.add(internalEntry);
        }
    }    	
    	

    /**
     * This will return the viewEntry setup by this mode.  If no viewEntry is 
     * found for this mode, then it will return null. 
     * 
     * If modeStr == null then getDefaultViewMode will be used to look up the 
     * view mode.
     * If modeStr equals NO_VIEW_MODE then this method will return null;
     * 
     * @param viewEntry
     * @param modeStr
     * @return
     */
    protected OTViewEntry getModeViewEntry(OTViewEntry viewEntry, String modeStr)
    {
    	if(modeStr == null){
    		modeStr = getDefaultViewMode();
    	}
    	
    	if(NO_VIEW_MODE.equals(modeStr)){
    		return null;
    	}

		OTViewMode firstMode = null;
		OTViewEntry modeViewEntry = null;
		
		List<OTViewBundle> allViewBundles = getViewBundles();
    	for(int j=0; j<allViewBundles.size() && modeViewEntry == null; j++){
    		OTViewBundle viewBundle = allViewBundles.get(j);
    		OTObjectList modes = viewBundle.getModes();
    		for(int i=0; i<modes.size(); i++){
    			OTViewMode curMode = (OTViewMode)modes.get(i);
    			if(curMode.getName().equals(modeStr)){
    				if(firstMode == null) {
    					firstMode = curMode;
    				}
    				OTObjectMap map = curMode.getMap();

    				modeViewEntry = 
    					(OTViewEntry)OTrunkUtil.getObjectFromMapWithIdKeys(map, viewEntry);
    				break;
    			}
    		}
    	}
		
		if(firstMode == null){
			logger.warning("Cannot find view mode: \"" + modeStr + "\"");
			return null;
		}
		
		if(modeViewEntry == null){
			modeViewEntry = firstMode.getDefault();
		}

		return modeViewEntry;
    }
    
	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTViewFactory#getView(org.concord.framework.otrunk.OTObject, org.concord.framework.otrunk.view.OTViewEntry, java.lang.String)
	 */
	public OTView getView(OTObject otObject, OTViewEntry viewEntry, String modeStr) 
	{
		OTViewEntry activeViewEntry = viewEntry;
		
		OTViewEntry modeViewEntry = getModeViewEntry(viewEntry, modeStr);

		if(modeViewEntry != null){
			activeViewEntry = modeViewEntry;
		}

		OTView view = getViewInternal(otObject, activeViewEntry);

		// if the modeViewEntry is not null then 
		// pass the viewEntry that was requested to the newly created view
		// this is useful for mode views that want to display other modes of
		// the original view entry.
		// this entry might have been specified by the user, or it could have 
		// been determined by looking up an interface and object type.
		if(modeViewEntry != null && 
				view instanceof OTRequestedViewEntryAware) {
			((OTRequestedViewEntryAware)view).setRequestedViewEntry(viewEntry);
		}
		
		return view;
		
	}
	
	/**
	 * Return a combined list of view bundles.
	 * @return
	 */
	public List<OTViewBundle> getViewBundles()
	{
		ArrayList<OTViewBundle> combinedBundles = new ArrayList<OTViewBundle>();
		combinedBundles.addAll(viewBundles);
		if (!(parent == null)){
			combinedBundles.addAll(parent.getViewBundles());
		}
		
		return combinedBundles;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTViewFactory#getViewServiceProvider()
	 */
	public OTViewContext getViewContext() 
	{
		return viewContext;
	}
	
	/**
	 * Return the view mode set for this view factory.  If this is 
	 * null then getDefaultViewMode will try to return the view mode
	 * off the parent.
	 * 
	 * @return
	 */
	public String getDefaultViewModeLocal()
	{
		return mode;
	}

	/**
	 * This returns the view mode used if null is passed in for the view mode
	 * to the getView methods.
	 * 
	 * @see org.concord.framework.otrunk.view.OTViewFactory#getDefaultViewMode()
	 */
	public String getDefaultViewMode()
    {
		if (mode == null && parent != null){
			return parent.getDefaultViewMode();
		}
		
		if(mode == null){
			return NO_VIEW_MODE;
		}
	    return mode;
    }

	/**
	 * This sets the default view mode for this view factory.  If it is null
	 * then the viewMode of the parent is used.  If this is set to null
	 * and there is no parent, then NO_VIEW_MODE is used.
	 * 
	 * @see org.concord.framework.otrunk.view.OTViewFactory#setDefaultViewMode(java.lang.String)
	 */
	public void setDefaultViewMode(String mode)
    {
	    this.mode = mode;
    }

	public String [] getModeNames()
    {
		ArrayList<String> names = new ArrayList<String>();

		for(OTViewBundle bundle: getViewBundles()){
			for (OTObject curMode : bundle.getModes()) {
				if(!names.contains(curMode.getName())){
					names.add(curMode.getName());
				}				
			}
		}

		return names.toArray(new String[names.size()]);
    }

	/**
	 * Adds all the viewEntries from the bundle 
	 * Overrides the default view mode.
	 * Adds the bundle to the beginning of the list of bundles which is used to find view mode view entries.
	 * 
	 * @param viewBundle
	 */
	public void addViewBundle(OTViewBundle viewBundle)
    {
		// Add view entries		
		ArrayList<InternalViewEntry> tempViewMap = new ArrayList<InternalViewEntry>();
		for(OTObject entry: viewBundle.getViewEntries()){
			InternalViewEntry internalViewEntry = createInternalViewEntry((OTViewEntry) entry);
			if(internalViewEntry == null){
				continue;
			}
			tempViewMap.add(internalViewEntry);
		}

		viewMap.addAll(0, tempViewMap);
		
		// Override currentMode
		if (viewBundle.getCurrentMode() != null){
			setDefaultViewMode(viewBundle.getCurrentMode());
		}

		viewBundles.add(0, viewBundle);			
    }
	
	private void notifyLifecycleListeners(int type) {
		OTLifecycleEvent event = new OTLifecycleEvent(type, this);
		for (OTLifecycleListener listener : lifecycleListeners) {
			listener.lifecycleEventOccurred(event);
		}
	}

	public void addLifecycleListener(OTLifecycleListener listener)
    {
		if (! this.lifecycleListeners.contains(listener)) {
			this.lifecycleListeners.add(listener);
		}
    }

	public void removeLifecycleListener(OTLifecycleListener listener)
    {
	    this.lifecycleListeners.remove(listener);
    }
	
}
