/**
 * 
 */
package org.concord.otrunk.view;

import java.awt.BorderLayout;
import java.util.Hashtable;

import javax.swing.JFrame;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.framework.otrunk.view.OTFrameManager;
import org.concord.framework.otrunk.view.OTViewFactory;

public class OTFrameManagerImpl implements OTFrameManager 
{
	Hashtable frameContainers = new Hashtable();
	OTViewFactory viewFactory;
	
	public class FrameContainer
	{
		OTViewContainerPanel container;
		JFrame frame;
	}
	
	public void setViewFactory(OTViewFactory viewFactory)
	{
		this.viewFactory = viewFactory;
	}
	
	public void putObjectInFrame(OTObject otObject, 
			OTFrame otFrame) 
	{
		putObjectInFrame(otObject, null, otFrame);
	}
	
	public void putObjectInFrame(OTObject otObject, 
			org.concord.framework.otrunk.view.OTViewEntry viewEntry,
			OTFrame otFrame) {
		// look up view container with the frame.
		FrameContainer frameContainer = 
			(FrameContainer)frameContainers.get(otFrame.getGlobalId());
		
		if(frameContainer == null) {
			JFrame jFrame = new JFrame(otFrame.getTitle());
	
			frameContainer = new FrameContainer();
			frameContainer.frame = jFrame;
			OTViewContainerPanel otContainer = new OTViewContainerPanel(this);
			frameContainer.container = otContainer;

			frameContainer.container.setOTViewFactory(viewFactory);

			jFrame.getContentPane().setLayout(new BorderLayout());
	
			jFrame.getContentPane().add(otContainer, BorderLayout.CENTER);
			jFrame.setSize(otFrame.getWidth(), otFrame.getHeight());
			
			frameContainers.put(otFrame.getGlobalId(), frameContainer);
		}
		
		// call setCurrentObject on that view container with a null
		// frame
		frameContainer.container.setCurrentObject(otObject, viewEntry, true);
		frameContainer.frame.setVisible(true);
	}
}