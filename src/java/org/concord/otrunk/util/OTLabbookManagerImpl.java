package org.concord.otrunk.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTLabbookManager;
import org.concord.otrunk.util.OTLabbookBundle.ResourceSchema.ImageFiletype;

public class OTLabbookManagerImpl
    implements OTLabbookManager, OTChangeListener
{
	private OTLabbookBundle.ResourceSchema resources;
	private ArrayList<OTChangeListener> listeners;
	private boolean tempShowLabbook;
	
	private static final String NEAR_LIMIT_MESSAGE = "Be careful, you have now taken _CURRENT_ENTRIES_ snapshots of this _TYPE_.\nYou can only take one more snapshot of this!";
	private static final String AT_LIMIT_MESSAGE = "You have now taken _CURRENT_ENTRIES_ snapshots of this _TYPE_. That's the limit!\nIf you want to take more snapshots of this _TYPE_, you will need to delete some in your lab book.";
	private static final String OVER_LIMIT_MESSAGE = "Oops, you already took _MAX_ENTRIES_ snapshots of this _TYPE_. That was the limit!\nIf you want to take more snapshots of this _TYPE_, you will need to delete some in your lab book.";

	
	
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
		if (resources.getLimitEntries() && (totalNumberOfEntriesForObject(originalObject) >= resources.getLimit())){
			showOverLimitMessage(originalObject);
			return;
		}
		
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
		remove(labbookEntry, true);
	}
	
	public void remove(OTObject labbookEntry, boolean showLabbook){
		tempShowLabbook = showLabbook;
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
	        OTLabbookEntry entry = resources.getOTObjectService().createObject(OTLabbookEntry.class);
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
	
	public void setAllowViewUpdates(boolean allowViewUpdates){
		
	}
	
	private int totalNumberOfEntriesForObject(OTObject object){
		int totalNumberOfEntriesForObject = 0;
		for (OTObject entry : resources.getEntries()) {
	        if (((OTLabbookEntry)entry).getOriginalObject().equals(object))
	        	totalNumberOfEntriesForObject++;
        }
		return totalNumberOfEntriesForObject;
	}
	
	private void showOverLimitMessage(OTObject originalObject){
		OTLabbookChangeEvent e = new OTLabbookChangeEvent(originalObject);
		String message = OVER_LIMIT_MESSAGE.replaceAll("_TYPE_", getType(originalObject));
		message = message.replaceAll("_MAX_ENTRIES_", ""+resources.getLimit());
		e.setMessage(message);
		notifyListeners(e);
	}
	
	private String getType(OTObject otObj){
		String className = otObj.otClass().getName();
		String type = "model";
		if (className.indexOf("Drawing") > -1)
			type = "drawing";
		else if (className.indexOf("Collector") > -1 || className.indexOf("Graph") > -1)
			type = "graph";
		return type;
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
		
		if (resources.getLimitEntries() && e.getOperation() == OTChangeEvent.OP_ADD){
			int total = totalNumberOfEntriesForObject(((OTLabbookEntry)e.getValue()).getOriginalObject());
			String message = null;
			if (total == resources.getLimit()){
				message = AT_LIMIT_MESSAGE;
			} else if (total == resources.getLimit() - 1){
				message = NEAR_LIMIT_MESSAGE;
			}
			
			if (message != null){
				message = message.replaceAll("_TYPE_", getType((OTObject) e.getValue()));
				message = message.replaceAll("_CURRENT_ENTRIES_", ""+total);
				labbookChangeEvent.setMessage(message);
			}
		}
		
		notifyListeners(labbookChangeEvent);
	}
	
	private void notifyListeners(OTLabbookChangeEvent e){
		for (int i = 0; i < listeners.size(); i++) {
	       (listeners.get(i)).stateChanged(e); 
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

        private static final long serialVersionUID = 1L;
        
		private boolean showLabbook = true;
		private String message;

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
		
		public void setMessage(String message){
			this.message = message;
		}
		
		public String getMessage(){
			return message;
		}
	}

	public boolean getEmbedInDrawTool()
    {
	    return resources.getEmbedInDrawTool();
    }
	
	public ImageFiletype getSnapshotFiletype(){
		return resources.getSnapshotFiletype();
	}

	public boolean getScaleDrawTools(){
	    return this.resources.getScaleDrawTools();
    }
}
