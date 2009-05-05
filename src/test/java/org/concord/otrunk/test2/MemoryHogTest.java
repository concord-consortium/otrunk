package org.concord.otrunk.test2;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.io.File;
import java.net.URL;

import javax.swing.JFrame;

import junit.framework.TestCase;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.otrunk.OTMLToXHTMLConverter;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.view.OTViewContainerPanel;
import org.concord.otrunk.view.OTViewerHelper;

public class MemoryHogTest extends TestCase
{
	public static Throwable eventQueueThrowable;
	OTViewContainerPanel rootView;
	
	public void testReload() throws Exception
	{		
		final URL resource = getClass().getResource("/memory-hog-test.otml");

		final JFrame frame = new JFrame("memory hog test");
		frame.setSize(800, 800);
		frame.getContentPane().setLayout(new FlowLayout());
		final Object lock = new Object();
		System.out.println("registering event throwable handler: " + EventQueueExceptionHandler.class.getCanonicalName());
		System.setProperty("sun.awt.exception.handler", EventQueueExceptionHandler.class.getCanonicalName());

		// This code assume we are not running in the event thread, so just to be 
		// safe it is put in its own thread.
		Thread thread = new Thread(){
			public void run(){
				for(int i=0; i<20; i++){
					eventQueueThrowable = null;
					
					EventQueue.invokeLater(new Runnable(){

						public void run()
		                {
							try {
			                    rootView = createOTRootView(resource);
			                    frame.getContentPane().add(rootView);
			                    frame.setVisible(true);
		                    } catch (Exception e) {
			                    e.printStackTrace();
		                    	assertTrue("Failed with exception", false);
		                    }	                
		                }				
					});
					
					
					// need to wait until the frame has finished rendering....
					invokeLater(100, new Runnable(){
						public void run()
		                {
							synchronized(lock){	
								lock.notifyAll();
							}
		                }						
					});
					
					synchronized(lock){
						try {
							lock.wait(10000);	                    
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					// check if we have a throwable
					if(eventQueueThrowable != null){
						// we do so break out of the loop and let the outter code take care of it
						break;
					}
					
					EventQueue.invokeLater(new Runnable(){

						public void run()
		                {
							frame.getContentPane().removeAll();
							frame.setVisible(false);
		                }
					});
					
					try {
	                    Thread.sleep(100);
                    } catch (InterruptedException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
                    }
				}				
			}
		};
		thread.start();

		// poll until the thread dies
		while(thread.isAlive()){
			Thread.sleep(100);
		}
		
		frame.dispose();
		
		// check if we have a throwable
		if(eventQueueThrowable != null){
			assertTrue("Event queue threw an throwable: " + eventQueueThrowable, false);
		}				
	}

	public OTViewContainerPanel createOTRootView(URL otmlURL) throws Exception
	{
		OTViewerHelper viewerHelper = new OTViewerHelper();
		
		OTDatabase mainDb = viewerHelper.loadOTDatabase(otmlURL);

		viewerHelper.loadOTrunk(mainDb, null);

		OTViewContainerPanel otContainer = viewerHelper.createViewContainerPanel(); 
		otContainer.setCurrentObject(viewerHelper.getRootObject());		
		
		return otContainer;
	}
	
	public void testXHTMLExport() throws Exception
	{
		URL resource = getClass().getResource("/memory-hog-test.otml");
		
		for(int i=0; i<10; i++){
			OTViewerHelper viewerHelper = new OTViewerHelper();

			OTDatabase mainDb = viewerHelper.loadOTDatabase(resource);

			viewerHelper.loadOTrunk(mainDb, null);

			OTObject rootObject = viewerHelper.getRootObject();
			OTViewFactory viewFactory = viewerHelper.getViewFactory();

			OTMLToXHTMLConverter toXHTMLConverter = new OTMLToXHTMLConverter(viewFactory, rootObject);

			// FIXME this will create a bunch of files that won't be cleaned up after the 
			// test is run
			File tempFile = File.createTempFile("ot_export", ".html");
			toXHTMLConverter.setXHTMLParams(tempFile, 800, 600);

			Thread thread = new Thread(toXHTMLConverter);
			thread.start();

			while(thread.isAlive()){
				Thread.sleep(100);
			}		
		}
	}
	
	private void invokeLater(final int depth, final Runnable muchLaterRunnable) {
		EventQueue.invokeLater(new Runnable() {
            public void run()
            {
            	if (depth != 0) {
            		invokeLater(depth-1, muchLaterRunnable);
            	} else {
            		muchLaterRunnable.run();
            	}
            }
        });
	}
}
