package org.concord.otrunk.util;

/**
 * Runnable interface designed for processing items one at a time.
 * 
 * The typical workflow will involve external code doing:
 *   MultiThreadedProcessorRunnable r = new MultiThreadedProcessorRunnable() { ... };
 *   r.process(someItem);
 *   r.process(someOtherItem);
 *   ...
 * @author aunger
 *
 * @param <T>
 */
public interface MultiThreadedProcessorRunnable<T>
{
	/**
	 * Method to process the passed in item.
	 * 
	 * MAKE SURE THIS METHOD IS THREADSAFE, as it will be called simultaneously by multiple threads.
	 * @param item
	 */
	public void process(T item);
}
