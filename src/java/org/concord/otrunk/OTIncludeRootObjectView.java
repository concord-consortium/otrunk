package org.concord.otrunk;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.concord.framework.otrunk.OTObject;
import org.concord.otrunk.view.AbstractOTJComponentContainerView;

public class OTIncludeRootObjectView extends AbstractOTJComponentContainerView
{
	
	public JComponent getComponent(OTObject otObject) 
	{
		OTIncludeRootObject ref = (OTIncludeRootObject) otObject;
		
		try {
			OTObject refObject = ref.getReference();
			JComponent refComponent = createSubViewComponent(refObject);

			return refComponent;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new JLabel("Error loading external object: " + ref.getHref());
	}

}
