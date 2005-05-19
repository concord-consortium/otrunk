/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-05-19 17:09:49 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.view.OTObjectView;
import org.concord.framework.otrunk.view.OTViewContainer;

/**
 * OTViewService
 * Class name and description
 *
 * Date created: May 18, 2005
 *
 * @author scott<p>
 *
 */
public class OTViewService extends DefaultOTObject
    implements OTViewFactory
{
    public static interface ResourceSchema extends OTResourceSchema {
        public OTObjectList getViewEntries();
    }
    
    ResourceSchema resources;
    
    Vector viewMap = new Vector();
    
    public OTViewService(ResourceSchema resources)
    {
        super(resources);
        this.resources = resources;
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

    /* (non-Javadoc)
     * @see org.concord.otrunk.view.OTViewFactory#getObjectView(org.concord.framework.otrunk.OTObject, org.concord.framework.otrunk.view.OTViewContainer)
     */
    public OTObjectView getObjectView(OTObject otObject,
            OTViewContainer container)
    {
        for(int i=0; i<viewMap.size(); i++) {
            ViewEntry entry = (ViewEntry)viewMap.get(i);
            if(entry.objectClass.isInstance(otObject)) {
                try {
                    OTObjectView view = (OTObjectView)entry.viewClass.newInstance();

                    view.initialize(otObject, container);
                    
                    return view;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
    
    class ViewEntry {
        Class objectClass;
        Class viewClass;
    }
    
    public void init()
    {
        // read in all the viewEntries and create a vector 
        // of class entries.
        OTObjectList viewEntries = resources.getViewEntries();
        ClassLoader loader = OTViewService.class.getClassLoader();
        
        for(int i=0; i<viewEntries.size(); i++) {
            OTViewEntry entry = (OTViewEntry)viewEntries.get(i);
            String objClassStr = entry.getObjectClass();
            String viewClassStr = entry.getViewClass();
            
            try {
                ViewEntry internalEntry = new ViewEntry();
                internalEntry.objectClass = loader.loadClass(objClassStr);
                internalEntry.viewClass = loader.loadClass(viewClassStr);
                viewMap.add(internalEntry);
            } catch (ClassNotFoundException e) {
                System.err.println("Can't find view: " + viewClassStr + 
                        " for object: " + objClassStr);
                System.err.println("  error: " + e.toString());
            }
        }
        
    }

}
