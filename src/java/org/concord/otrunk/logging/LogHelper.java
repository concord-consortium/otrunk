package org.concord.otrunk.logging;

import java.util.HashMap;
import java.util.Map.Entry;

import org.concord.framework.otrunk.OTObjectList;
import org.concord.otrunk.logging.OTModelEvent.EventType;

public class LogHelper {
	
	public static void add(OTModelLogging model, EventType type) {
		add(model, type, null);
	}
	
	public static void add(OTModelLogging model, EventType type, HashMap<String, String> details) {
		try {
			OTModelEvent item = model.getOTObjectService().createObject(OTModelEvent.class);
			item.setType(type);
			item.setTimestamp(System.currentTimeMillis());
			
			if (details != null) {
    			for (Entry<String, String> e : details.entrySet()) {
    			    item.getDetails().put(e.getKey(), e.getValue());
    			}
			}
			model.getLog().add(item);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int getNumCollections(OTModelLogging model) {
		OTObjectList items = model.getLog();
		int cnt = 0;
		for (int i = 0; i < items.size(); ++i) {
		    OTModelEvent item = (OTModelEvent) items.get(i);
			if (item.getType().equals(EventType.START)) {
				++cnt;
			}
		}
		return cnt;
	}
	
	public static long getTotalCollectionTime(OTModelLogging model) {
		OTObjectList items = model.getLog();
		long sum = 0;
		long start = 0;
		for (int i = 0; i < items.size(); ++i) {
		    OTModelEvent item = (OTModelEvent) items.get(i);
			EventType name = item.getType();
			if (name.equals(EventType.START)) {
				start = item.getTimestamp();
			}
			else if (name.equals(EventType.STOP)) {
				sum += item.getTimestamp() - start;
			}
		}
		return sum;
	}
}
