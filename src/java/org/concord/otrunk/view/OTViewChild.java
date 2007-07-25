package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;

public interface OTViewChild extends OTObjectInterface {
	
	public OTObject getObject();
	
	public OTObject getViewid();
	
	public boolean getUseScrollPane();
	
	public static boolean DEFAULT_useScrollPane = false;
}
