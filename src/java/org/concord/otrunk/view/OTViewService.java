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
 * $Revision: 1.11 $
 * $Date: 2007-03-09 12:08:04 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.OTrunk;
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
public class OTViewService extends DefaultOTObject
{
    public static interface ResourceSchema extends OTResourceSchema {
        public OTObjectList getViewEntries();
        
        public OTObjectList getModes();
    }
    
    ResourceSchema resources;
    
    public OTViewService(ResourceSchema resources)
    {
        super(resources);
        this.resources = resources;
    }

    /**
     * If this method can be removed then this OTClass can be turned 
     * into a pure interface.  But the only way I can see to remove it
     * is to use a view or a wrapper which will need this object to 
     * already exist.  So the only way to really remove it then is to 
     * support this wrapper/controller or view as part of framework.  
     * But that makes the framework more complicated.  
     * 
     * The alternative is to support methods in the OTClasses.  Then
     * the entire view factory interface can be implemented by this. 
     * 
     * @param otrunk
     * @return
     */
    public OTViewFactory getViewFactory(OTrunk otrunk)
    {
        return new OTViewFactoryImpl(otrunk, this);        
    }

    public OTObjectList getViewEntries()
    {
    	return resources.getViewEntries();
    }
    
    public OTObjectList getModes()
    {
    	return resources.getModes();
    }
}
