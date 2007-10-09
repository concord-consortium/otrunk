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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlException;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTUser;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.OTFrameManager;
import org.concord.framework.otrunk.view.OTJComponentServiceFactory;
import org.concord.framework.otrunk.view.OTViewContext;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.framework.text.UserMessageHandler;
import org.concord.otrunk.OTObjectServiceImpl;
import org.concord.otrunk.OTStateRoot;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.user.OTUserObject;
import org.concord.otrunk.xml.ExporterJDOM;
import org.concord.otrunk.xml.XMLDatabase;
import org.concord.view.SwingUserMessageHandler;

public class OTViewerHelper 
{
	public final static int MULTIPLE_USER_MODE = 2;
	public final static int SINGLE_USER_MODE = 1;
	public final static int NO_USER_MODE = 0;
	public final static String NO_USER_PROP = "otrunk.view.no_user";
	public final static String SINGLE_USER_PROP = "otrunk.view.single_user";
	public final static String DEBUG_PROP = "otrunk.view.debug";
	public final static String TRACE_PROP = "otrunk.trace";
	public final static String TRACE_LISTENERS_PROP = "otrunk.trace.listeners";
	public final static String TRACE_PACKAGES_PROP = "otrunk.trace.packages";
	public final static String AUTHOR_PROP = "otrunk.view.author";
	public final static String REMOTE_URL_PROP = "otrunk.remote_url";
	public final static String REST_ENABLED_PROP = "otrunk.rest_enabled";
	public final static String ROOT_OBJECT_PROP = "otrunk.root.localid";
	public final static String VIEW_MODE_PROP = "otrunk.view.mode";
	public final static String CODEBASE_PROP = "otrunk.codebase";
	
	/**
	 * This is yet another hack to support something like layers or mutliple files.
	 * If this is set to url that url will be loaded in first and the OTSystem 
	 * will be used from that instead from the regular url.
	 * 
	 */
	public final static String SYSTEM_OTML_PROP = "otrunk.system.otml";	

	protected static boolean cannotReadProperties = false;

	private OTrunk otrunk;
	private OTViewFactory viewFactory;
	private OTDatabase otDB;
	private OTDatabase userOtDB;	
	private OTUser currentUser;
	OTFrameManagerImpl frameManager; 

	/**
	 * This method should be used to read properties because in some places
	 * properties cannot be read.  So this will catch the exception when that
	 * happens, and it will not try to read the properties again.
	 * 
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBooleanProp(String property, boolean defaultValue)
	{
		if(cannotReadProperties){
			return defaultValue;
		}

		try {
			return Boolean.getBoolean(property);
		} catch (AccessControlException e){			
			handlePropertyReadException(e);
			return defaultValue;
		}		
	}

	public static String getStringProp(String property)
	{
		if(cannotReadProperties){
			return null;
		}

		try {
			return System.getProperty(property);
		} catch (AccessControlException e){			
			handlePropertyReadException(e);
			return null;
		}				
	}

	protected static void handlePropertyReadException(AccessControlException e)
	{
		System.err.println(e);
		System.err.println("Cannot read system properties, defaults will be used");
		cannotReadProperties = true;		
	}

	public static boolean isDebug()
	{
		return getBooleanProp(DEBUG_PROP, false);
	}

	public static boolean isTrace()
	{
		return getBooleanProp(TRACE_PROP, false);
	}

	public static OTUserObject createUser(String name, OTObjectService objService)
	throws Exception
	{
		OTUserObject user = (OTUserObject)objService.createObject(OTUserObject.class); 
		user.setName(name);
		return user;
	}

	public OTViewerHelper()
	{
	}


	public void loadOTrunk(OTDatabase otDB, Component parentComponent)
	throws Exception
	{
		this.otDB = otDB;
		otrunk = new OTrunkImpl(otDB,
				new Object[] { new SwingUserMessageHandler(parentComponent)},
				new Class[] {UserMessageHandler.class});

		viewFactory = 
			(OTViewFactory) otrunk.getService(OTViewFactory.class);

		// Maybe this shouldn't happen here
		frameManager = new OTFrameManagerImpl();
		frameManager.setViewFactory(viewFactory);

		OTViewContext factoryContext = viewFactory.getViewContext();

		factoryContext.addViewService(OTrunk.class, otrunk);
		factoryContext.addViewService(OTFrameManager.class, frameManager);
		factoryContext.addViewService(OTJComponentServiceFactory.class, new OTJComponentServiceFactoryImpl());
	}

	public OTDatabase loadOTDatabase(URL url)
	throws Exception
	{
		XMLDatabase xmlDB = new XMLDatabase(url, System.err);
		xmlDB.loadObjects();
		return xmlDB;
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
		XMLDatabase xmlDB = new XMLDatabase(input, url, System.err);
		xmlDB.loadObjects();
		return xmlDB;
	}

	public OTDatabase loadOTDatabase(Reader reader, URL url)
	throws Exception
	{
		XMLDatabase xmlDB = new XMLDatabase(reader, url, System.err);
		xmlDB.loadObjects();
		return  xmlDB;
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
		this.userOtDB = userOtDB;
		currentUser = ((OTrunkImpl)otrunk).registerUserDataDatabase(userOtDB, name);
	}

	/**
	 * This does not check for unsaved user data.  So if you call this before saving
	 * a previous userOtDB then that work will be lost
	 *
	 */
	public void newAnonUserData()
	{
		try {
			// make a brand new userDB
			userOtDB = new XMLDatabase();

			OTObjectService objService = ((OTrunkImpl)otrunk).createObjectService(userOtDB);

			OTStateRoot stateRoot = (OTStateRoot)objService.createObject(OTStateRoot.class);
			userOtDB.setRoot(stateRoot.getGlobalId());
			stateRoot.setFormatVersionString("1.0");		
			((XMLDatabase)userOtDB).setDirty(false);

			OTUserObject userObject = createUser("anon_single_user", objService);

			((OTrunkImpl)otrunk).initUserObjectService((OTObjectServiceImpl)objService, userObject, stateRoot);

			currentUser = userObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public OTObject getRootObject() 
	throws Exception
	{
		OTObject root = getOtrunk().getRoot();
		if(currentUser != null){
			return getOtrunk().getUserRuntimeObject(root, currentUser);
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

	public OTUser getCurrentUser()
	{
		return currentUser;
	}
}
