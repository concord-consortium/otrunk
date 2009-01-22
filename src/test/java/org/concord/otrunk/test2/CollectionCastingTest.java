package org.concord.otrunk.test2;


import java.net.URL;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.view.document.OTCompoundDoc;


public class CollectionCastingTest extends TestCase
{
    /**
     * Test creating an ArrayList from an OTObjectList.
     * Used to throw an IllegalArgumentException from the constructor of ArrayList.
     */
    public void testArrayListFromOtObjectList() throws Exception {
        URL url = getClass().getResource("/collection-test.otml");
        OTViewerHelper viewerHelper = new OTViewerHelper();
        OTDatabase mainDb = viewerHelper.loadOTDatabase(url);
        viewerHelper.loadOTrunk(mainDb, null);

        OTCompoundDoc doc = (OTCompoundDoc) viewerHelper.getRootObject();
        OTObjectList objectList = doc.getDocumentRefsAsObjectList();

        ArrayList<OTObject> aList = new ArrayList<OTObject>(objectList);
    }
}
