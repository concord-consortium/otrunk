/**
 * 
 */
package org.concord.otrunk.test2;

public class EventQueueExceptionHandler{
	public void handle(Throwable t){
		// need to send this to the test so it can assert it there and fail the test
		MemoryHogTest.eventQueueThrowable = t;
	}
}