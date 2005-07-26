
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
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.OTMultiUserView;
import org.concord.framework.otrunk.view.OTObjectView;
import org.concord.framework.otrunk.view.OTView;
import org.concord.framework.otrunk.view.OTViewContainer;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OTViewFactory 
{
    OTrunk otrunk;
    OTViewFactory parent;
    Vector viewMap = new Vector();
    Vector userList = null;
        
    public OTViewFactory(OTrunk otrunk)
    {
        this.otrunk = otrunk;
    }
    
    public OTViewFactory(OTViewFactory parent)
    {
        this.parent = parent;
        this.otrunk = parent.otrunk;
    }
    
    class ViewEntry {
        Class objectClass;
        Class viewClass;
    }
        
    public void setUserList(Vector userList) {
    	this.userList = userList;
    }
    
    /* (non-Javadoc)
     * @see org.concord.otrunk.view.OTViewFactory#getComponent(org.concord.framework.otrunk.OTObject, org.concord.framework.otrunk.view.OTViewContainer, boolean)
     */
    public JComponent getComponent(OTObject pfObject,
            OTViewContainer container, boolean editable)
    {
        OTObjectView view = 
            getObjectView(pfObject, container);
        
        if(view == null) {
            return new JLabel("No view for object: " + pfObject);
        }
        
        return view.getComponent(editable);
    }

    public OTView getView(OTObject otObject, Class viewInterface)
    {
        OTView view = getViewInternal(otObject, viewInterface);
                
        if(view != null) { 
            if(view instanceof OTMultiUserView && 
                    userList != null) {
                ((OTMultiUserView)view).setUserList(otrunk, userList);                
            }
            
            if(view instanceof OTViewFactoryAware) {
                ((OTViewFactoryAware)view).setViewFactory(this);
            }
        }
        
        return view;
    }
    
    private OTView getViewInternal(OTObject otObject, Class viewInterface)
    {
        OTView view = null;
        for(int i=0; i<viewMap.size(); i++) {
            ViewEntry entry = (ViewEntry)viewMap.get(i);
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
     * @see org.concord.otrunk.view.OTViewFactory#getObjectView(org.concord.framework.otrunk.OTObject, org.concord.framework.otrunk.view.OTViewContainer)
     */
    public OTObjectView getObjectView(OTObject otObject,
            OTViewContainer container)
    {
        OTObjectView view = (OTObjectView)getView(otObject, OTObjectView.class);
        
        if(view == null) {
            return null;
        }
        
        view.initialize(otObject, container);
        
        return view;
    }
    
    public void addViewEntry(Class objectClass, Class viewClass)
    {
        ViewEntry internalEntry = new ViewEntry();
        internalEntry.objectClass = objectClass;
        internalEntry.viewClass = viewClass;
        viewMap.add(internalEntry);
    }
    
}
