package org.concord.otrunk.util;

import org.concord.framework.otrunk.OTObject;

public class OTSharingEvent
{
	public static final int SHARED = 0;
	public static final int REMOVED = 1;
	
	private int type;
	private OTObject object;
	
	public OTSharingEvent(int type, OTObject object) {
		this.type = type;
		this.object = object;
	}
	
	public int getType() {
		return this.type;
	}
	
	public OTObject getObject() {
		return this.object;
	}
}
