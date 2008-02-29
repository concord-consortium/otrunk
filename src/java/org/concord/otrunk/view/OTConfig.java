package org.concord.otrunk.view;

import java.security.AccessControlException;

public class OTConfig
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
	public final static String SHOW_STATUS_PROP = "otrunk.view.status";
	public final static String USERDATA_URL_PROP = "otrunk.userdata_url";
	public final static String REMOTE_SAVE_DATA_PROP = "otrunk.remote_save_data";
	
	
	/**
     * This is yet another hack to support something like layers or mutliple files.
     * If this is set to url that url will be loaded in first and the OTSystem 
     * will be used from that instead from the regular url.
     * 
     */
    public final static String SYSTEM_OTML_PROP = "otrunk.system.otml";

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
    	if(OTConfig.cannotReadProperties){
    		return defaultValue;
    	}
    
    	try {
    		return Boolean.getBoolean(property);
    	} catch (AccessControlException e){			
    		OTConfig.handlePropertyReadException(e);
    		return defaultValue;
    	}		
    }

	public static String getStringProp(String property)
    {
    	if(OTConfig.cannotReadProperties){
    		return null;
    	}
    
    	try {
    		return System.getProperty(property);
    	} catch (AccessControlException e){			
    		OTConfig.handlePropertyReadException(e);
    		return null;
    	}				
    }

	protected static boolean cannotReadProperties = false;

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

	public static boolean isAuthorMode()
    {
    	return getBooleanProp(AUTHOR_PROP, false);
    }

	public static boolean isRestEnabled()
    {
    	return getBooleanProp(REST_ENABLED_PROP, true);
    }

	public static boolean isShowStatus()
    {
    	return getBooleanProp(SHOW_STATUS_PROP, false);
    }

	public static String getSystemPropertyViewMode()
    {
    	return getStringProp(VIEW_MODE_PROP);
    }
	
	public static boolean isRemoteSaveData()
    {
    	return getBooleanProp(REMOTE_SAVE_DATA_PROP, false);
    }

}
