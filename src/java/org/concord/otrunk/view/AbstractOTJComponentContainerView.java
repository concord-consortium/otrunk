/**
 * 
 */
package org.concord.otrunk.view;

import javax.swing.JComponent;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;
import org.concord.framework.otrunk.view.OTViewEntry;

/**
 * @author scott
 *
 */
public abstract class AbstractOTJComponentContainerView extends AbstractOTJComponentView
{
	private OTJComponentContainerHelper containerHelper;
	protected String myMode;

	/**
	 * @see org.concord.framework.otrunk.view.OTJComponentView#viewClosed()
	 */
	public void viewClosed()
	{
		removeAllSubViews();
	}
	
	public void setMode(String mode){
		myMode = mode;
	}
	
	public String getMode(){
		return myMode;
	}

	protected OTViewContainerPanel createViewContainerPanel()
    {
	    return getContainerHelper().createViewContainerPanel();
    }

	protected JComponent createSubViewComponent(OTObject otObject)
	{
		return createSubViewComponent(otObject, false);
	}
	
	protected JComponent createSubViewComponent(OTObject otObject, boolean useScrollPane)
	{
		return createSubViewComponent(otObject, useScrollPane, null);
	}
	
	protected JComponent createSubViewComponent(OTObject otObject, boolean useScrollPane, 
		OTViewEntry viewEntry)
	{
		OTViewContainerPanel otObjectPanel = createViewContainerPanel();
		otObjectPanel.setUseScrollPane(useScrollPane);
		
		// The OTViewContainerPanel automatically handles the OTViewChild object
		otObjectPanel.setCurrentObject(otObject, viewEntry);
		
		// set parent of new viewcontainer to this viewcontainer
		otObjectPanel.setParentContainer(viewContainer);
		return otObjectPanel;
	}
	
	protected void removeAllSubViews()
    {
		if(containerHelper != null){
			containerHelper.removeAllSubViews();
		}
    }

	protected OTJComponentContainerHelper getContainerHelper()
	{
		if(containerHelper == null){
			containerHelper = new OTJComponentContainerHelper(getFrameManager(),
					getJComponentService(), getMode());

		}
		return containerHelper;
	}

	protected void setSubViewAutoRequestFocus(boolean autoRequestFocus)
    {
		getContainerHelper().setAutoRequestFocus(autoRequestFocus);
    }

	protected void setSubViewUseScrollPane(boolean useScrollPane)
    {
		getContainerHelper().setUseScrollPane(useScrollPane);
    }

	protected void setSubViewMode(String viewMode)
    {
		getContainerHelper().setViewMode(viewMode);
    }
}
