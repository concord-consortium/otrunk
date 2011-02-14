package org.concord.otrunk.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadedProcessor<T> {
	private static final Logger logger = Logger.getLogger(MultiThreadedProcessor.class.getName());
	private ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<T>();
	private int numberOfThreads;
	private MultiThreadedProcessorRunnable<T> runnable;
	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	
	public MultiThreadedProcessor(Collection<T> collection, int threads, MultiThreadedProcessorRunnable<T> runnable) {
		queue.addAll(collection);
		this.numberOfThreads = threads;
		this.runnable = runnable;
	}
	
	public void process() throws MultiThreadedProcessingException {
		Runnable processingTask = new Runnable(){
			public void run()
            {	
				while(true){
					T item = queue.poll();
					if (item == null) {
						break;
					}
					// logger.log(Level.INFO, "Processing item: " + item + ", Thread: " + Thread.currentThread().getName());
					runnable.process(item);
				}	            
            }
	    };

	    Future<?>[] futures = new Future<?>[numberOfThreads];
	    for (int i=0; i < numberOfThreads; i++) {
	    	futures[i] = threadPool.submit(processingTask);
	    }
	    
	    ArrayList<Exception> exceptions = new ArrayList<Exception>();
	    for(int i=0; i<futures.length; i++){
	    	try {
	            futures[i].get();
            } catch (Exception e) {
	            exceptions.add(e);
            }
	    }
	    
	    if (exceptions.size() > 0) {
	    	throw new MultiThreadedProcessingException(exceptions);
	    }
	}
}

class ProcessingException extends Exception {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	
}
