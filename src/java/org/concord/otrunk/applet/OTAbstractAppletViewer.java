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
import java.awt.event.ActionEvent;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;
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
import org.concord.otrunk.xml.Exporter;
import org.concord.otrunk.xml.XMLDatabase;
import org.concord.view.SwingUserMessageHandler;

public abstract class OTAbstractAppletViewer extends JApplet
{
	protected OTViewFactory viewFactory;
	protected OTrunk otrunk;

	protected XMLDatabase xmlDB;
	protected boolean masterLoaded = false;
	protected OTAbstractAppletViewer master;
	protected Action stateAction;
	protected JButton authorSaveButton;

	public OTAbstractAppletViewer()
	{
		super();
	}

	public String getAppletName()
	{
		return getParameter("name");
	}

	public void init() 
	{
		super.init();
	}	
		
	public void stop() 
	{
		super.stop();
	}
	
	protected abstract void openOTDatabase() throws Exception;
	
	protected void loadState()
	{
		try {

			//Open xmlDB
			openOTDatabase();
			
			otrunk = new OTrunkImpl(xmlDB,
					new Object[] { new SwingUserMessageHandler(this),
					new OTUserListService() });

			OTViewService viewService = (OTViewService)otrunk.getService(OTViewService.class);

			viewFactory = null;
			if (viewService != null) {
				viewFactory = viewService.getViewFactory(otrunk);
			}

			masterLoaded = true;
			master = this;
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}

	public void setupView()
	{
		System.out.println("" + getAppletName() + " start setupView");
	
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
	
			getContentPane().removeAll();
			
			getContentPane().add(otContainer, BorderLayout.CENTER);
	
			// call setCurrentObject on that view container with a null
			// frame
			OTObject root;
			root = getOTrunk().getRoot();
	
			String refid = getParameter("refid");
			OTObject appletObject = root;
			if(refid != null && refid.length() > 0){
				OTID id = getID(refid);
	
				appletObject = ((DefaultOTObject)root).getReferencedObject(id);
			}
			
			otContainer.setCurrentObject(appletObject, null);
			
			///////////////////////////////
			String saveUrlString = getParameter("author_state_save_url");
			if (saveUrlString == null){
				//Don't save
			}
			else{
				stateAction = new StateHandlerAction();
				
				//Save author content button
				authorSaveButton = new JButton("Save");
				authorSaveButton.setActionCommand("save_author");
				authorSaveButton.addActionListener(stateAction);
				
				JPanel buttonPanel = new JPanel();
				buttonPanel.add(authorSaveButton);
				
				getContentPane().add(buttonPanel, BorderLayout.SOUTH);
			}
			///////////////////////////////
			
			System.out.println("" + getAppletName() + " finished setupView");
			//repaint();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public OTObject getOTObject()
		throws Exception
	{
		// call setCurrentObject on that view container with a null
		// frame
		OTObject root = getOTrunk().getRoot();
	
		String refid = getParameter("refid");
		OTObject appletObject = root;
		if(refid != null && refid.length() > 0){
			OTID id = getID(refid);
	
			appletObject = ((DefaultOTObject)root).getReferencedObject(id);
		}
	
		return appletObject;
	}

	public boolean isMaster()
	{
		return true;
		//return this == master;
	}

	public OTAbstractAppletViewer getMaster()
	{
			if(isMaster()){
				return this;
			}
			
			if(master != null) {
				return master;
			}
			
			Enumeration applets = getAppletContext().getApplets();
			while(applets.hasMoreElements()){
				Applet a = (Applet)applets.nextElement();
				System.out.println("" + a.getParameter("name") + " found: " +
						a.getParameter("name") + " applet.toString: " + a);
				if(a instanceof OTAppletViewer &&
						((OTAbstractAppletViewer)a).isMaster()){
					master = (OTAbstractAppletViewer)a;
					return master;
				}
			}
	
			return null;
	//		return (OTAppletViewer)getAppletContext().getApplet("master");		
		}

	public boolean isMasterLoaded()
	{
		if(isMaster()) {
			return masterLoaded;
		}
	
		if(getMaster() != null){
			return getMaster().isMasterLoaded();
		}
		
		return false;
	}

	public void masterFinishedLoading(OTAbstractAppletViewer master)
	{
		this.master = master;
		//we might not be in the correct thread
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				setupView();
			}
		});
	}

	public OTViewFactory getViewFactory()
	{
		if(isMaster()) {
			return viewFactory;
		}
		
		// try to get the viewfactory from the master applet
		return getMaster().getViewFactory();
	}

	public OTrunk getOTrunk()
	{
		if(isMaster()) {
			return otrunk;
		}
	
		// try to get the viewfactory from the master applet
		return getMaster().getOTrunk();
	}

	public OTID getID(String id)
	{
		if(isMaster()){
			return xmlDB.getOTIDFromLocalID(id);
		}
		
		// try to get the viewfactory from the master applet
		return getMaster().getID(id);
	}

	public void saveAuthorState()
	{
		String saveUrlString = getParameter("author_state_save_url");
		if (saveUrlString == null){
			//Don't save
			System.err.println("No author url specified for saving");
			return;
		}
				
		try{
			System.out.println("opening "+saveUrlString);
			URL saveUrl = new URL(getDocumentBase(), saveUrlString);
			System.out.println(saveUrl);
			URLConnection urlConn = saveUrl.openConnection();
			System.out.println(urlConn);
			urlConn.setDoOutput(true);
			OutputStream outStream = urlConn.getOutputStream();
			
			Exporter.export(outStream, xmlDB.getRoot(), xmlDB);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	class StateHandlerAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getActionCommand().equals("save_author")){
				saveAuthorState();
			}
			
		}
		
	}
}