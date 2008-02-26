/*
 *  Copyright (C) 2008  The Concord Consortium, Inc.,
 *  25 Love Lane, Concord, MA 01742
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


package org.concord.otrunk.view;


import java.net.URL;
import java.util.HashMap;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.AbstractOTView;
import org.concord.framework.otrunk.view.OTXHTMLView;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.xml.XMLDatabase;


public class OTMultiUserRootView extends AbstractOTView implements OTXHTMLView 
{
	private HashMap dbMap = new HashMap(); // key: url, value: database
	
	
	public String getXHTMLText(OTObject otObject) {
		System.out.println("OTMultiUserRootView.getXHTMLText()");
		
		OTrunk otrunk = (OTrunk) getViewService(OTrunk.class);
		OTrunkImpl otrunkImpl = (OTrunkImpl) otrunk;
		
	    OTMultiUserRoot root = (OTMultiUserRoot) otObject;
	    OTObjectList userDatabases = root.getUserDatabases();
	    for (int i = 0; i < userDatabases.size(); i++) {
	    	OTUserDatabaseRef ref = (OTUserDatabaseRef) userDatabases.get(i);
	    	URL url = ref.getUrl();
	    	
	    	if (dbMap.containsKey(url)) {
	    		continue;
	    	}
	    	
	    	try {
	    		XMLDatabase db = new XMLDatabase(url);
	    		db.loadObjects();
	    		otrunkImpl.registerUserDataDatabase(db, null);
	    		dbMap.put(url, db);
	    	}
	    	catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    OTObject reportTemplate = (OTObject) root.getReportTemplate();
	    return "<object refid=\"" + reportTemplate.otExternalId() + "\"/>";
    }

}
