package org.concord.otrunk.overlay;

import java.net.URL;
import java.util.logging.Logger;

import org.concord.framework.otrunk.OTObject;
import org.concord.otrunk.OTrunkImpl;

public class OTUserOverlayManagerFactory
{
	private static final Logger logger = Logger.getLogger(OTUserOverlayManagerFactory.class.getName());
	/**
	 * returns an appropriate instance of an OTUserOverlayManager based on the type of object in the root of the url passed in.
	 * If the object is an OTOverlay, then it returns an OTUserSingleOverlayManager, otherwise it returns a default OTUserMappedOverlayManager
	 * @param url
	 * @param otrunk
	 * @return
	 */
	public static OTUserOverlayManager getUserOverlayManager(URL url, OTrunkImpl otrunk) {
		if (url != null) {
    		try {
    	        OTObject externalObject = otrunk.getExternalObject(url, otrunk.getRootObjectService());
    	        if (externalObject instanceof OTOverlay) {
    	        	return new OTUserSingleOverlayManager(otrunk);
    	        }
            } catch (Exception e) {
    	        // TODO Auto-generated catch block
    	        e.printStackTrace();
            }
		}
		if (Boolean.getBoolean("otrunk.intrassession.use_single_overlay")) {
			logger.info("Using SINGLE overlay manager");
			return new OTUserSingleOverlayManager(otrunk);
		}
		logger.info("Using MAPPED overlay manager");
        return new OTUserMappedOverlayManager(otrunk);
	}
}
