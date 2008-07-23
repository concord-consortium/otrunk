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
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2007-09-26 19:34:26 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTBundle;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.OTServiceContext;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.framework.otrunk.view.OTViewFactory;

/**
 * OTViewService
 * Class name and description
 *
 * Date created: May 18, 2005
 *
 * @author scott<p>
 *
 */
public class OTViewBundle extends DefaultOTObject
	implements OTBundle
{
    public static interface ResourceSchema extends OTResourceSchema {
    	
        public OTObjectList getViewEntries();
        public OTObjectList getViews();
        
        public OTObjectList getModes();
        
        public String getCurrentMode();
        public void setCurrentMode(String mode);
        
        public OTFrame getFrame();
        
        public boolean getShowLeftPanel();
        
        public static boolean DEFAULT_showLeftPanel = true;
    }
    
    ResourceSchema resources;
	private boolean viewFactoryServiceAlreadyExisted;
    
    public OTViewBundle(ResourceSchema resources)
    {
        super(resources);
        this.resources = resources;
    }

    public OTObjectList getViewEntries()
    {
    	if(resources.getViews().size() > 0){
    		return resources.getViews();
    	}
    	return resources.getViewEntries();
    }
    
    public OTObjectList getModes()
    {
    	return resources.getModes();
    }
    
    public String getCurrentMode()
    {
    	String sysPropViewMode = OTConfig.getSystemPropertyViewMode();
    	if(sysPropViewMode != null && sysPropViewMode.length() > 0){
    		return sysPropViewMode;
    	}
    	return resources.getCurrentMode();
    }
    
    public void setCurrentMode(String mode)
    {
    	resources.setCurrentMode(mode);
    }

	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTBundle#registerServices(org.concord.framework.otrunk.OTServiceContext)
     */
    public void registerServices(OTServiceContext serviceContext)
    {
    	if (serviceContext.getService(OTViewFactory.class) == null) {
			OTViewFactoryImpl factory = new OTViewFactoryImpl(this);
			factory.setDefaultViewMode(getCurrentMode());
			serviceContext.addService(OTViewFactory.class, factory);
		} else {
			viewFactoryServiceAlreadyExisted = true;
		}
		if (serviceContext.getService(OTMainFrame.class) == null) {
			OTMainFrame mainFrame = new OTMainFrame() {

				public OTFrame getFrame()
				{
					return resources.getFrame();
				}

				public boolean getShowLeftPanel()
				{
					return resources.getShowLeftPanel();
				}
			};
			serviceContext.addService(OTMainFrame.class, mainFrame);
		}
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.framework.otrunk.OTBundle#initializeBundle(org.concord.framework.otrunk.OTServiceContext)
	 */
    public void initializeBundle(OTServiceContext serviceContext)
    {
    	// If there already exists a view factory, add own view entries to the top of its list
    	// and override other properties
    	if (viewFactoryServiceAlreadyExisted){
    		OTViewFactoryImpl factory = (OTViewFactoryImpl) serviceContext.getService(OTViewFactory.class);
    		
    		factory.addViewBundle(this);    		
    	}
    }

}
