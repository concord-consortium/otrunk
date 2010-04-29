package org.concord.otrunk.view;

public class OTLifecycleEvent
{
	// TODO Add more lifecycle events
	public static int INITIALIZATION_COMPLETE = 0;
	
	private long timestamp;
	private int type;
	private OTLifecycleNotifying source;
	
	public OTLifecycleEvent(int eventType, OTLifecycleNotifying eventSource) {
		this.source = eventSource;
		this.type = eventType;
		this.timestamp = System.currentTimeMillis();
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
	
	public int getType() {
		return this.type;
	}
	
	public OTLifecycleNotifying getSource() {
		return this.source;
	}

}
