package org.concord.otrunk.test;

import java.util.Random;

import junit.framework.TestCase;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTrunk;
import org.concord.otrunk.view.OTFolderObject;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

public class OTObjectInternalTest extends TestCase
{
	OTFolderObject folder;
	OTFolderObject item;
	OTChangeListener listener;
	Thread changeObjectThread;
	Thread changeListenersThread;
	boolean noExceptionThrown;
	
	// run test for 500 ms
    final long endTime = System.currentTimeMillis() + 1000;
	
    /**
     * If OTObjectInternal believes it has listeners when an otObject is
     * being changed, and another removes the last listener during the
     * change event, OTObjectInternal should handle it gracefully.
     */
	public void testListenersNotNull(){
		OTViewerHelper viewerHelper = new OTViewerHelper();

		// create  an empty database
		XMLDatabase db = new XMLDatabase();
		
		noExceptionThrown = true;
		
		try {
	        viewerHelper.loadOTrunk2(null, db);
	        
	        OTrunk otrunk = viewerHelper.getOtrunk();
	        folder = otrunk.createObject(OTFolderObject.class);
	        item = otrunk.createObject(OTFolderObject.class);
	        
	        otrunk.setRoot(folder);	
	        
	        changeObjectThread = new Thread(){
	        	public void run(){
	        		while(System.currentTimeMillis() < endTime){
	        			try {
		        			if (folder.getChildCount() == 0){
		        				folder.addChild(item);
		        			} else {
		        				folder.removeAllChildren();
		        			}
	        			} catch (java.lang.NullPointerException e){
	        				noExceptionThrown = false;
	        			}
	        		}
	        	}
	        };
	        
	        changeListenersThread = new Thread(){
	        	public void run(){
	        		listener = new OTChangeListener() {
	    				
	    				public void stateChanged(OTChangeEvent e){}
	    			};
	    			
	        		boolean added = false;
	        		
	        		while(System.currentTimeMillis() < endTime){
	        			if (!added){
	        				folder.addOTChangeListener(listener);
	        				added = true;
	        			} else {
	        				folder.removeOTChangeListener(listener);
	        				added = false;
	        			}
	        		}
	        	}
	        };
	        
	        changeObjectThread.start();
	        changeListenersThread.start();
	        
	        

	        // FIXME we should automatically check if the id is actually saved in the string.
			
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
        
//        System.out.println(noExceptionThrown);
//        assertTrue(noExceptionThrown);
        while (System.currentTimeMillis() < (endTime)){
        	assertTrue(noExceptionThrown);
        }
	}

}
