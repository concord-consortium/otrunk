/**
 * 
 */
package org.concord.otrunk.view;

import javax.swing.JComponent;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewFactory;

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
		super.viewClosed();
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

	public JComponent createSubViewComponent(OTObject otObject)
	{
		return createSubViewComponent(otObject, false);
	}
	
	public JComponent createSubViewComponent(OTObject otObject, boolean useScrollPane)
	{
		return createSubViewComponent(otObject, useScrollPane, null);
	}
	
	public JComponent createSubViewComponent(OTObject otObject, boolean useScrollPane, 
		OTViewEntry viewEntry)
	{
		return createSubViewComponent(otObject, useScrollPane, viewEntry, false, null);
	}
	
	/**
	 * Creates a sub view, and returns the view container JComponent
	 * 
	 * If isTopLevelContainer is true and a ViewFactory is passed in, the new view container
	 * will be top level, and so will contain a shared service for all its descendant views.
	 */
	public JComponent createSubViewComponent(OTObject otObject, boolean useScrollPane, 
			OTViewEntry viewEntry, boolean isTopLevelContainer, OTViewFactory otViewFactory)
		{
			OTViewContainerPanel otObjectPanel = createViewContainerPanel();
			otObjectPanel.setUseScrollPane(useScrollPane);
			otObjectPanel.setTopLevelContainer(isTopLevelContainer);
			if (otViewFactory != null)
				otObjectPanel.setOTViewFactory(otViewFactory);
			
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
