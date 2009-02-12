package org.concord.otrunk.handlers;

/**
 * This class is from http://code.google.com/p/livcos/source/browse/CustomUrlProtocol/trunk/src/org/livcos/java/net/protocol/BaseUrlStreamHandler.java,
 * which is discussed here http://brunof.dyndns.org:8080/livcos/livcos.org/Map/web/nav.html?map=/livcos.org/data/map/dev/UrlStreamHandler,
 * in response to this java bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4648098
 * 
 * No license is given, but owner gives his consent for use in the last link above. If we end up keeping this, we should contact owner for license.
 */

import java.io.*;
import java.net.*;

public abstract class BaseUrlStreamHandler extends URLStreamHandler {

  final URLConnection openConnectionWrap(URL u) throws IOException {
    return this.openConnection(u);
  }

}
