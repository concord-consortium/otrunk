package org.concord.otrunk.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InstallationResponseCache extends ResponseCache
{
	private static final Logger logger =
        Logger.getLogger(InstallationResponseCache.class.getCanonicalName());
	private static File CACHE_DIR;
	private static boolean TEMP_DIR = true;
	private static HashMap<String, String> LOOKUP_MAP;
	static {
		try {
			CACHE_DIR = File.createTempFile("otrunk_cache_", "");
			CACHE_DIR.delete();
			CACHE_DIR.mkdir();
			CACHE_DIR.deleteOnExit();
			if(!CACHE_DIR.isDirectory()){
				System.err.println("Can't create cache dir");
				CACHE_DIR = null;
			}
			logger.info("Cache dir: " + CACHE_DIR.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CACHE_DIR = null;
		}
	}
	
	public static void registerLookupMap() {
		try {
			File lookupFile = new File(CACHE_DIR.toString() + File.pathSeparator + "url_map.xml");
			if (lookupFile.exists()) {
				LOOKUP_MAP = readLookupMap(lookupFile);
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, "Can't open url map file!", e);
			LOOKUP_MAP = null;
		}
	}
	
	public static void installResponseCache(File cacheDir) {
		CACHE_DIR = cacheDir;
		TEMP_DIR = false;
		registerLookupMap();
		ResponseCache.setDefault(new InstallationResponseCache());
	}
	
	public static void installResponseCache() {
		if (CACHE_DIR != null) {
			registerLookupMap();
			ResponseCache.setDefault(new InstallationResponseCache());
		}
	}
	
	public static void clearCache() {
		if (CACHE_DIR != null) {
			delDir(CACHE_DIR);
		}
	}
	
	private static void delDir(File dir) {
		if (dir.isDirectory()) {
			for (File child : dir.listFiles()) {
				if (child.getName().equals(".") || child.getName().equals("..")) {
					continue;
				}
				delDir(child);
				child.delete();
			}
		}
	}
	
	@Override
	public CacheResponse get(URI uri, String rqstMethod, Map<String, List<String>> rqstHeaders)
	    throws IOException
	{
		File localFile = getLocalFile(uri);
		if (!localFile.exists()) {
			logger.info("Cache miss! (" + uri.toString() + ")");
			// the file isn't already in our cache, return null
			return null;
		}
		logger.info("Cache hit! (" + uri.toString() + ")");
		return new InstallationCacheResponse(localFile);
	}

	@Override
	public CacheRequest put(URI uri, URLConnection conn)
	    throws IOException
	{
		// only cache http(s) GET requests
		if (conn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			if (httpConn.getRequestMethod().equals("GET") && isURICachable(uri)) {
				File localFile = getLocalFile(uri);
				logger.info("Caching file: " + uri.toString());
				return new InstallationCacheRequest(localFile, httpConn);
			}
		}
		logger.info("Skipping file: " + uri.toString());
		return null;
	}
	
	/**
	 * Returns the local File corresponding to the given remote URI.
	 */
	public static File getLocalFile(URI remoteUri) {
		File localFile = null;
		if (LOOKUP_MAP != null) {
			String fileName = LOOKUP_MAP.get(remoteUri.toString());
			if (fileName != null) {
				localFile = new File(CACHE_DIR, fileName);
			}
		}
		if (localFile == null) {
			int code = remoteUri.hashCode();
			String fileName = Integer.toString(code >= 0 ? code : -code);
			localFile = new File(CACHE_DIR, fileName);
		}
		if (TEMP_DIR) {
			localFile.deleteOnExit();
		}
		return localFile;
	}
	
	private static 	HashMap<String, String> readLookupMap(File mapFile) throws IOException
	{
		Properties properties = new Properties();
		properties.loadFromXML(new FileInputStream(mapFile));

		HashMap<String, String> map = new HashMap<String, String>();
		
		Set<Entry<Object,Object>> entrySet = properties.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			map.put(key, value);
		}

		return map;
	}
	
	private boolean isURICachable(URI uri) {
		// TODO We might want to implement more complex URI filtering here...
		return true;
	}

}
