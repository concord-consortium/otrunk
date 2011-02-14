package org.concord.otrunk.util;

import java.util.ArrayList;

public class MultiThreadedProcessingException extends Exception
{
    private static final long serialVersionUID = 1L;
	private ArrayList<Exception> exceptions;
    
    public MultiThreadedProcessingException(ArrayList<Exception> exceptions) {
    	super("Error processing all elements.");
    	this.exceptions = exceptions;
    }
    
    public ArrayList<Exception> getExceptions() {
    	return exceptions;
    }
}
