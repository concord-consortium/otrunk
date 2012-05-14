package org.concord.otrunk.test2;

import static org.fest.assertions.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.overlay.CompositeDatabase;
import org.concord.otrunk.overlay.RotatingReferenceMapDatabase;
import org.concord.otrunk.test.OTBasicTestObject;
import org.concord.otrunk.test.OTListTestObject;
import org.concord.otrunk.test.OTMapTestObject;
import org.concord.otrunk.test.OTMultiReferenceTestObject;
import org.concord.otrunk.test.OTPrimitivesTestObject;
import org.concord.otrunk.test.RotatingRoundTripHelperLearner;
import org.concord.otrunk.user.OTReferenceMap;
import org.concord.otrunk.view.OTConfig;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class PeriodicUploadingLearnerDataTest
{
	private static RotatingRoundTripHelperLearner helper;
	private static final Logger logger = Logger.getLogger(PeriodicUploadingLearnerDataTest.class.getName());
	
//	@Rule
//	public ClientDriverRule driver = new ClientDriverRule();
	
	@BeforeClass
	public static void setup() {
		System.setProperty(OTConfig.PERIODIC_UPLOADING_USER_DATA, "true");
		helper = new RotatingRoundTripHelperLearner();
	}
	
	@Test
	public void testPrimitiveObjectAttributes() throws Exception {
		// create an object
		// set some attributes
		// rotate the database
		// make sure the the previous attributes are still set
		// set some more, unset one
		// rotate the database
		// make sure the attributes are still set/unset
		
		helper.initOTrunk(OTPrimitivesTestObject.class);
		
		OTPrimitivesTestObject root = (OTPrimitivesTestObject) helper.getRootObject();
		
		String name = "Object-name";
		String s = "Some test string";
		int i = 34;
		float f = 67.89f;
		
		root.setName(name);
		root.setString(s);
		root.setInt(i);
		root.setFloat(f);
		
		assertThat(root.getString()).as("String attribute").isEqualTo(s);
		assertThat(root.getInt()).as("Int attribute").isEqualTo(i);
		assertThat(root.getFloat()).as("Float attribute").isEqualTo(f);
		assertThat(root.getName()).as("Name attribute").isEqualTo(name);
		
		// Rotate
		rotate();
		
		assertThat(root.getString()).as("String attribute").isEqualTo(s);
		assertThat(root.getInt()).as("Int attribute").isEqualTo(i);
		assertThat(root.getFloat()).as("Float attribute").isEqualTo(f);
		assertThat(root.getName()).as("Name attribute").isEqualTo(name);
		
		double d = 1.34524784;
		boolean b = true;
		
		root.setDouble(d);
		root.setBoolean(b);
		root.otUnSet(root.otClass().getProperty("string"));
		root.otUnSet(root.otClass().getProperty("name"));
		
		assertThat(root.getDouble()).as("Double attribute").isEqualTo(d);
		assertThat(root.getBoolean()).as("Boolean attribute").isEqualTo(b);
		assertThat(root.getString()).as("String attribute").isEqualTo("");
		assertThat(root.getName()).as("Name attribute").isEqualTo(null);
		
		// rotate
		rotate();

		assertThat(root.getInt()).as("Int attribute").isEqualTo(i);
		assertThat(root.getFloat()).as("Float attribute").isEqualTo(f);
		assertThat(root.getDouble()).as("Double attribute").isEqualTo(d);
		assertThat(root.getBoolean()).as("Boolean attribute").isEqualTo(b);
		assertThat(root.getString()).as("String attribute").isEqualTo("");
		assertThat(root.getName()).as("Name attribute").isEqualTo(null);
		

		root.setBoolean(false); // set an attribute so the object will get written out
		
		// Verify exported otml looks like we expect
		verifyOutputOtml("/exporter-jdom-expected-results/rotated-primitives.xml", helper.getExportedReferenceMapDb());
	}
	
	@Test
	public void testObjectListAttributes() throws Exception {
		helper.initOTrunk(OTListTestObject.class);
		
		OTListTestObject root = (OTListTestObject) helper.getRootObject();
		
		ArrayList<Object> testObjects = new ArrayList<Object>();
		
		for (int i = 0; i < 4; i++) {
			OTBasicTestObject obj = helper.createObject(OTBasicTestObject.class);
			obj.setName("Item: " + i);
			testObjects.add(obj);
			root.getObjectList().add(obj);
		}
		
		verifyListsMatch(testObjects, root.getObjectList());
		
		rotate();
		
		verifyListsMatch(testObjects, root.getObjectList());
		
		for (int i = 4; i < 7; i++) {
			OTBasicTestObject obj = helper.createObject(OTBasicTestObject.class);
			obj.setName("Item: " + i);
			testObjects.add(obj);
			root.getObjectList().add(obj);
		}
		
		verifyListsMatch(testObjects, root.getObjectList());
		
		rotate();
		
		verifyListsMatch(testObjects, root.getObjectList());
		
		for (int i : new int[] {2,4}) {
			testObjects.remove(i);
			root.getObjectList().remove(i);
		}
		
		verifyListsMatch(testObjects, root.getObjectList());
		
		rotate();
		
		verifyListsMatch(testObjects, root.getObjectList());
		
		root.getObjectList().remove(0);
		
		// Verify exported otml looks like we expect
		verifyOutputOtml("/exporter-jdom-expected-results/rotated-object-list.xml", helper.getExportedReferenceMapDb());
	}
	
	@Test
	public void testResourceListAttributes() throws Exception {
		helper.initOTrunk(OTListTestObject.class);
		
		OTListTestObject root = (OTListTestObject) helper.getRootObject();
		
		ArrayList<Object> testObjects = new ArrayList<Object>();
		
		for (int i = 0; i < 4; i++) {
			String s = "Item: " + i;
			testObjects.add(s);
			root.getResourceList().add(s);
		}
		
		verifyListsMatch(testObjects, root.getResourceList());
		
		rotate();
		
		verifyListsMatch(testObjects, root.getResourceList());
		
		for (int i = 4; i < 7; i++) {
			String s = "Item: " + i;
			testObjects.add(s);
			root.getResourceList().add(s);
		}
		
		verifyListsMatch(testObjects, root.getResourceList());
		
		rotate();
		
		verifyListsMatch(testObjects, root.getResourceList());
		
		for (int i : new int[] {2,4}) {
			testObjects.remove(i);
			root.getResourceList().remove(i);
		}
		
		verifyListsMatch(testObjects, root.getResourceList());
		
		rotate();
		
		verifyListsMatch(testObjects, root.getResourceList());
		
		root.getResourceList().remove(0);
		
		// Verify exported otml looks like we expect
		verifyOutputOtml("/exporter-jdom-expected-results/rotated-resource-list.xml", helper.getExportedReferenceMapDb());
	}
	
	@Test
	public void testObjectMapAttributes() throws Exception {
		helper.initOTrunk(OTMapTestObject.class);
		
		OTMapTestObject root = (OTMapTestObject) helper.getRootObject();
		
		HashMap<String, Object> testObjects = new HashMap<String, Object>();
		
		for (int i = 0; i < 4; i++) {
			String s = "Item: " + i;
			String name = "Value: " + (i*i);
			OTBasicTestObject v = helper.createObject(OTBasicTestObject.class);
			v.setName(name);
			testObjects.put(s,v);
			root.getObjectMap().putObject(s,v);
		}
		
		verifyMapsMatch(testObjects, root.getObjectMap());
		
		rotate();
		
		verifyMapsMatch(testObjects, root.getObjectMap());
		
		for (int i = 4; i < 7; i++) {
			String s = "Item: " + i;
			String name = "Value: " + (i*i);
			OTBasicTestObject v = helper.createObject(OTBasicTestObject.class);
			v.setName(name);
			testObjects.put(s,v);
			root.getObjectMap().putObject(s,v);
		}
		
		verifyMapsMatch(testObjects, root.getObjectMap());
		
		rotate();
		
		verifyMapsMatch(testObjects, root.getObjectMap());
		
		OTBasicTestObject v = helper.createObject(OTBasicTestObject.class);
		v.setName("Some name");
		root.getObjectMap().putObject("Change Item",v);
		
		// Verify exported otml looks like we expect
		verifyOutputOtml("/exporter-jdom-expected-results/rotated-object-map.xml", helper.getExportedReferenceMapDb());
		
		testObjects.clear();
		root.getObjectMap().clear();
		
		verifyMapsMatch(testObjects, root.getObjectMap());
		
		rotate();
		
		verifyMapsMatch(testObjects, root.getObjectMap());
	}
	
	@Test
	public void testResourceMapAttributes() throws Exception {
		helper.initOTrunk(OTMapTestObject.class);
		
		OTMapTestObject root = (OTMapTestObject) helper.getRootObject();
		
		HashMap<String, Object> testObjects = new HashMap<String, Object>();
		
		for (int i = 0; i < 4; i++) {
			String s = "Item: " + i;
			String v = "Value: " + (i*i);
			testObjects.put(s,v);
			root.getResourceMap().put(s,v);
		}
		
		verifyMapsMatch(testObjects, root.getResourceMap());
		
		rotate();
		
		verifyMapsMatch(testObjects, root.getResourceMap());
		
		for (int i = 4; i < 7; i++) {
			String s = "Item: " + i;
			String v = "Value: " + (i*i);
			testObjects.put(s,v);
			root.getResourceMap().put(s,v);
		}
		
		verifyMapsMatch(testObjects, root.getResourceMap());
		
		rotate();
		
		verifyMapsMatch(testObjects, root.getResourceMap());
		
		String v = "Some name";
		root.getResourceMap().put("Change Item",v);
		
		// Verify exported otml looks like we expect
		verifyOutputOtml("/exporter-jdom-expected-results/rotated-resource-map.xml", helper.getExportedReferenceMapDb());
		
		testObjects.clear();
		root.getResourceMap().clear();
		
		verifyMapsMatch(testObjects, root.getResourceMap());
		
		rotate();
		
		verifyMapsMatch(testObjects, root.getResourceMap());
	}
	
	/*
	 * Basically, verify that changing an object that was created within the delta layer (non-composite) will write
	 * out the closest ancestor that's a composite object.
	 */
	@Test
	public void testOnlyChangedCreatedObjectsGetWritten() throws Exception {
		helper.initOTrunk(OTBasicTestObject.class);
		
		OTBasicTestObject root = (OTBasicTestObject) helper.getRootObject();
		root.setName("Root");
		
		OTBasicTestObject child1 = helper.createObject(OTBasicTestObject.class);
		child1.setName("Child 1");
		
		OTBasicTestObject child2 = helper.createObject(OTBasicTestObject.class);
		child2.setName("Child 2");
		
		OTBasicTestObject child3 = helper.createObject(OTBasicTestObject.class);
		child3.setName("Child 3");
		
		root.setReference(child1);
		child1.setReference(child2);
		child2.setReference(child3);
		
		verifyOutputRegexpOtml("/exporter-jdom-expected-results/rotated-multiple-created-initial.xml", helper.getExportedReferenceMapDb());

		rotate();
		
		verifyOutputRegexpOtml("/exporter-jdom-expected-results/rotated-multiple-clean.xml", helper.getExportedReferenceMapDb());

		child3.setString("Something changed, too");
		
		verifyOutputRegexpOtml("/exporter-jdom-expected-results/rotated-multiple-created-changed.xml", helper.getExportedReferenceMapDb());
	}
	
	@Test
	public void testOnlyChangedObjectsGetWritten() throws Exception {
		testOnlyChangedObjectGetWrittenImpl(false);
	}
	
	@Test
	@Ignore
	public void testUploading() throws Exception {
		// TODO Configure capturing requests and make sure the requests look correct.
//		System.setProperty(OTConfig.PERIODIC_UPLOADING_USER_DATA_URL, driver.getBaseUrl() + "/bundles");
		testOnlyChangedObjectGetWrittenImpl(true);
	}
	
	@Test
	public void testCreatedObjectsMaintainConsistentIds() throws Exception {
		helper.initOTrunk(OTMultiReferenceTestObject.class);
		
		OTMultiReferenceTestObject root = (OTMultiReferenceTestObject) helper.getRootObject();
		root.setName("Root");
		
		OTBasicTestObject child1 = helper.createObject(OTBasicTestObject.class);
		child1.setName("Child 1");
		
		OTMultiReferenceTestObject child2 = helper.createObject(OTMultiReferenceTestObject.class);
		child2.setName("Child 2");
		
		OTMultiReferenceTestObject child3 = helper.createObject(OTMultiReferenceTestObject.class);
		child3.setName("Child 3");
		
		OTID child1Id = child1.getGlobalId();
		OTID child2Id = child2.getGlobalId();
		
		root.setReference(child3);
		root.setData(child2);
		root.setStuff(child1);
		
		child3.setReference(child1);
		child3.setObject(child2);

		String expected1 = ".*<OTBasicTestObject id=\"" + child1Id.getMappedId().toExternalForm() + "\" name=\"Child 1\" />.*";
		String expected2 = ".*<OTMultiReferenceTestObject id=\"" + child2Id.getMappedId().toExternalForm() + "\" name=\"Child 2\" />.*";
		String expected3 = ".*<object refid=\"" + child2Id.getMappedId().toExternalForm() + "\" />.*";
		String expected4 = ".*<object refid=\"" + child1Id.getMappedId().toExternalForm() + "\" />.*";

		verifyOutputRegexpOtml(expected1, helper.getExportedReferenceMapDb());
		verifyOutputRegexpOtml(expected2, helper.getExportedReferenceMapDb());
		verifyOutputRegexpOtml(expected3, helper.getExportedReferenceMapDb());
		verifyOutputRegexpOtml(expected4, helper.getExportedReferenceMapDb());
		
		rotate();
		
		root.setName("Something different");
		
		verifyOutputRegexpOtml(expected1, helper.getExportedReferenceMapDb());
		verifyOutputRegexpOtml(expected2, helper.getExportedReferenceMapDb());
		verifyOutputRegexpOtml(expected3, helper.getExportedReferenceMapDb());
		verifyOutputRegexpOtml(expected4, helper.getExportedReferenceMapDb());
		
		rotate();
		
		child3.setName("A new name");
		
		verifyOutputRegexpOtml(expected1, helper.getExportedReferenceMapDb());
		verifyOutputRegexpOtml(expected2, helper.getExportedReferenceMapDb());
		verifyOutputRegexpOtml(expected3, helper.getExportedReferenceMapDb());
		verifyOutputRegexpOtml(expected4, helper.getExportedReferenceMapDb());
	}
	
	@Test
	public void testCreatedObjectsWithMultipleParentsAlwaysExportId() throws Exception {
		helper.initOTrunk(OTMultiReferenceTestObject.class);

		// Authored tree setup
		OTMultiReferenceTestObject authoredRoot = (OTMultiReferenceTestObject) helper.getAuthoredRootObject();
		authoredRoot.setName("Root");
		
		OTMultiReferenceTestObject authoredChild3 = helper.createObject(OTMultiReferenceTestObject.class, authoredRoot);
		authoredChild3.setName("Child 3");
		
		OTMultiReferenceTestObject authoredChild4 = helper.createObject(OTMultiReferenceTestObject.class, authoredRoot);
		authoredChild4.setName("Child 4");
		
		authoredRoot.setReference(authoredChild3);
		authoredRoot.setData(authoredChild4);
		
		// Student data manipulation
		
		OTMultiReferenceTestObject root = (OTMultiReferenceTestObject) helper.getRootObject();
		OTMultiReferenceTestObject child3 = (OTMultiReferenceTestObject) root.getReference();
		OTMultiReferenceTestObject child4 = (OTMultiReferenceTestObject) root.getData();
		
		OTBasicTestObject child1 = helper.createObject(OTBasicTestObject.class);
		child1.setName("Child 1");
		
		OTID child1Id = child1.getGlobalId();

		child3.setReference(child1);
		
		String expected1 = ".*<entry key=\"" + child3.getGlobalId().getMappedId().toExternalForm() + "\">.*<reference>.*<OTBasicTestObject name=\"Child 1\" />.*</reference>.*</entry>.*";
		verifyOutputRegexpOtml(expected1, helper.getExportedReferenceMapDb());
		
		rotate();

		child4.setData(child1);
		
		String expected2 = ".*<entry key=\"" + child4.getGlobalId().getMappedId().toExternalForm() + "\">.*<data>.*<OTBasicTestObject id=\"" + child1Id.getMappedId().toExternalForm() + "\" name=\"Child 1\" />.*</data>.*</entry>.*";
		verifyOutputRegexpOtml(expected2, helper.getExportedReferenceMapDb());
		
		rotate();
		
		child3.setReference(null);
		child4.setData(null);
		child3.setReference(child1);
		child4.setData(child1);
		
		String expected3a = ".*<entry key=\"" + child3.getGlobalId().getMappedId().toExternalForm() + "\">.*<reference>.*<OTBasicTestObject id=\"" + child1Id.getMappedId().toExternalForm() + "\" name=\"Child 1\" />.*</reference>.*</entry>.*";
		String expected3b = "<entry key=\"" + child4.getGlobalId().getMappedId().toExternalForm() + "\">.*<data>.*<object refid=\"" + child1Id.getMappedId().toExternalForm() + "\" />.*</data>.*</entry>.*";
		verifyOutputRegexpOtml(expected3a + expected3b, helper.getExportedReferenceMapDb());
	}
	
	private void testOnlyChangedObjectGetWrittenImpl(boolean checkHttp) throws Exception {
		helper.initOTrunk(OTBasicTestObject.class);
		
		OTBasicTestObject root = (OTBasicTestObject) helper.getAuthoredRootObject();
		root.setName("Root");
		
		OTBasicTestObject child1 = helper.createObject(OTBasicTestObject.class, root);
		child1.setName("Child 1");
		
		OTBasicTestObject child2 = helper.createObject(OTBasicTestObject.class, root);
		child2.setName("Child 2");
		
		OTBasicTestObject child3 = helper.createObject(OTBasicTestObject.class, root);
		child3.setName("Child 3");
		
		root.setReference(child1);
		child1.setReference(child2);
		child2.setReference(child3);
		
		verifyOutputRegexpOtml("/exporter-jdom-expected-results/rotated-multiple-clean.xml", helper.getExportedReferenceMapDb());
		if (checkHttp) {
//			driver.addExpectation(onRequestTo("/bundles"), giveEmptyResponse().withStatus(201));
		}
		rotate();
		
		root = (OTBasicTestObject) helper.getRootObject();
		child1 = (OTBasicTestObject) root.getReference();
		child2 = (OTBasicTestObject) child1.getReference();
		child3 = (OTBasicTestObject) child2.getReference();
		
		verifyOutputRegexpOtml("/exporter-jdom-expected-results/rotated-multiple-clean.xml", helper.getExportedReferenceMapDb());
		
		root.setString("Something changed");
		child2.setString("Something changed, three");
		
		verifyOutputRegexpOtml("/exporter-jdom-expected-results/rotated-multiple-changed.xml", helper.getExportedReferenceMapDb());

		if (checkHttp) {
//			driver.addExpectation(onRequestTo("/bundles"), giveEmptyResponse().withStatus(201));
		}
		rotate();

		child1.setString("Something changed, too");
		child3.setString("Something changed, four");
		
		verifyOutputRegexpOtml("/exporter-jdom-expected-results/rotated-multiple-changed2.xml", helper.getExportedReferenceMapDb());
		
		if (checkHttp) {
//			driver.addExpectation(onRequestTo("/bundles"), giveEmptyResponse().withStatus(201));
		}
		rotate();
	}
	
	private OTReferenceMap rotate() throws Exception {
		CompositeDatabase referenceMapDb = helper.getReferenceMapDb();
		if (referenceMapDb instanceof RotatingReferenceMapDatabase) {
			return helper.getOTrunk().rotateAndSaveUserDatabase((RotatingReferenceMapDatabase) referenceMapDb);
		}
		return null;
	}
	
	private void verifyListsMatch(ArrayList<Object> expected, OTObjectList received) {
		assertThat(expected).containsExactly(received.toArray());
	}
	
	private void verifyListsMatch(ArrayList<Object> expected, OTResourceList received) {
		assertThat(expected).containsExactly(received.toArray());
	}
	
	private void verifyMapsMatch(HashMap<String, Object> expected, OTObjectMap received) {
		Vector<String> receivedKeys = received.getObjectKeys();
		assertThat(receivedKeys).as("Map keys").containsOnly(expected.keySet().toArray());
		for (String key : receivedKeys) {
			assertThat(received.getObject(key)).as("Map value for key: " + key).isEqualTo(expected.get(key));
		}
	}
	
	private void verifyMapsMatch(HashMap<String, Object> expected, OTResourceMap received) {
		String[] receivedKeys = received.getKeys();
		assertThat(receivedKeys).as("Map keys").containsOnly(expected.keySet().toArray());
		for (String key : receivedKeys) {
			assertThat(received.get(key)).as("Map value for key: " + key).isEqualTo(expected.get(key));
		}
	}
	
	private void verifyOutputOtml(String expectedResource, String actualOutput) throws Exception {
		String expectedOutput = getExpectedOutput(expectedResource);

        assertThat(actualOutput).as("OTML export").contains(expectedOutput);
	}
	
	private void verifyOutputRegexpOtml(String expectedResource, String actualOutput) throws Exception {
		verifyOutputRegexpOtml(expectedResource, actualOutput, "");
	}
	
	private void verifyOutputRegexpOtml(String expectedResource, String actualOutput, String as) throws Exception {
		String expectedOutput = getExpectedOutput(expectedResource);
		Pattern regexp = Pattern.compile(expectedOutput, Pattern.DOTALL | Pattern.MULTILINE);
		Matcher m = regexp.matcher(actualOutput);
		if (m.matches()) {
			// awesome!
		} else {
			assertThat(actualOutput).as("OTML export: " + as).matches(expectedOutput);
		}
	}

	private String getExpectedOutput(String expectedResource)
        throws IOException
    {
	    URL expectedUrl = PeriodicUploadingLearnerDataTest.class.getResource(expectedResource);
	    if (expectedUrl == null) {
	    	// assume that we weren't passed a resource after all, but an actual expected value
	    	return expectedResource;
	    }
		BufferedReader in = new BufferedReader(new InputStreamReader(expectedUrl.openStream()));
		StringBuffer expectedOutput = new StringBuffer();
        String line;
        while ((line = in.readLine()) != null) {
            expectedOutput.append(line);
            expectedOutput.append(System.getProperty("line.separator"));
        }
        expectedOutput.setLength(expectedOutput.length()-System.getProperty("line.separator").length());
        in.close();
	    return expectedOutput.toString();
    }
}
