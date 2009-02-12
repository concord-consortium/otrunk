package org.concord.otrunk.test2;

import java.net.URL;

import junit.framework.TestCase;

import org.concord.otrunk.transfer.URLStreamHandler;

public class URLStreamHandlerTest extends TestCase
{
	public void test404Error()
        throws Exception
    {
		URLStreamHandler streamHandler = 
			new URLStreamHandler(new URL("http://dev.concord.org/should-not-work"));

		// this should throw an exception and print an error
		try {
			streamHandler.getURLStream();
			assertTrue("An exception was not thrown", false);
		} catch (Exception e){
			// we really should throw and catch more specific exceptions here
			return;
		}
    }
}
