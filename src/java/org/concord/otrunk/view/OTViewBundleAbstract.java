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

import org.concord.framework.otrunk.OTBundle;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTServiceContext;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.otrunk.AbstractOTObject;

/**
 * OTViewService
 * Class name and description
 *
 * Date created: May 18, 2005
 *
 * @author scott<p>
 *
 */
public abstract class OTViewBundleAbstract extends AbstractOTObject
	implements OTBundle
{    	
	protected abstract OTObjectList _getViewEntries();
	protected abstract OTObjectList _getViews();

	public abstract OTObjectList getModes();

	protected abstract String _getCurrentMode();
	public abstract void setCurrentMode(String mode);

	public abstract OTFrame getFrame();

	public static boolean DEFAULT_showLeftPanel = true;
	public abstract boolean getShowLeftPanel();

	private boolean viewFactoryServiceAlreadyExisted;
    
    public OTObjectList getViewEntries()
    {
    	if(_getViews().size() > 0){
    		return _getViews();
    	}
    	return _getViewEntries();
    }
    
    public String getCurrentMode()
    {
    	String sysPropViewMode = OTConfig.getSystemPropertyViewMode();
    	if(sysPropViewMode != null && sysPropViewMode.length() > 0){
    		return sysPropViewMode;
    	}
    	return _getCurrentMode();
    }
    
	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTBundle#registerServices(org.concord.framework.otrunk.OTServiceContext)
     */
    public void registerServices(OTServiceContext serviceContext)
    {
    	if (serviceContext.getService(OTViewFactory.class) == null) {
    		// this is changed due to class interdepencency it doesn't need
    		// to be changed to use the new Abstract OTClass pattern
			OTViewFactoryImpl factory = null;
			factory.setDefaultViewMode(getCurrentMode());
			serviceContext.addService(OTViewFactory.class, factory);
		} else {
			viewFactoryServiceAlreadyExisted = true;
		}
    	final OTMainFrame existingMainFrame = (OTMainFrame) serviceContext.getService(OTMainFrame.class);
		if (existingMainFrame == null) {
			OTMainFrame mainFrame = new OTMainFrame() {

				public OTFrame getFrame()
				{
					return OTViewBundleAbstract.this.getFrame();
				}

				public boolean getShowLeftPanel()
				{
					return OTViewBundleAbstract.this.getShowLeftPanel();
				}
			};
			serviceContext.addService(OTMainFrame.class, mainFrame);
		} else {
			OTMainFrame mainFrame = new OTMainFrame() {
				public OTFrame getFrame()
				{
					if(isResourceSet("frame")){
						return OTViewBundleAbstract.this.getFrame();
					}
					return existingMainFrame.getFrame();
				}

				public boolean getShowLeftPanel()
				{
					if(isResourceSet("showLeftPanel")){
						return OTViewBundleAbstract.this.getShowLeftPanel();
					}
					return existingMainFrame.getShowLeftPanel();
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
    		
    		// this is changed due to class interdepencency it doesn't need
    		// to be changed to use the new Abstract OTClass pattern
    		// factory.addViewBundle(this);    		
    	}
    }

}
