package org.concord.otrunk.test;

import java.io.ByteArrayInputStream;

import org.concord.otrunk.view.OTViewerHelper;

public class EmptyXMLDatabaseTest
{
	public static void main(String[] args) throws Exception
    {
		byte [] emptyArray = new byte [0];
		ByteArrayInputStream inStream = new ByteArrayInputStream(emptyArray); 

		OTViewerHelper viewerHelper = new OTViewerHelper();

		viewerHelper.loadOTDatabase(inStream, null);

    }
}
