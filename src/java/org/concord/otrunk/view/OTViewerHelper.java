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

package org.concord.otrunk.view;

import java.awt.Component;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Vector;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTUser;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.OTExternalAppService;
import org.concord.framework.otrunk.view.OTFrameManager;
import org.concord.framework.otrunk.view.OTJComponentServiceFactory;
import org.concord.framework.otrunk.view.OTUserListService;
import org.concord.framework.otrunk.view.OTViewContext;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.framework.text.UserMessageHandler;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.OTrunkServiceEntry;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.xml.ExporterJDOM;
import org.concord.otrunk.xml.XMLDatabase;
import org.concord.view.PrintUserMessageHandler;
import org.concord.view.SwingUserMessageHandler;

public class OTViewerHelper 
{
	private OTrunk otrunk;
	private OTViewFactory viewFactory;
	private OTDatabase otDB;
	OTUserSession userSession;
	private OTDatabase userOtDB;	
	OTFrameManagerImpl frameManager; 

	private int userMode = OTConfig.NO_USER_MODE;

	private ArrayList<OTrunkServiceEntry<?>> services = new ArrayList<OTrunkServiceEntry<?>>();
	
	URL authoredContentURL = null;

	public final static String ANON_SINGLE_USER_NAME = "anon_single_user";

	public static OTUserObject createUser(String name, OTObjectService objService)
	throws Exception
	{
		OTUserObject user = (OTUserObject)objService.createObject(OTUserObject.class); 
		user.setName(name);
		return user;
	}

	public static URL getURLFromArgs(String[] args)
	{
		if (args.length > 0) {
			if (args[0].equals("-f")) {
				if (args.length > 1) {
					File inFile = new File(args[1]);
					try {
						return inFile.toURL();
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			} else if (args[0].equals("-r")) {
				if (args.length > 1) {
					ClassLoader cl = OTViewer.class.getClassLoader();
					return cl.getResource(args[1]);
				}
			} else {
				String urlStr = args[0];
				try {
	                return new URL(urlStr);
                } catch (MalformedURLException e) {
	                e.printStackTrace();
	                return null;
                }
			}
		}
		
		return null;
	}
	
	public OTViewerHelper()
	{
		if (OTConfig.getBooleanProp(OTConfig.SINGLE_USER_PROP, false)) {
			setUserMode(OTConfig.SINGLE_USER_MODE);
		} else if (OTConfig.getBooleanProp(OTConfig.NO_USER_PROP, false)) {
			setUserMode(OTConfig.NO_USER_MODE);
		}
	}

	public void init(String [] args) throws Exception
	{
		URL url = OTViewerHelper.getURLFromArgs(args);
		
		loadAuthoredContentURL(url);
		
		String userDataUrlStr = OTConfig.getStringProp(OTConfig.USERDATA_URL_PROP);
		if(getUserMode() == OTConfig.SINGLE_USER_MODE &&
				 userDataUrlStr != null){
			URL userDataUrl = new URL(userDataUrlStr);
			OTDatabase otDB = loadOTDatabase(userDataUrl.openStream(), userDataUrl, 
				System.out, false);
			loadUserData(otDB, null);
		}

	}
	
	public void loadOTrunkNoViewSystem(OTDatabase otDB)
		throws Exception
	{
		this.otDB = otDB;
		addService(UserMessageHandler.class, new PrintUserMessageHandler());
		otrunk = new OTrunkImpl(null, otDB, services);
		
	}
		
	/**
	 * The 2 is used so there are not two methods with the same number of Object arguments.
	 * That would break the backward compatibility if a class was calling loadOTrunk with null
	 * 
	 * @param systemDB
	 * @param otDB
	 * @throws Exception
	 */
	public void loadOTrunk2(OTDatabase systemDB, OTDatabase otDB)
	throws Exception
	{
		this.otDB = otDB;
		
		otrunk = new OTrunkImpl(systemDB, otDB, services);

		initOTrunk(otrunk);		
	}

	public void initOTrunk(OTrunk ot)
	{
		this.otrunk = ot;
		
		// only update the instance field if there is a valid view factory
		OTViewFactory myViewFactory =
		    (OTViewFactory) ot.getService(OTViewFactory.class);

		if(myViewFactory != null){
			viewFactory = myViewFactory;
		}

		if(viewFactory != null){
			// Maybe this shouldn't happen here
			frameManager = new OTFrameManagerImpl();
			frameManager.setViewFactory(viewFactory);

			OTViewContext factoryContext = viewFactory.getViewContext();

			factoryContext.addViewService(OTrunk.class, ot);
			factoryContext.addViewService(OTFrameManager.class, frameManager);
			factoryContext.addViewService(OTJComponentServiceFactory.class,
				new OTJComponentServiceFactoryImpl());
			factoryContext.addViewService(OTExternalAppService.class,
				new OTExternalAppServiceImpl());
			factoryContext.addViewService(OTUserListService.class, new OTUserListService() {
				public Vector getUserList() {
		            return ((OTrunkImpl)otrunk).getUsers();
	            }
			});
		}
		
	}
	
	/**
	 * 
	 * @param otDB
	 * @param parentComponent can be null if the parent component is not known.  This component
	 *  is used to setup a message service which shows dialogs.  In order for those dialogs to
	 *  centered on a component that component must be passed in.
	 * @throws Exception
	 */
	public void loadOTrunk(OTDatabase otDB, Component parentComponent)
	throws Exception
	{		
		addService(UserMessageHandler.class, new SwingUserMessageHandler(parentComponent));
		loadOTrunk2(null, otDB); 
	}

	public OTDatabase loadOTDatabase(URL url)
	throws Exception
	{
		return loadOTDatabase(url.openStream(), url, System.err, false);
	}

	public OTDatabase loadOTDatabaseXML(URL url)
	throws Exception
	{
		URLConnection urlConn = url.openConnection();
		urlConn.setRequestProperty("Content-Type", "application/xml");

		InputStream input = urlConn.getInputStream();

		return loadOTDatabase(input, url);
	}	

	public OTDatabase loadOTDatabase(InputStream input, URL url)
	throws Exception
	{
		return loadOTDatabase(input, url, System.err, false);
	}
	
	public void showParseException(org.jdom.input.JDOMParseException e)
	{
		String xmlWarningTitle = "XML Decoding error";
		String xmlWarningMessage =
			"There appears to a problem parsing the XML of this document. \n"
			+ "Please show this error message to one of the workshop leaders. \n\n"
			+ e.getMessage();
		UserMessageHandler messageHandler = getMessageHander();
		messageHandler.showMessage(xmlWarningMessage, xmlWarningTitle);		
	}
	
	public OTDatabase loadOTDatabase(InputStream input, URL url, PrintStream loggingStream, 
		boolean trackResources)
	throws Exception
	{
		setupMessageHandler();
		
		OTDatabase localOtDB;
		try {
			localOtDB = new XMLDatabase(input, url, loggingStream);
			((XMLDatabase)localOtDB).setTrackResourceInfo(trackResources);
			((XMLDatabase)localOtDB).loadObjects();
			
		} catch (org.jdom.input.JDOMParseException e) {
			showParseException(e);
			throw e;
		}		
		
		return localOtDB;
	}

	public OTDatabase loadOTDatabase(Reader reader, URL url)
	throws Exception
	{
		setupMessageHandler();
		
		OTDatabase localOtDB;
		try {
			localOtDB = new XMLDatabase(reader, url, System.err);
			((XMLDatabase)localOtDB).loadObjects();
			
		} catch (org.jdom.input.JDOMParseException e) {
			showParseException(e);
			throw e;
		}		
		
		return localOtDB;
	}

	public void saveOTDatabase(OTDatabase otDB, OutputStream output)
	throws Exception
	{
		ExporterJDOM.export(output, otDB.getRoot(), otDB);
	}

	public String saveOTDatabase(OTDatabase otDB)
	throws Exception
	{
		StringWriter userDataWriter = new StringWriter();
		ExporterJDOM.export(userDataWriter, otDB.getRoot(), otDB);

		return userDataWriter.toString();
	}

	public void saveOTDatabaseXML(OTDatabase otDB, URL url)
	throws Exception
	{
		saveOTDatabaseXML(otDB, url, "PUT");
	}

	/**
	 * This will send the xml of a OT Database to a url.  This method sets
	 * the content-type to "application/xml" 
	 * 
	 * The "method" parameter is only used if the connection object returned by
	 * url.openConnection is an instanceof HttpURLConnection 
	 * 
	 * @param otDB
	 * @param url
	 * @param method
	 * @throws Exception
	 */
	public void saveOTDatabaseXML(OTDatabase otDB, URL url, String method)
	throws Exception
	{
		URLConnection urlConn = url.openConnection();
		System.out.println(urlConn);
		urlConn.setDoOutput(true);
		urlConn.setRequestProperty("Content-Type", "application/xml");
		if(urlConn instanceof HttpURLConnection){
			if(method == null || method.length() == 0) {
				method = "PUT";
			}
			((HttpURLConnection)urlConn).setRequestMethod(method);
		}			
		// url
		OutputStream outStream = urlConn.getOutputStream();

		ExporterJDOM.export(outStream, otDB.getRoot(), otDB);

		outStream.flush();
		outStream.close();

		InputStream response = urlConn.getInputStream();

		// It seems like I have to read the response other wise the put isn't accepted.		
		byte [] inBytes = new byte [1000];
		response.read(inBytes);

		response.close();

		if(urlConn instanceof HttpURLConnection) {
			((HttpURLConnection)urlConn).disconnect();
		}
	}	

	/**
	 * 
	 * @param frame
	 * @return
	 */
	public OTViewContainerPanel createViewContainerPanel()
	{
		OTViewContainerPanel otContainer = new OTViewContainerPanel(frameManager);
		otContainer.setTopLevelContainer(true);

		otContainer.setOTViewFactory(getViewFactory());

		return otContainer;
	}

	public void loadUserData(OTDatabase userOtDB, String name)
	throws Exception
	{
		loadUserSession(new OTMLUserSession((XMLDatabase)userOtDB, name));
	}

	public void loadUserSession(OTUserSession userSession) throws Exception
	{
		this.userSession = userSession;
		userSession.setOTrunk(otrunk);
		userSession.load();		 
	}
	
	/**
	 * This does not check for unsaved user data
	 * 
	 */
	public void newAnonUserData()
	{
		newUserData(ANON_SINGLE_USER_NAME);
	}
	
	/**
	 * This does not check for unsaved user data, it uses the current OTUserSession object
	 * to create the new user data.  If there is no OTUserSession object it will throw an
	 * IllegalStateException
	 * 
	 */
	public void newUserData(String userName)
	{
		// call some new method for creating a new un-saved user state
		// this should set the currentUserFile to null, so the save check
		// prompts
		// for a file name
		if(userSession == null) {
			throw new IllegalStateException("a OTUserSession must be supplied before newUserData can be called");
		}
		
		try {
			userSession.newLayer();
			
			OTUserObject userObject = userSession.getUserObject();
			if(userName != null){
				userObject.setName(userName);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}	

	public void loadAuthoredContentURL(URL url)
	throws Exception
	{
		XMLDatabase systemDB = null;

		try {
			// try loading in the system object if there is one
			String systemOtmlUrlStr =
				OTConfig.getStringProp(OTConfig.SYSTEM_OTML_PROP);
			if(systemOtmlUrlStr != null){
				URL systemOtmlUrl = new URL(systemOtmlUrlStr);

				// don't track the resource info on the system db.
				systemDB = (XMLDatabase) loadOTDatabase(systemOtmlUrl.openStream(), systemOtmlUrl,
					System.out, false);
			}
		} catch (Exception e){
			e.printStackTrace();
			systemDB = null;
		}

		boolean trackResourceInfo = getUserMode() == OTConfig.NO_USER_MODE;
		otDB = loadOTDatabase(url.openStream(), url, System.out, trackResourceInfo);
			
		loadOTrunk2(systemDB, otDB);
		
		authoredContentURL = url;		
	}	
	
	protected UserMessageHandler setupMessageHandler()
	{
		// Check to see if we need to add a user message handler ourselves
		UserMessageHandler messageHandler = 
			(UserMessageHandler) findService(UserMessageHandler.class);
	
		if(messageHandler == null){
			messageHandler = new PrintUserMessageHandler();
			addService(UserMessageHandler.class, messageHandler);
		}
		return messageHandler;
	}
	
	/**
	 * Find the registered message handler, add a PrintUserMessageHandler.  
	 * 
	 * @return
	 */
	public UserMessageHandler getMessageHander()
	{	
		return setupMessageHandler();
	}

	public Object findService(Class serviceInterface)
	{
		for(int i=0; i<services.size(); i++){
			OTrunkServiceEntry entry = (OTrunkServiceEntry) services.get(i);
			if(serviceInterface.equals(entry.serviceInterface)){
				return entry.service;
			}
		}

		return null;
	}
	
	public OTObject getAuthoredRoot()
	throws Exception
	{
		String rootLocalId =
			OTConfig.getStringProp(OTConfig.ROOT_OBJECT_PROP);
		if (rootLocalId != null && otDB instanceof XMLDatabase) {
			OTID rootID = ((XMLDatabase)otDB).getOTIDFromLocalID(rootLocalId);
			return otrunk.getOTObject(rootID);
		}
		return otrunk.getRoot();
	}

	public OTObject getRootObject() 
	throws Exception
	{
		OTObject root = getAuthoredRoot();
		OTUser user = getCurrentUser();
		if(user != null){
			return getOtrunk().getUserRuntimeObject(root, user);
		}

		return root;
	}

	public OTrunk getOtrunk() 
	{
		return otrunk;
	}

	public OTViewFactory getViewFactory() 
	{
		return viewFactory;
	}

	public OTDatabase getOtDB() 
	{
		return otDB;
	}

	public OTDatabase getUserOtDB()
	{
		return userOtDB;
	}

	public OTUserSession getUserSession()
	{
		return userSession;
	}
	
	public OTUser getCurrentUser()
	{
		if(userSession == null) {
			return null;
		}
		return userSession.getUserObject();
	}

	public void setUserMode(int userMode)
    {
	    this.userMode = userMode;
    }

	public int getUserMode()
    {
	    return userMode;
    }
	
	/**
	 * this needs to be called before initialized.
	 * 
	 * @param serviceInterface
	 * @param service
	 */
	public <T> void addService(Class<T> serviceInterface, T service)
	{
		OTrunkServiceEntry<T> entry = new OTrunkServiceEntry<T>(service, serviceInterface);
		services.add(entry);
	}
}
