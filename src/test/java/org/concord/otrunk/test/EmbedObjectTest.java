package org.concord.otrunk.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.otrunk.datamodel.OTDatabase;
import org.concord.otrunk.view.OTViewContainerPanel;
import org.concord.otrunk.view.OTViewerHelper;
import org.concord.otrunk.xml.XMLDatabase;

public class EmbedObjectTest extends JFrame {
	
	private OTViewContainerPanel otContainer;
	private OTViewerHelper viewerHelper;
	private String xmlString;
	private String objID;
	
	public EmbedObjectTest(String xmlString, String objID) {
		super();

		this.objID = objID;
		this.xmlString = xmlString;
		this.viewerHelper = new OTViewerHelper();
		try {
			viewerHelper.loadOTrunk(loadOTDatabase(), this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setupView();
	}
	
	public OTDatabase loadOTDatabase() {
		ByteArrayInputStream bais = new ByteArrayInputStream(xmlString.getBytes());
		XMLDatabase otDB = null;
		try {
			otDB = new XMLDatabase(bais, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return otDB;
	}
	
	public void setupView()
	{
        setTitle("Embed Object Test App");
        
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		addWindowListener( new WindowAdapter() {
		    public void windowClosing(WindowEvent e)
		    {
		    	System.exit(0);		        
		    }			
		});				
	
		try {
			// look up view container with the frame.
			otContainer = viewerHelper.createViewContainerPanel(); 
			otContainer.setPreferredSize(new Dimension(800,600));
	
			getContentPane().setLayout(new BorderLayout());
	
			getContentPane().removeAll();
			
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

	public OTObject getOTObject()
		throws Exception
	{
		OTObject root = viewerHelper.getRootObject();
		
		OTObject otObject = root;
		if(objID != null && objID.length() > 0){
			OTID id = getID(objID);
	
			otObject = ((DefaultOTObject)root).getReferencedObject(id);
		}
	
		return otObject;
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
										" objectClass=\"org.concord.otrunk.ui.OTImage\"" +
										" viewClass=\"org.concord.otrunk.ui.swing.OTImageView\">" +
									"</OTViewEntry>" +
									"<OTViewEntry" +
										" objectClass=\"org.concord.otrunk.view.document.OTTextObject\"" +
										" viewClass=\"org.concord.otrunk.view.document.OTTextObjectView\">" +
									"</OTViewEntry>" +
								"</viewEntries>" +
							"</OTViewService>" +
						"</services>" +
						"<root>" +
							"<OTFolderObject name=\"OTNotebook Test\">" +
								"<children>" +
									"<OTUnitValue local_id=\"unitvalue1\" name=\"UnitValue Test\" value=\"20.653\" unit=\"NONY\"/>" +
									"<OTNotebookMeasurement name=\"Measurement test\">" +
										"<notes>" +
											"<OTText local_id=\"textbox1\">" +
												"<text>My text</text>" +
											"</OTText>" +
										"</notes>" +
										"<unitValue>" +
											"<OTUnitValue refid=\"${unitvalue1}\"/>" +
										"</unitValue>" +
										"<image>" +
											"<OTImage local_id=\"image1\"" +
												" imageBytes=\"http://www.concord.org/images/logos/cc/cc-logo.gif\"/>" +
										"</image>" +
									"</OTNotebookMeasurement>" +
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
													"<OTImage" +
														" imageBytes=\"http://www.concord.org/images/logos/cc/cc-logo.gif\"/>" +
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
													"<OTImage " +
														" imageBytes=\"http://www.concord.org/images/logos/cc/cc-logo.gif\"/>" +
												"</image>" +
											"</OTNotebookMeasurement>" +
										"</entries>" +
									"</OTNotebook>" +
								"</children>" +
							"</OTFolderObject>" +
						"</root>" +
					"</OTSystem>" +
				"</objects>" +
			"</otrunk>";
		EmbedObjectTest test = new EmbedObjectTest(xmlString, "notebook1");
		test.pack();
		test.show();
	}
}
