package org.concord.otrunk.test2;

import java.net.URL;

import org.concord.framework.util.TimeProvider;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.test.RoundTrip;
import org.concord.otrunk.view.OTViewerHelper;

public class TimeProviderTest extends RoundTrip
{	
	
	public void testTimeProviderExists() throws Exception
	{
		OTrunkImpl otrunk = helper(getClass().getResource("/copy-test.otml"));
		
		TimeProvider provider = otrunk.getService(TimeProvider.class);
		
		assertNotNull(provider);
		assertEquals(provider.getSystemDrift(), 0);
	}

	private OTrunkImpl helper(URL input) throws Exception
	{
		OTViewerHelper viewerHelper = new OTViewerHelper();
		
		OTDatabase mainDb = viewerHelper.loadOTDatabase(input);

		viewerHelper.loadOTrunk(mainDb, null);
		
		OTrunkImpl otrunk = (OTrunkImpl)viewerHelper.getOtrunk();
		
		return otrunk;
	}
}
