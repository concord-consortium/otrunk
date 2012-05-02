package org.concord.otrunk.test;

import java.io.ByteArrayOutputStream;

import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.overlay.CompositeDatabase;
import org.concord.otrunk.user.OTUserObject;

/**
 * This is intended to be extended so test round tripping various OTrunk
 * properties
 * 
 * @author scytacki
 *
 */
public class RotatingRoundTripHelperLearner extends RoundTripHelperLearner
{
	public OTUserObject getLearnerUser() {
		return userSession.getUserObject();
	}

	public CompositeDatabase getReferenceMapDb() {
		return otrunk.getCompositeDatabases().get(getLearnerUser().getGlobalId());
	}
	
	public OTrunkImpl getOTrunk()
    {
	    return otrunk;
    }
	
	public String getExportedReferenceMapDb() throws Exception {
		CompositeDatabase db = getReferenceMapDb();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		viewerHelper.saveOTDatabase(db.getActiveOverlay().getOverlayDatabase(), baos);
		return baos.toString();
	}
}
