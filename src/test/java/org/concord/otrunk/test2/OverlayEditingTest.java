package org.concord.otrunk.test2;

import java.io.StringReader;
import java.net.URL;

import junit.framework.TestCase;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.otcore.OTClassProperty;
import org.concord.otrunk.OTrunkImpl;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.datamodel.OTTransientMapID;
import org.concord.otrunk.test.OTBasicTestObject;
import org.concord.otrunk.view.OTMLUserSession;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

/**
 * 
 * OverlayEditingTest <br>
 * This test checks to see how certain changes made to an overlay are saved
 * and loaded.
 * 
 * <p>
 * Date created: Sep 29, 2008
 * 
 * @author scytacki<p>
 *
 */
public class OverlayEditingTest extends TestCase
{
	public void testOverlayUnset() throws Exception
	{
		URL input = getClass().getResource("/overlay-editing-test.otml");
		Helper firstLoad = new NewUserDataHelper(){

			public void runTest()
            {
				// This should save that the property is unset into the overlay.
				root.otUnSet(intProp);

				// check that it appears the property is unset
				// The passed in authored content should have the int property set
				assertFalse("after unset the root object thinks the int prop is still set.", 
					root.otIsSet(intProp));
				assertTrue("root doesn't have the int prop overriden", 
					otrunk.hasOverrideInTopOverlay(intProp, root));				
            }

		};
		
		firstLoad.init(input);
		firstLoad.runTest();
		
		// write out the overlay and check that it handles the round tripping
		final String userDataString = firstLoad.getUserDataString();
		System.out.println(userDataString);

		Helper secondLoad = new Helper(){

			public OTMLUserSession getUserSession()
            {
				XMLDatabase loadedOTDatabase;
                try {
	                loadedOTDatabase = (XMLDatabase) viewerHelper.loadOTDatabase(new StringReader(userDataString), null);
                } catch (Exception e) {
	                e.printStackTrace();
	                return null;
                }
				return new OTMLUserSession(loadedOTDatabase, "test-user");
            }

			public void runTest()
            {
				// check that it appears the property is unset
				// The passed in authored content should have the int property set
				assertFalse("after unset the root object thinks the int prop is still set.", 
					root.otIsSet(intProp));
				assertTrue("root doesn't have the int prop overriden", 
					otrunk.hasOverrideInTopOverlay(intProp, root));
				
				assertFalse("root has the float prop set", root.otIsSet(floatProp));
				assertFalse("root has the float prop overriden", 
					otrunk.hasOverrideInTopOverlay(floatProp, root));					            
            }

			
		};

		secondLoad.init(input);
		secondLoad.runTest();
			
		String userDataString2 = secondLoad.getUserDataString();
		System.out.println(userDataString2);
	}
	
	public void testOverlayRemoveOverride() throws Exception
	{
		// load in same test.otml
		// set the int to be something other than 1 (which is authored version)
		// try removing that setting and see what happens
		
		URL input = getClass().getResource("/overlay-editing-test.otml");
		Helper firstLoad = new NewUserDataHelper(){

			public void runTest() throws Exception
            {
				int originalInt = root.getInt();
				
				root.setInt(2);

				printUserData();

				// check that it appears the property is unset
				// The passed in authored content should have the int property set
				assertTrue("after set the root object thinks the int prop is not set.", 
					root.otIsSet(intProp));
				assertTrue("root doesn't have the int prop overriden", 
					otrunk.hasOverrideInTopOverlay(intProp, root));
			
				assertTrue("Value of int is not 2", root.getInt() == 2);
				
				otrunk.removeOverrideInTopOverlay(intProp, root);

				assertTrue("Value of int is not the original", root.getInt() == originalInt);

				printUserData();
            }
			
		};
		
		firstLoad.init(input);
		firstLoad.runTest();
	}	
	
	public void testSetNull() throws Exception
	{
		URL input = getClass().getResource("/overlay-editing-test.otml");
		Helper firstLoad = new NewUserDataHelper(){

			public void runTest() throws Exception
            {
				OTObject origObj = root.getReference();
				
				root.setReference(null);

				printUserData();

				// check that it appears the property is unset
				assertFalse("after setting to null the root object thinks the ref prop is set.", 
					root.otIsSet(referenceProp));
				assertTrue("root doesn't have the int prop overriden", 
					otrunk.hasOverrideInTopOverlay(referenceProp, root));
			
				assertTrue("Value of refernece is not null", root.getReference() == null);
				
				otrunk.removeOverrideInTopOverlay(referenceProp, root);

				assertTrue("Value of reference is not the original", 
					root.getReference().equals(origObj));

				printUserData();
            }
			
		};
		
		firstLoad.init(input);
		firstLoad.runTest();		
	}
	
	
	abstract class Helper
	{
		protected OTViewerHelper viewerHelper;
		protected OTDatabase mainDb;
		protected OTMLUserSession userSession;
		protected OTBasicTestObject root;
		protected OTrunkImpl otrunk;
		protected OTClassProperty intProp;
		protected OTClassProperty floatProp;
		protected OTClassProperty referenceProp;			
		
		public void init(URL input) throws Exception
		{
			// load in the first argument as an otml file
			// assume the root object is a folder, and then 
			// get the first child of the folder and
			// copy it and store the copy as the second
			// object in the folder
			viewerHelper = new OTViewerHelper();
			
			mainDb = viewerHelper.loadOTDatabase(input);

			viewerHelper.loadOTrunk(mainDb, null);
			
			userSession = getUserSession();
			viewerHelper.loadUserSession(userSession);
					
			// This root should be in the overlay
			root = (OTBasicTestObject)viewerHelper.getRootObject();
			OTID globalId = root.getGlobalId();
			assertTrue("returned root is not from an overlay", globalId instanceof OTTransientMapID);

			otrunk = (OTrunkImpl) root.getOTObjectService().getOTrunkService(OTrunk.class);
					
			// We want to try setting some properties to null
			// Assume the root object is a OTBasicTestObject
			intProp = root.otClass().getProperty("int");
			floatProp = root.otClass().getProperty("float");
			referenceProp = root.otClass().getProperty("reference");
		}
		
		public String getUserDataString() throws Exception
		{
			OTDatabase userDataDb = userSession.getUserDataDb();
			return viewerHelper.saveOTDatabase(userDataDb);
		}
		
		public void printUserData() throws Exception
		{
			String userDataString = getUserDataString();
			System.out.println(userDataString);
		}
		
		public abstract OTMLUserSession getUserSession();
		
		public abstract void runTest() throws Exception;
	}
	
	abstract class NewUserDataHelper extends Helper
	{
		public OTMLUserSession getUserSession()
        {
            return new OTMLUserSession();
        }
		
		public void init(URL arg0)
		    throws Exception
		{
		    super.init(arg0);
		    
			// The passed in authored content should have the int property set
			assertTrue("root doesn't have the int prop set", root.otIsSet(intProp));
			assertFalse("root already has the int prop overriden", 
				otrunk.hasOverrideInTopOverlay(intProp, root));
			
			assertFalse("root has the float prop set", root.otIsSet(floatProp));
			assertFalse("root has the float prop overriden", 
				otrunk.hasOverrideInTopOverlay(floatProp, root));	            
		}
	}
	
}
