package org.concord.otrunk.util;

/**
 * Runnable interface designed for processing items one at a time.
 * 
 * The typical workflow will involve external code doing:
 *   MultiThreadedProcessorRunnable r = new MultiThreadedProcessorRunnable() { ... };
 *   r.setItem(someItem);
 *   r.run();
 *   r.setItem(someOtherItem);
 *   r.run();
 *   ...
 * @author aunger
 *
 * @param <T>
 */
public interface MultiThreadedProcessorRunnable<T> extends Runnable
{
	public void setItem(T item);
}
