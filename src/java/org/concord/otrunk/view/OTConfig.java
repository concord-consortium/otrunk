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
	public final static String REMOTE_SAVE_DATA_PROP = "otrunk.remote_save_data";
	public final static String REST_ENABLED_PROP = "otrunk.rest_enabled";
	public final static String ROOT_OBJECT_PROP = "otrunk.root.localid";
	public final static String VIEW_MODE_PROP = "otrunk.view.mode";
	public final static String CODEBASE_PROP = "otrunk.codebase";
	public final static String SHOW_STATUS_PROP = "otrunk.view.status";
	public final static String SHOW_USER_DATA_WARNING = "otrunk.view.user_data_warning";
	public final static String USERDATA_URL_PROP = "otrunk.userdata_url";
	public final static String SHOW_DESTRUCTIVE_MENU_ITEMS_PROP = "otrunk.view.destructive_menu";	// "New" and "Open"
	public final static String TRACE_DB_LOAD_TIME = "otrunk.db.trace.load";
	public final static String USE_ASM = "otrunk.use.asm";
	public final static String IGNORE_LOAD_ERRORS = "otrunk.db.ignore_errors";
	public final static String IGNORE_SAIL_VIEW_MODE = "otrunk.debug.ignore_sail_view_mode";
	public final static String USE_ALTERNATIVE_EXPORT = "otrunk.export.use_alternative";
	public final static String SILENT_DB = "otrunk.db.silent";
	
	/**
     * This is yet another hack to support something like layers or mutliple files.
     * If this is set to url that url will be loaded in first and the OTSystem 
     * will be used from that instead from the regular url.
     * 
     */
    public final static String SYSTEM_OTML_PROP = "otrunk.system.otml";
    
	public static final String PERIODIC_UPLOADING_USER_DATA = "otrunk.periodic.uploading.enabled";
	public static final String PERIODIC_UPLOADING_USER_DATA_URL = "otrunk.periodic.uploading.url";
	public static final String PERIODIC_UPLOADING_USER_DATA_INTERVAL = "otrunk.periodic.uploading.interval";

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
        	String value = System.getProperty(property, null);
        	if(value == null){
        		return defaultValue;
        	} else {
        		return Boolean.parseBoolean(value);
        	}
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
	
	public static String getStringProp(String property, String defaultValue) {
		String val = getStringProp(property);
		if (val == null) {
			val = defaultValue;
		}
		return val;
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

	public static boolean isNoUserMode()
	{
		return getBooleanProp(NO_USER_PROP, false);
	}
	
	public static boolean isSingleUserMode()
	{
		return getBooleanProp(SINGLE_USER_PROP, false);
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
	
	public static boolean isShowDestructiveMenuItems()
	{
		return getBooleanProp(SHOW_DESTRUCTIVE_MENU_ITEMS_PROP, false);
	}
	
	public static boolean isIgnoreSailViewMode()
	{
		return getBooleanProp(IGNORE_SAIL_VIEW_MODE, false);
	}
	
	public static boolean isUseAlternativeExport()
	{
		return getBooleanProp(USE_ALTERNATIVE_EXPORT, false);
	}

	public static boolean isShowUserDataWarning()
    {
		return getBooleanProp(SHOW_USER_DATA_WARNING, false);
    }

	public static boolean isPeriodicUploadingUserDataEnabled()
    {
		return getBooleanProp(PERIODIC_UPLOADING_USER_DATA, false);
    }
	
	public static String getPeriodicUploadingUserDataUrl() {
		return getStringProp(PERIODIC_UPLOADING_USER_DATA_URL, null);
	}
	
	public static int getPeriodicUploadingUserDataInterval() {
		String in = getStringProp(PERIODIC_UPLOADING_USER_DATA_INTERVAL, "300000");
		int i = 300000;
		try {
			i = Integer.parseInt(in);
		} catch (NumberFormatException e) { }
		return i;
	}
}
