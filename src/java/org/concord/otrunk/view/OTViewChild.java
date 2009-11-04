package org.concord.otrunk.view;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.view.OTViewEntry;

public interface OTViewChild extends OTObjectInterface {
	
	public OTObject getObject();
	public void setObject(OTObject object);
	
	public OTViewEntry getViewid();
	public void setViewid(OTViewEntry viewid);
	
	public static boolean DEFAULT_useScrollPane = false;	
	public boolean getUseScrollPane();
	public void setUseScrollPane(boolean useScrollPane);
	
	public static boolean DEFAULT_useHorizontalScrollPane = true;
	/**
	 * This property will only be checked if useScrollPane is true. 
	 * @return
	 */
	public boolean getUseHorizontalScrollPane();
	public void setUseHorizontalScrollPane(boolean useHorizontal);
	
	public static boolean DEFAULT_scrollPanelHasBorder = true;
	public boolean getScrollPanelHasBorder();
	public void setScrollPanelHasBorder(boolean hasBorder);
	
}
