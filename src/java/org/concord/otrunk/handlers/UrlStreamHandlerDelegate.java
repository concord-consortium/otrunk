package org.concord.otrunk.handlers;

import java.io.*;
import java.net.*;

/**
 * This class is from http://code.google.com/p/livcos/source/browse/CustomUrlProtocol/trunk/src/org/livcos/java/net/protocol/UrlStreamHandlerDelegate.java,
 * which is discussed here http://brunof.dyndns.org:8080/livcos/livcos.org/Map/web/nav.html?map=/livcos.org/data/map/dev/UrlStreamHandler,
 * in response to this java bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4648098
 * 
 * No license is given, but owner gives his consent for use in the last link above. If we end up keeping this, we should contact owner for license.
 */
public class UrlStreamHandlerDelegate extends URLStreamHandler {

  public static final String JAVA_PROTOCOL_HANDLER_PKGS = "java.protocol.handler.pkgs";

  private BaseUrlStreamHandler handler;

  public UrlStreamHandlerDelegate() {
    super();
    String protocol = UrlStreamHandlerFactory.getProtocol(this.getClass());
    this.handler = (BaseUrlStreamHandler)UrlStreamHandlerFactory.getInstance().createURLStreamHandler(protocol);
  }

  public static void registerSystemProperty() {
    // try to set the system property...
    // some implementations might not reread this property and you have to
    // explicitly specify it at startup.
    String pkgPrefix = UrlStreamHandlerDelegate.class.getPackage().getName();
    String pkgs = System.getProperty(JAVA_PROTOCOL_HANDLER_PKGS);
    if ((null == pkgs) || ("".equals(pkgs.trim())))
      System.setProperty(JAVA_PROTOCOL_HANDLER_PKGS, pkgPrefix);
    else if (!pkgs.contains(pkgPrefix))
      System.setProperty(JAVA_PROTOCOL_HANDLER_PKGS, pkgs + "|" + pkgPrefix);
  }

  protected URLConnection openConnection(URL u) throws IOException {
    return this.handler.openConnectionWrap(u);
  }

}
