package org.concord.otrunk.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.otrunk.view.OTViewContainerPanel;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

public class EmbedObjectTest extends JFrame {
	
	private OTViewContainerPanel otContainer;
	private OTViewerHelper viewerHelper;
	
	public EmbedObjectTest(String xmlString) {
		super();

		this.viewerHelper = new OTViewerHelper();
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(xmlString.getBytes());
			XMLDatabase otDatabase = new XMLDatabase(bais, null, null);
			viewerHelper.loadOTrunk(otDatabase, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setupFrame();
		
		try {
			// look up view container with the frame.
			otContainer = viewerHelper.createViewContainerPanel(); 
			otContainer.setPreferredSize(new Dimension(800,600));

			// add the ot pane to the content pane
			getContentPane().add(otContainer, BorderLayout.CENTER);
	
			// call setCurrentObject on that view container with a null
			// frame
			OTObject otObject = getOTObject();

			otContainer.setCurrentObject(otObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public void setupFrame()
	{
		setTitle("Embed Object Test App");        
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);		
		addWindowListener( new WindowAdapter() {
		    public void windowClosing(WindowEvent e)
		    {
		    	System.exit(0);		        
		    }			
		});				
	
		// setup the contentpane of the frame
		getContentPane().setLayout(new BorderLayout());	
		getContentPane().removeAll();			
	}
	
	public OTObject getOTObject()
		throws Exception
	{
		OTObject root = viewerHelper.getRootObject();
		
		return root;
		/*
		OTObject otObject = root;
		if(objID != null && objID.length() > 0){
			OTID id = getID(objID);
	
			otObject = ((DefaultOTObject)root).getReferencedObject(id);
		}
	
		return otObject;
		*/
	}
	
	public OTID getID(String id)
	{
		return ((XMLDatabase)viewerHelper.getOtDB()).getOTIDFromLocalID(id);
	}

	public static void main(String[] args) {
		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<otrunk id=\"33754150-b594-11d9-9669-0800200c9a66\" >" +
				"<imports>" +
					"<import class=\"org.concord.otrunk.OTSystem\"/>" +
					"<import class=\"org.concord.otrunk.ui.notebook.OTNotebookMeasurement\"/>" +
					"<import class=\"org.concord.otrunk.ui.notebook.OTNotebook\"/>" +
					"<import class=\"org.concord.otrunk.ui.notebook.OTNotebookTester\"/>" +
					"<import class=\"org.concord.otrunk.view.OTFolderObject\"/>" +
					"<import class=\"org.concord.otrunk.view.document.OTCompoundDoc\"/>" +
					"<import class=\"org.concord.otrunk.view.OTViewEntry\"/>" +
					"<import class=\"org.concord.otrunk.ui.OTImage\"/>" +
					"<import class=\"org.concord.otrunk.ui.OTImageViewConfig\"/>" +
					"<import class=\"org.concord.otrunk.view.OTViewService\"/>" +
					"<import class=\"org.concord.framework.otrunk.view.OTFrame\"/>" +
					"<import class=\"org.concord.data.state.OTUnitValue\"/>" +
					"<import class=\"org.concord.data.state.OTUnitValueViewConfig\"/>" +
					"<import class=\"org.concord.otrunk.ui.OTText\"/>" +
				"</imports>" +
				"<objects>" +
					"<OTSystem>" +
						"<services>" +
							"<OTViewService>" +
								"<viewEntries>" +
									"<OTUnitValueViewConfig" +
										" objectClass=\"org.concord.data.state.OTUnitValue\"" +
										" viewClass=\"org.concord.data.state.OTUnitValueView\">" +
										" <precision>1</precision>" +
									"</OTUnitValueViewConfig>" +
									"<OTImageViewConfig" +
										" objectClass=\"org.concord.otrunk.ui.OTImage\"" +
										" viewClass=\"org.concord.otrunk.ui.swing.OTImageView\">" +
										" <preferredWidth>50</preferredWidth>" +
										" <preferredHeight>50</preferredHeight>" +
										" <zoomEnabled>true</zoomEnabled>" +
									"</OTImageViewConfig>" +
									"<OTViewEntry " +
										" objectClass=\"org.concord.otrunk.ui.notebook.OTNotebook\"" +
										" viewClass=\"org.concord.otrunk.ui.notebook.OTNotebookView\">" +
									"</OTViewEntry>" +
									"<OTViewEntry" +
										" objectClass=\"org.concord.otrunk.ui.OTText\"" +
										" viewClass=\"org.concord.otrunk.ui.swing.OTTextEditView\">" +
									"</OTViewEntry>" +
									"<OTViewEntry" +
										" objectClass=\"org.concord.otrunk.ui.notebook.OTNotebookTester\"" +
										" viewClass=\"org.concord.otrunk.ui.notebook.OTNotebookTesterView\">" +
									"</OTViewEntry>" +
									"<OTViewEntry" +
										" objectClass=\"org.concord.otrunk.view.document.OTCompoundDoc\"" +
										" viewClass=\"org.concord.otrunk.view.document.OTDocumentView\">" +
									"</OTViewEntry>" +
									"<OTViewEntry" +
										" objectClass=\"org.concord.otrunk.ui.notebook.OTNotebookMeasurement\"" +
										" viewClass=\"org.concord.otrunk.ui.notebook.OTNotebookMeasurementView\">" +
									"</OTViewEntry>" +
									"<OTViewEntry" +
										" objectClass=\"org.concord.otrunk.view.document.OTTextObject\"" +
										" viewClass=\"org.concord.otrunk.view.document.OTTextObjectView\">" +
									"</OTViewEntry>" +
								"</viewEntries>" +
							"</OTViewService>" +
						"</services>" +
						"<root>" +
							"<OTNotebook name=\"Notebook test\" local_id=\"notebook1\">" +
							"<entries>" +
								"<OTNotebookMeasurement>" +
									"<notes>" +
										"<OTText>" +
											"<text>My text</text>" +
										"</OTText>" +
									"</notes>" +
									"<unitValue>" +
										"<OTUnitValue value=\"26.653\" unit=\"cm\"/>" +
									"</unitValue>" +
									"<image>" +
										"<OTImage imageBytes=\"http://www.concord.org/images/logos/cc/cc-logo.gif\"/>" +
									"</image>" +
								"</OTNotebookMeasurement>" +
								"<OTNotebookMeasurement>" +
									"<notes>" +
										"<OTText>" +
											"<text>My text</text>" +
										"</OTText>" +
									"</notes>" +
									"<unitValue>" +
										"<OTUnitValue value=\"39.657\" unit=\"cm\"/>" +
									"</unitValue>" +
									"<image>" +
										"<OTImage imageBytes=\"http://www.concord.org/images/logos/cc/cc-logo.gif\"/>" +
									"</image>" +
								"</OTNotebookMeasurement>" +
							"</entries>" +
						"</OTNotebook>" +
					"</root>" +
				"</OTSystem>" +
			"</objects>" +
		"</otrunk>";
		EmbedObjectTest test = new EmbedObjectTest(xmlString);
		test.pack();
		test.show();
	}
}
