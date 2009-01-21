package org.concord.otrunk.test;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.framework.otrunk.view.OTJComponentView;

public class OTMapTestView implements OTJComponentView {

	HashMap<String, Object> map;
	
	public OTMapTestView()
	{
		map = new HashMap<String, Object>();
		map.put("myString", "hello world");
		map.put("myInteger", new Integer("10"));
		map.put("myFloat", new Float("33.1"));
		String testBlobStr = "hello world,";
		map.put("myBlob", testBlobStr.getBytes());
		testBlobStr = "This is longer so we can see how line breaks " +
			"work.  Because it is so compress it will probably take " +
			"a lot to make two lines.";
		map.put("myLongBlob", testBlobStr.getBytes());		
	}
	
	public JComponent getComponent(OTObject otObject) {
		OTResourceMap otMap = ((OTMapTestObject)otObject).getResourceMap();
		if(otMap.size() == 0){
			Set<Entry<String, Object>> entries = map.entrySet();
			for (Entry<String, Object> entry : entries) {
				otMap.put((String)entry.getKey(), entry.getValue());
			}
			
			return new JLabel("Map Initialized");			
		}

		if(otMap.size() != map.size()) {
			return new JLabel("Map Size doesn't match");						
		}
		
		Set<Entry<String, Object>> entries = map.entrySet();
		for (Entry<String, Object> entry : entries) {
			Object value = otMap.get((String)entry.getKey());
			if(value instanceof byte[]) {
				if(!checkBytes(value, entry.getValue())){
					return new JLabel("Map entries (byte []) doen't match");									
				}
			} else if(!value.equals(entry.getValue())){
				return new JLabel("Map entries doen't match");				
			}
		}
				
		return new JLabel("Map passes test");
	}

	public static boolean checkBytes(Object bytes1, Object bytes2)
	{
		byte [] otBytes = (byte[])bytes2;
		byte [] bytes = (byte[])bytes2;
		if(otBytes.length != bytes.length) {
			return false;
		}
		for(int i=0; i<otBytes.length; i++){
			if(otBytes[i] != bytes[i]){
				return false;
			}
		}

		return true;
	}
	
	public void viewClosed() {
		// TODO Auto-generated method stub

	}

}
