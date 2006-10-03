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

package org.concord.otrunk.applet;

import java.applet.Applet;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import org.concord.otrunk.xml.XMLDatabase;

public class OTAppletViewer extends OTAbstractAppletViewer 
{
	public void init() 
	{
		super.init();

		System.out.println("" + getAppletName() + " started init");

		String urlString = getParameter("url");		
		// If the url is not set then
		// this is not the master applet on the page
		// so the state does not need to be loaded
		if(urlString == null) {
			return;			
		}

		loadState();
	}
	
	public void start() 
	{
		super.start();

		System.out.println("" + getAppletName() + " started start");

		if(isMasterLoaded()){
			setupView();
		}
		
		if(isMaster()){
			Enumeration applets = getAppletContext().getApplets();
			while(applets.hasMoreElements()){
				Applet a = (Applet)applets.nextElement();
				if(a instanceof OTAppletViewer &&
						!((OTAppletViewer)a).isMaster()){
					System.out.println("" + getAppletName() + " calling finishedLoading on " + a.getParameter("name"));
					((OTAppletViewer)a).masterFinishedLoading(this);
				}
			}
		}
	}
	
	protected void openOTDatabase()
		throws Exception
	{
		String urlString = getParameter("url");

		if (urlString == null || urlString.equals("")){
			System.err.println("No url load state specified");
			throw new Exception("No url load state specified");
		}
		
		try {
			URL url = new URL(getDocumentBase(), urlString);

			URLConnection urlConn = url.openConnection();
			urlConn.setRequestProperty("Content-Type", "application/xml");
				
			InputStream input = urlConn.getInputStream();
			
			xmlDB = new XMLDatabase(input, url, System.err);

		}catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}				
	}		
}
