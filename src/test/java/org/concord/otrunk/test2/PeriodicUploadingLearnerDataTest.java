package org.concord.otrunk.test2;

import static org.fest.assertions.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectMap;
import org.concord.framework.otrunk.OTResourceList;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.otrunk.overlay.CompositeDatabase;
import org.concord.otrunk.overlay.RotatingReferenceMapDatabase;
import org.concord.otrunk.test.OTBasicTestObject;
import org.concord.otrunk.test.OTListTestObject;
import org.concord.otrunk.test.OTMapTestObject;
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
	
	@Test
	@Ignore
	public void testOnlyChangedObjectsGetWritten() throws Exception {
		// TODO
		assertThat(true).isFalse();
	}
	
	private OTReferenceMap rotate() throws Exception {
		CompositeDatabase referenceMapDb = helper.getReferenceMapDb();
		if (referenceMapDb instanceof RotatingReferenceMapDatabase) {
			return helper.getOTrunk().rotateUserDatabase((RotatingReferenceMapDatabase) referenceMapDb);
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
		URL expectedUrl = PeriodicUploadingLearnerDataTest.class.getResource(expectedResource);
		BufferedReader in = new BufferedReader(new InputStreamReader(expectedUrl.openStream()));
		StringBuffer expectedOutput = new StringBuffer();
        String line;
        while ((line = in.readLine()) != null) {
            expectedOutput.append(line);
            expectedOutput.append(System.getProperty("line.separator"));
        }
        expectedOutput.setLength(expectedOutput.length()-System.getProperty("line.separator").length());
        in.close();

        assertThat(actualOutput).as("OTML export").contains(expectedOutput.toString());
	}
}
