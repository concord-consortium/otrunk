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

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.OTMultiUserView;
import org.concord.framework.otrunk.view.OTObjectView;
import org.concord.framework.otrunk.view.OTView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerAware;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.framework.otrunk.view.OTViewFactoryAware;

/**
 * @author scytacki
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OTViewFactoryImpl implements OTViewFactory 
{
    OTrunk otrunk;
    OTViewFactoryImpl parent;
    Vector viewMap = new Vector();
    Vector userList = null;
        
    public OTViewFactoryImpl(OTrunk otrunk, OTViewService viewService)
    {
        this.otrunk = otrunk;
        
        // read in all the viewEntries and create a vector 
        // of class entries.
        OTObjectList viewEntries = viewService.getViewEntries();
        
        for(int i=0; i<viewEntries.size(); i++) {
            OTViewEntry entry = (OTViewEntry)viewEntries.get(i);
            addViewEntry(entry);
        }        
    }
    
    protected OTViewFactoryImpl(OTViewFactoryImpl parent)
    {
        this.parent = parent;
        this.otrunk = parent.otrunk;
    }
    
    class InternalViewEntry {
        Class objectClass;
        Class viewClass;
        OTViewEntry otEntry;
    }

    public OTViewFactory createChildViewFactory()
    {
    	return new OTViewFactoryImpl(this);
    }
    
    /* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewFactory#setUserList(java.util.Vector)
	 */
    public void setUserList(Vector userList) {
    	this.userList = userList;
    }
    
    /* (non-Javadoc)
     * @see org.concord.otrunk.view.OTViewFactoryImpl#getComponent(org.concord.framework.otrunk.OTObject, org.concord.framework.otrunk.view.OTViewContainer, boolean)
     */
    /* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewFactory#getComponent(org.concord.framework.otrunk.OTObject, org.concord.framework.otrunk.view.OTViewContainer, boolean)
	 */
    public JComponent getComponent(OTObject otObject,
            OTViewContainer container, boolean editable)
    {
        OTObjectView view = getObjectView(otObject, container);

        // FIXME this doesn't set the frame manager, this method
        // should be removed from here, 
        
        if(view == null) {
            return new JLabel("No view for object: " + otObject);
        }
        
        return view.getComponent(otObject, editable);
    }

    /* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewFactory#getView(org.concord.framework.otrunk.OTObject, java.lang.Class)
	 */
    public OTView getView(OTObject otObject, Class viewInterface)
    {
        OTView view = getViewInternal(otObject, viewInterface);
         
        initView(view);
        
        return view;
    }
    
    protected void initView(OTView view)
    {
        if(view != null) { 
            if(view instanceof OTMultiUserView && 
                    userList != null) {
                ((OTMultiUserView)view).setUserList(otrunk, userList);                
            }
            
            if(view instanceof OTViewFactoryAware) {
                ((OTViewFactoryAware)view).setViewFactory(this);
            }
        }    	
    }
    
	public OTView getView(OTObject otObject, OTViewEntry viewEntry) 
	{
		// because we have the view entry we don't need to actually
		// look up this view.
        String viewClassStr = viewEntry.getViewClass();
        String objClassStr = viewEntry.getObjectClass();

        ClassLoader loader = getClass().getClassLoader();
		
        try {
            Class objectClass = loader.loadClass(objClassStr);
            Class viewClass = loader.loadClass(viewClassStr);

            if(!objectClass.isInstance(otObject)){
            	throw new RuntimeException("viewEntry: " + viewEntry + 
            			" cannot handle otObject: " + otObject);
            }

            OTView view = (OTView)viewClass.newInstance();
            initView(view);
        	return view;                       
        } catch (ClassNotFoundException e) {
            System.err.println("Can't find view: " + viewClassStr + 
                    " for object: " + objClassStr);
            System.err.println("  error: " + e.toString());
        } catch (InstantiationException e) {
        	e.printStackTrace();
        } catch (IllegalAccessException e) {
        	e.printStackTrace();
        }
		
		return null;
	}
    
    private OTView getViewInternal(OTObject otObject, Class viewInterface)
    {
        OTView view = null;
        for(int i=0; i<viewMap.size(); i++) {
            InternalViewEntry entry = (InternalViewEntry)viewMap.get(i);
            if(entry.objectClass.isInstance(otObject) &&
                    viewInterface.isAssignableFrom(entry.viewClass)) {
                try {
                    view = (OTView)entry.viewClass.newInstance();
                    break;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        // can't find the view in our own list
        // check parent
        if(view == null && parent != null) {
            view = parent.getViewInternal(otObject, viewInterface);
        }
        
        return view;        
    }
    
    /* (non-Javadoc)
     * @see org.concord.otrunk.view.OTViewFactoryImpl#getObjectView(org.concord.framework.otrunk.OTObject, org.concord.framework.otrunk.view.OTViewContainer)
     */
    /* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewFactory#getObjectView(org.concord.framework.otrunk.OTObject, org.concord.framework.otrunk.view.OTViewContainer)
	 */
    public OTObjectView getObjectView(OTObject otObject,
            OTViewContainer container)
    {
        OTObjectView view = (OTObjectView)getView(otObject, OTObjectView.class);
        
        if(view == null) {
            return null;
        }
        
        if(view instanceof OTViewContainerAware){
        	((OTViewContainerAware)view).setViewContainer(container);
        }
        
        return view;
    }
    
    /* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewFactory#addViewEntry(java.lang.Class, java.lang.Class)
	 */
    public void addViewEntry(OTViewEntry entry)
    {
        String objClassStr = entry.getObjectClass();
        String viewClassStr = entry.getViewClass();

        ClassLoader loader = getClass().getClassLoader();
        
        try {
            Class objectClass = loader.loadClass(objClassStr);
            Class viewClass = loader.loadClass(viewClassStr);

            InternalViewEntry internalEntry = new InternalViewEntry();
            internalEntry.objectClass = objectClass;
            internalEntry.viewClass = viewClass;
            internalEntry.otEntry = entry;
            viewMap.add(internalEntry);
            
        } catch (ClassNotFoundException e) {
            System.err.println("Can't find view: " + viewClassStr + 
                    " for object: " + objClassStr);
            System.err.println("  error: " + e.toString());
        }

    }

}
