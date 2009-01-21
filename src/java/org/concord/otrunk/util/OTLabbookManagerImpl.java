package org.concord.otrunk.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTLabbookManager;

public class OTLabbookManagerImpl
    implements OTLabbookManager, OTChangeListener
{
	private OTLabbookBundle.ResourceSchema resources;
	private ArrayList<OTChangeListener> listeners;
	private boolean tempShowLabbook;

	public OTLabbookManagerImpl(OTLabbookBundle.ResourceSchema resources)
    {
	    this.resources = resources;
	    resources.addOTChangeListener(this);
    }
	
	public void add(OTObject otObject)
	{
		this.add(otObject, null);
	}
	
	public void add(OTObject otObject, OTObject originalObject)
	{
		this.add(otObject, originalObject, null);
	}
	
	public void add(OTObject otObject, OTObject originalObject, OTObject container)
	{
		this.add(otObject, container, originalObject, true);
	}
	
	public void add(OTObject otObject, OTObject container, OTObject originalObject, boolean showLabbook)
	{
		OTLabbookEntry entry = createEntry(otObject);
		if (container != null){
			entry.setContainer(container);
		}
		if (originalObject != null){
			entry.setOriginalObject(originalObject);
		}
		tempShowLabbook = showLabbook;
		resources.getEntries().add(entry);
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

	public Vector<OTObject> getGraphs()
	{
		return resources.getEntries().getVector();
	}

	public Vector<OTObject> getDrawings()
	{
		return resources.getEntries().getVector();
	}

	public Vector<OTObject> getText()
	{
		return resources.getEntries().getVector();
	}

	public Vector<OTObject> getSnapshots()
	{
		return resources.getEntries().getVector();
	}
	
	public Vector<OTObject> getAllEntries(){
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
		}	else if (object.toString().indexOf("OTBlob") > -1){
				type = "Snapshots";
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
			listeners = new ArrayList<OTChangeListener>();
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
		OTLabbookChangeEvent labbookChangeEvent = new OTLabbookChangeEvent((OTObject) e.getSource());
		labbookChangeEvent.setOperation(e.getOperation());
		labbookChangeEvent.setProperty(e.getProperty());
		labbookChangeEvent.setValue(e.getValue());
		labbookChangeEvent.setShowLabbook(tempShowLabbook);
		
		for (int i = 0; i < listeners.size(); i++) {
	       ((OTChangeListener)listeners.get(i)).stateChanged(labbookChangeEvent); 
        }
	}

	/*
	public void setOTObjectService(OTObjectService objectService)
    {
	    this.objectService = objectService;
	    try {
	    	OTLabbookBundle bundle = (OTLabbookBundle) objectService.getOTObject(resources.getGlobalId());
	        this.resources = bundle.resources;
	        resources.addOTChangeListener(this);
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }
    */
	
	public class OTLabbookChangeEvent extends OTChangeEvent
	{

		private boolean showLabbook = true;

		public OTLabbookChangeEvent(OTObject source)
        {
	        super(source);
        }
		
		public void setShowLabbook(boolean showLabbook){
			this.showLabbook = showLabbook;
		}
		
		/*
		 * Any listener that will pop up a labbook view should check this first
		 */
		public boolean getShowLabbook(){
			return showLabbook;
		}
	}
}
