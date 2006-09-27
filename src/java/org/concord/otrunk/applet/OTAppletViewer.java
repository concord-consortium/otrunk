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
import java.awt.BorderLayout;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.otrunk.OTUserListService;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.view.OTFrameManager;
import org.concord.otrunk.view.OTViewContainerPanel;
import org.concord.otrunk.view.OTViewFactory;
import org.concord.otrunk.view.OTViewService;
import org.concord.otrunk.xml.XMLDatabase;
import org.concord.view.SwingUserMessageHandler;

public class OTAppletViewer extends JApplet {
	OTViewFactory viewFactory;
	OTrunk otrunk;
	XMLDatabase xmlDB;
	boolean masterLoaded = false;
	private OTAppletViewer master;
	
	public void init() {
		super.init();

		String urlString = getParameter("url");

		System.out.println("I " + getParameter("name") + " started init");

		if(urlString == null) {
			return;			
		}
		
		try {			
			URL url = new URL(getDocumentBase(), urlString);

			xmlDB = new XMLDatabase(url, System.err);

			otrunk = new OTrunkImpl(xmlDB,
					new Object[] { new SwingUserMessageHandler(this),
							new OTUserListService() });

			OTViewService viewService = (OTViewService) otrunk
					.getService(OTViewService.class);

			viewFactory = null;
			if (viewService != null) {
				viewFactory = viewService.getViewFactory(otrunk);
			}

			masterLoaded = true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	}
	
	public void start() {
		// TODO Auto-generated method stub
		super.start();

		System.out.println("I " + getParameter("name") + " started start");

		if(isMasterLoaded()){
			setupView();
		}
		
		if(isMaster()){
			Enumeration applets = getAppletContext().getApplets();
			while(applets.hasMoreElements()){
				Applet a = (Applet)applets.nextElement();
				if(a instanceof OTAppletViewer &&
						!((OTAppletViewer)a).isMaster()){
					((OTAppletViewer)a).masterFinishedLoading(this);
				}
			}
		}
	}
	
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
	}

	protected void setupView()
	{
		// get the otml url
		try {
			// look up view container with the frame.
			OTViewContainerPanel otContainer = new OTViewContainerPanel(
					new OTFrameManager() {
						public void setFrameObject(OTObject otObject,
								OTFrame otFrame) {
							// TODO Auto-generated method stub

						}
					}, null);

			otContainer.setOTViewFactory(getViewFactory());

			getContentPane().setLayout(new BorderLayout());

			getContentPane().add(otContainer, BorderLayout.CENTER);

			// call setCurrentObject on that view container with a null
			// frame
			OTObject root;
			root = getOTrunk().getRoot();

			OTID id = getID(getParameter("refid"));
			
			OTObject appletObject = 
				((DefaultOTObject)root).getReferencedObject(id);
			
			otContainer.setCurrentObject(appletObject, null);
			
			System.out.println("I " + getParameter("name") + " finished setupView");
			//repaint();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public boolean isMaster(){
		return "master".equals(getParameter("name"));		
	}
	
	public OTAppletViewer getMaster(){
		if(isMaster()){
			return this;
		}
		
		if(master != null) {
			return master;
		}
		
		Enumeration applets = getAppletContext().getApplets();
		while(applets.hasMoreElements()){
			Applet a = (Applet)applets.nextElement();
			System.out.println("I " + a.getParameter("name") + " found applet: " + a);
			System.out.println("  name: " + a.getParameter("name"));
			if(a instanceof OTAppletViewer &&
					((OTAppletViewer)a).isMaster()){
				master = (OTAppletViewer)a;
				return master;
			}
		}

		return null;
//		return (OTAppletViewer)getAppletContext().getApplet("master");		
	}
	
	public boolean isMasterLoaded(){
		if(isMaster()) {
			return masterLoaded;
		}

		if(getMaster() != null){
			return getMaster().isMasterLoaded();
		}
		
		return false;
	}
	
	public void masterFinishedLoading(OTAppletViewer master){
		this.master = master;
		//we might not be in the correct thread
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				setupView();
			}
		});
	}
	
	public OTViewFactory getViewFactory(){
		if(isMaster()) {
			return viewFactory;
		}
		
		// try to get the viewfactory from the master applet
		return getMaster().getViewFactory();
	}
	
	public OTrunk getOTrunk(){
		if(isMaster()) {
			return otrunk;
		}

		// try to get the viewfactory from the master applet
		return getMaster().getOTrunk();
	}
	
	public OTID getID(String id){
		if(isMaster()){
			return xmlDB.getOTIDFromLocalID(id);
		}
		
		// try to get the viewfactory from the master applet
		return getMaster().getID(id);
	}
}
