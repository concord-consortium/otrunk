package org.concord.otrunk;

import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTrunk;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.view.AbstractOTJComponentContainerView;

public class OTIncludeRootObjectView extends AbstractOTJComponentContainerView
{
	public JComponent getComponent(OTObject otObject) 
	{
		OTIncludeRootObject ref = (OTIncludeRootObject) otObject;
		URL url = ref.getHref();
		
		OTrunk otrunk = (OTrunk) getViewService(OTrunk.class);
		OTrunkImpl otrunkImpl = (OTrunkImpl) otrunk;
		
		try {
			OTObject refObject = otrunkImpl.getExternalObject(url, ref.getOTObjectService());
			JComponent refComponent = createSubViewComponent(refObject);

			return refComponent;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new JLabel("Error loading external object: " + url);
	}

}
