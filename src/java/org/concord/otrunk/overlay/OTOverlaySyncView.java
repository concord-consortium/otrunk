package org.concord.otrunk.overlay;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTChangeNotifying;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;
import org.concord.framework.otrunk.view.OTJComponentViewContext;
import org.concord.framework.otrunk.view.OTJComponentViewContextAware;

public class OTOverlaySyncView extends AbstractOTJComponentView
	implements OTJComponentViewContextAware
{

	private OTJComponentViewContext jViewContext;
	private HashMap<String, ArrayList<OTObject>> externalIdToObject = 
		new HashMap<String, ArrayList<OTObject>>();
	
	private OTChangeListener changeListener = new OTChangeListener(){
		public void stateChanged(OTChangeEvent e)
        {
	        OTObject source = (OTObject) e.getSource();
	        String otExternalId = source.otExternalId();
	        ArrayList<OTObject> relatedList = externalIdToObject.get(otExternalId);
	        for (OTObject related : relatedList) {
	        	if(related.equals(source)){
	        		continue;
	        	}
	        	
	        	if(related instanceof OTObjectInterface){
	        		((OTObjectInterface) related).removeOTChangeListener(this);
	        		((OTObjectInterface) related).notifyOTChange(e.getProperty(), e.getOperation(), 
	        			e.getValue(), e.getPreviousValue());
	        		((OTObjectInterface) related).addOTChangeListener(this);
	        	} else {
	        		// FIXME This is currently unsupported there should be some otMethod for doing this 
	        		// or possibly a dummy property that be passed to otSet can cause an update.
	        		// Or this whole need of sending events like this should be removed because the
	        		// underlying dataobjects should send these events automatically
	        	}
	        }
        }
		
	};
	
	public JComponent getComponent(OTObject otObject)
	{
		// do this in an invokeLater so we make sure the rest of the views are loaded
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				Object[] allObjects = jViewContext.getAllObjects();
				
				for(int i=0; i<allObjects.length; i++){
					OTObject sibling = (OTObject) allObjects[i];
					String idStr = sibling.otExternalId();
					ArrayList<OTObject> relatedObjects = externalIdToObject.get(idStr);
					if(relatedObjects == null){
						relatedObjects = new ArrayList<OTObject>();
						externalIdToObject.put(idStr, relatedObjects);
					}

					relatedObjects.add(sibling);
					if(sibling instanceof OTChangeNotifying){
						((OTChangeNotifying) sibling).addOTChangeListener(changeListener);
					}
				}			    
			}
		});
				
        JLabel label = new JLabel("...");
        label.setVisible(false);
        return label;
	}

	public void setOTJComponentViewContext(OTJComponentViewContext viewContext)
    {
		jViewContext = viewContext;	    
    }

}
