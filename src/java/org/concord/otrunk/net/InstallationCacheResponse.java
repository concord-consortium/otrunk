package org.concord.otrunk.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.CacheResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

public class InstallationCacheResponse extends CacheResponse
{
	private FileInputStream fis;
	private Map<String, List<String>> headers;
	
	public InstallationCacheResponse(File localFile) {
		try {
			this.fis = new FileInputStream(localFile);
		} catch (FileNotFoundException ex) {
			// should not happen, since we already checked for existence
			ex.printStackTrace();
		}

		File headersFile = new File(localFile.getAbsolutePath() + ".hdrs");
		try {
			this.headers = readHeaders(headersFile);
			ArrayList<String> dateVal = new ArrayList<String>();
			dateVal.add((new Date()).toGMTString());
			headers.put("Date", dateVal);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public InputStream getBody()
	    throws IOException
	{
		return fis;
	}

	@Override
	public Map<String, List<String>> getHeaders()
	    throws IOException
	{
		return headers;
	}
	
	Map<String, List<String>> readHeaders(File headersFile) throws IOException
	{
		Properties properties = new Properties();
		properties.loadFromXML(new FileInputStream(headersFile));

		LinkedHashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		
		Set<Entry<Object,Object>> entrySet = properties.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			String key = (String) entry.getKey();
			ArrayList<String> valueList = new ArrayList<String>();
			valueList.add((String) entry.getValue());
			if(key.equals("_http_version")){
				key = null;
			}
			map.put(key, valueList);
		}

		return map;
	}

}
