package org.concord.otrunk.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.concord.framework.otrunk.OTrunk;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

/**
 * This is intended to be extended so test round tripping various OTrunk
 * properties
 * 
 * @author scytacki
 *
 */
public class RoundTrip extends TestCase
{
	protected OTViewerHelper viewerHelper;
	XMLDatabase db;
	protected OTrunk otrunk;
	
	public void initOTrunk() throws Exception
	{
		viewerHelper = new OTViewerHelper();

		// create  an empty database
		db = new XMLDatabase();

        viewerHelper.loadOTrunk2(null, db);

        otrunk = viewerHelper.getOtrunk();		
	}

	public void reload() throws Exception
	{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		viewerHelper.saveOTDatabase(db, output);
		output.close();
		
		InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(output.toByteArray()));
		BufferedReader bufReader = new BufferedReader(reader);
		String line;
		while((line = bufReader.readLine()) != null){
			System.out.println(line);
		}
		
		viewerHelper = new OTViewerHelper();
		
		db = (XMLDatabase) viewerHelper.loadOTDatabase(new ByteArrayInputStream(output.toByteArray()), 
			null);
		
		viewerHelper.loadOTrunk2(null, db);		
	}	
}
