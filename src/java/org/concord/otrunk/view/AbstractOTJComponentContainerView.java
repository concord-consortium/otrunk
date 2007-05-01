/**
 * 
 */
package org.concord.otrunk.view;

import org.concord.framework.otrunk.view.AbstractOTJComponentView;

/**
 * @author scott
 *
 */
public abstract class AbstractOTJComponentContainerView extends AbstractOTJComponentView
{
	private OTJComponentContainerHelper containerHelper;

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTJComponentView#viewClosed()
	 */
	public void viewClosed()
	{
		removeAllSubViews();
	}

	protected OTViewContainerPanel createtViewContainerPanel()
    {
	    return getContainerHelper().createtViewContainerPanel();
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
					getJComponentService(), null);

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
