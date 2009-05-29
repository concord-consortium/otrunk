package org.concord.otrunk.net;

import java.io.IOException;

public class HTTPRequestException extends IOException {
    private static final long serialVersionUID = 1L;
	private int responseCode;
	
	public HTTPRequestException(String msg, int responseCode) {
		super(msg);
		this.responseCode = responseCode;
	}
	
	// if we ever move to Java 1.6 we can enable this...
//	public HTTPRequestException(String msg, Throwable t, int responseCode) {
//		super(msg, t);
//		this.responseCode = responseCode;
//	}

	/**
     * @return the responseCode
     */
    public int getResponseCode()
    {
	    return responseCode;
    }

}
