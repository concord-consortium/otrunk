package org.concord.otrunk.overlay;

import javax.swing.JComponent;

import org.concord.framework.otrunk.OTObject;
import org.concord.otrunk.view.AbstractOTJComponentContainerView;

public class OTOverlaySwitcherStudentView extends AbstractOTJComponentContainerView
{
	OTOverlaySwitcher otSwitcher;
	
	public JComponent getComponent(OTObject otObject)
	{
		// This view ignores the overlay switcher stuff entirely
		otSwitcher = (OTOverlaySwitcher) otObject;
		OTObject activityRoot = otSwitcher.getActivityRoot();
		
		return createSubViewComponent(activityRoot);
	}

}
