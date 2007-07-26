package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.view.OTViewEntry;

public interface OTViewChild extends OTObjectInterface {
	
	public OTObject getObject();
	
	public OTViewEntry getViewid();
	
	public boolean getUseScrollPane();
	
	public static boolean DEFAULT_useScrollPane = false;
}
