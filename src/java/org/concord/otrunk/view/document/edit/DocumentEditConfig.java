package org.concord.otrunk.view.document.edit;

import org.concord.framework.otrunk.OTObjectList;
import org.concord.otrunk.view.document.DocumentConfig;
import org.concord.otrunk.view.document.OTDocumentViewConfig;

public class DocumentEditConfig
    implements DocumentConfig
{
	private final OTDocumentEditViewConfig documentEditViewConfig;
	private final OTDocumentViewConfig documentViewConfig;
	
	public DocumentEditConfig(OTDocumentEditViewConfig documentEditViewConfig){
		this.documentEditViewConfig = documentEditViewConfig;
		this.documentViewConfig = documentEditViewConfig.getDocumentViewConfig();
	}
	
	public OTObjectList getObjectsToInsert(){
		return documentEditViewConfig.getObjectsToInsert();
	}

	public String getCss()
	{
		return documentViewConfig.getCss();
	}

	public OTObjectList getCssBlocks()
	{
		return documentViewConfig.getCssBlocks();
	}

	public String getMode()
	{
		return documentViewConfig.getMode();
	}

	public boolean getViewContainerIsUpdateable()
	{
		return documentViewConfig.getViewContainerIsUpdateable();
	}

	public void setCss(String css)
	{
		documentViewConfig.setCss(css);
	}

	public void setMode(String mode)
	{
		documentViewConfig.setMode(mode);
	}

	public void setViewContainerIsUpdateable(boolean viewContainerIsUpdateable)
	{
		documentViewConfig.setViewContainerIsUpdateable(viewContainerIsUpdateable);
	}

}
