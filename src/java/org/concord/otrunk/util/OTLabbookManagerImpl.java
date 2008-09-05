package org.concord.otrunk.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTLabbookManager;
import org.concord.otrunk.datamodel.OTDataCollection;
import org.concord.otrunk.util.OTLabbookBundle;

public class OTLabbookManagerImpl
    implements OTLabbookManager, OTChangeListener
{
	private final OTLabbookBundle.ResourceSchema resources;
	private Vector listeners;

	public OTLabbookManagerImpl(OTLabbookBundle.ResourceSchema resources)
    {
	    this.resources = resources;
	    resources.addOTChangeListener(this);
    }
	
	public void add(OTObject otObject)
	{
		OTLabbookEntry entry = createEntry(otObject);
		resources.getEntries().add(entry);
		System.out.println("resources.getEntries().get(0) = "+resources.getEntries().get(0));
	}

	public void addSnapshot(OTObject snapshot)
	{
		
		OTLabbookEntry entry = createEntry(snapshot);
		resources.getEntries().add(entry);
	}

	public void addDataCollector(OTObject dataCollector)
	{
		OTLabbookEntry entry = createEntry(dataCollector);
		resources.getEntries().add(entry);
	}

	public void addDrawingTool(OTObject drawingTool)
	{
		OTLabbookEntry entry = createEntry(drawingTool);
		resources.getEntries().add(entry);
	}

	public void addText(OTObject question)
	{
		OTLabbookEntry entry = createEntry(question);
		resources.getEntries().add(entry);
	}

	public Vector getGraphs()
	{
		return resources.getEntries().getVector();
	}

	public Vector getDrawings()
	{
		return resources.getEntries().getVector();
	}

	public Vector getText()
	{
		return resources.getEntries().getVector();
	}

	public Vector getSnapshots()
	{
		return resources.getEntries().getVector();
	}
	
	public Vector getAllEntries(){
		return resources.getEntries().getVector();
	}
	
	public void remove(OTObject labbookEntry){
		resources.getEntries().remove(labbookEntry);
	}
	
	private OTLabbookEntry createEntry(OTObject object){
		String type = null;
		if (object.toString().indexOf("OTDataCollector") > -1){
			type = "Graphs";
		} else if (object.toString().indexOf("OTDrawing") > -1){
			type = "Drawings";
		} else if (object.toString().indexOf("OTText") > -1 ||
				object.toString().indexOf("Question") > -1){
			type = "Text";
		}
		return createEntry(object, type);
	} 
	
	private OTLabbookEntry createEntry(OTObject object, String type){
		try {
	        OTLabbookEntry entry = (OTLabbookEntry) resources.getOTObjectService().createObject(OTLabbookEntry.class);
	        entry.setOTObject(object);
	        
	        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d 'at' K:mm");
			Date now = new Date();
			String timeString = dateFormat.format(now);
			entry.setTimeStamp(timeString);
			if (type != null){
				entry.setType(type);
			}
	        return entry;
		} catch (Exception e) {
	        e.printStackTrace();
	        return null;
        }
	}

	public boolean isEmpty()
    {
	    return (resources.getEntries().getVector().isEmpty());
    }
	
	/**
	 * Change events on the bundle will get passed straight through to listeners
	 */
	public void stateChanged(OTChangeEvent e)
    {
	    notifyListeners(e);
    }
	
	public void addLabbookListener(OTChangeListener listener){
		if (listeners == null){
			listeners = new Vector();
		}
		if (!listeners.contains(listener)){
			listeners.add(listener);
		}
	}
	
	public void removeLabbookListener(OTChangeListener listener){
		if (listeners != null){
			listeners.remove(listener);
		}
	}
	
	private void notifyListeners(OTChangeEvent e){
		if (listeners == null){
			return;
		}
		for (int i = 0; i < listeners.size(); i++) {
	       ((OTChangeListener)listeners.get(i)).stateChanged(e); 
        }
	}

}
