package org.concord.otrunk.handlers;

/**
 * This class is from http://code.google.com/p/livcos/source/browse/CustomUrlProtocol/trunk/src/org/livcos/java/net/protocol/UrlStreamHandlerFactory.java,
 * which is discussed here http://brunof.dyndns.org:8080/livcos/livcos.org/Map/web/nav.html?map=/livcos.org/data/map/dev/UrlStreamHandler,
 * in response to this java bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4648098
 * 
 * No license is given, but owner gives his consent for use in the last link above. If we end up keeping this, we should contact owner for license.
 */
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.logging.Logger;

// not yet tested...
public class UrlStreamHandlerFactory implements URLStreamHandlerFactory {
	private static final Logger logger =
		Logger.getLogger(UrlStreamHandlerFactory.class.getCanonicalName());

	public static final String JAVA_PROTOCOL_HANDLER_PKGS = "java.protocol.handler.pkgs";

	private static UrlStreamHandlerFactory instance = null;

	private HashMap handlerClasses = new HashMap();

	private UrlStreamHandlerFactory() {
		super();
	}

	public static UrlStreamHandlerFactory getInstance() {
		return instance;
	}

	public static void registerHandlerClass(Class handlerClass) throws Exception {
		synchronized (UrlStreamHandlerFactory.class) {
			if (null == instance) {
				instance = new UrlStreamHandlerFactory();
				try {
					URL.setURLStreamHandlerFactory(instance);
				}
				catch (Error e) {
					logger.finer("Another URLStreamHandlerFactory has already been registered");
					if (BaseUrlStreamHandler.class.isAssignableFrom(handlerClass)){
						logger.finer("This handler is a BaseUrlStreamHandler, " +
							"so we'll try to register system properties");
						UrlStreamHandlerDelegate.registerSystemProperty();
					} else
						throw new Exception("A URL stream handler factory has been set already! "
							+ "You can try to register a BaseUrlStreamHandler class instead.");
				}
			}
		}
		String protocol = UrlStreamHandlerFactory.getProtocol(handlerClass);
		instance.handlerClasses.put(protocol, handlerClass);
	}

	public static String getProtocol(Class handlerClass) {
		try {
			Field[] fields = handlerClass.getFields();
			for (int i = 0; i < fields.length; i++) {
				Field f = fields[i];
				if ("PROTOCOL_NAME".equals(f.getName()))
					return f.get(null).toString();
				if (f.getName().startsWith("URL_PROTOCOL_"))
					return f.get(null).toString();
			}
		}
		catch (Exception ex) {
			// just a try...
		}
		// take the package name for the protocol, if not specified by constant.
		String pn = handlerClass.getPackage().getName();
		return pn.substring(pn.lastIndexOf('.') + 1);
	}

	public URLStreamHandler createURLStreamHandler(String protocol) {
		Class handlerClass = (Class)this.handlerClasses.get(protocol);
		if (null == handlerClass)
			return null;
		try {
			return (URLStreamHandler)handlerClass.newInstance();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}

