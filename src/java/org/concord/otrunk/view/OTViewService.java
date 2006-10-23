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
 * $Revision: 1.6 $
 * $Date: 2006-10-23 04:59:20 $
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
    }
    
    ResourceSchema resources;
    
    public OTViewService(ResourceSchema resources)
    {
        super(resources);
        this.resources = resources;
    }

    public OTViewFactory getViewFactory(OTrunk otrunk)
    {
        OTViewFactoryImpl factory = new OTViewFactoryImpl(otrunk);
        
        // read in all the viewEntries and create a vector 
        // of class entries.
        OTObjectList viewEntries = resources.getViewEntries();
        ClassLoader loader = OTViewService.class.getClassLoader();
        
        for(int i=0; i<viewEntries.size(); i++) {
            OTViewEntry entry = (OTViewEntry)viewEntries.get(i);
            String objClassStr = entry.getObjectClass();
            String viewClassStr = entry.getViewClass();
            
            try {
                Class objectClass = loader.loadClass(objClassStr);
                Class viewClass = loader.loadClass(viewClassStr);
                factory.addViewEntry(objectClass, viewClass);
                
            } catch (ClassNotFoundException e) {
                System.err.println("Can't find view: " + viewClassStr + 
                        " for object: " + objClassStr);
                System.err.println("  error: " + e.toString());
            }
        }
        
        return factory;
    }

}
