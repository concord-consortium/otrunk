/**
 * 
 */
package org.concord.otrunk.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;
import org.concord.framework.otrunk.view.OTJComponentViewContext;
import org.concord.framework.otrunk.view.OTJComponentViewContextAware;
import org.concord.framework.otrunk.view.OTView;

/**
 * @author scott
 *
 */
public class OTViewContextButtonTestView extends AbstractOTJComponentView
	implements OTJComponentViewContextAware
{

	private OTJComponentViewContext viewContext;
	OTObject target;
	
	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTJComponentView#getComponent(org.concord.framework.otrunk.OTObject, boolean)
	 */
	public JComponent getComponent(OTObject otObject)
	{
		OTViewContextButtonTest testObject = (OTViewContextButtonTest) otObject;
		
		target = testObject.getTarget();
		
		JButton button = new JButton("test view context");
		
		button.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0)
            {
				OTView targetView = viewContext.getViewByObject(target);
				System.err.println("targetView: " + targetView);
				
				JComponent targetComponent = viewContext.getComponentByObject(target);
				System.err.println("targetComponent: " + targetComponent);
            }
			
		});
		return button;
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTJComponentView#viewClosed()
	 */
	public void viewClosed()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.view.OTJComponentViewContextAware#setOTJComponentViewContext(org.concord.framework.otrunk.view.OTJComponentViewContext)
     */
    public void setOTJComponentViewContext(OTJComponentViewContext viewContext)
    {
    	this.viewContext = viewContext;	    
    }

}
