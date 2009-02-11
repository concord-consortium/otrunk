package org.concord.otrunk.handlers.jres;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Handler extends java.net.URLStreamHandler
{

	@Override
    protected URLConnection openConnection(URL u)
        throws IOException
    {
	    return new JResConnection(u);
    }

}
