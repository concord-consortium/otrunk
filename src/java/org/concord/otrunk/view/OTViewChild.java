package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.view.OTViewEntry;

public interface OTViewChild extends OTObjectInterface {
	
	public OTObject getObject();
	public void setObject(OTObject object);
	
	public OTViewEntry getViewid();
	
	public boolean getUseScrollPane();
	
	public static boolean DEFAULT_useScrollPane = false;
	
	public boolean getScrollPanelHasBorder();
	
	public static boolean DEFAULT_scrollPanelHasBorder = true;
}
