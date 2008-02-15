/*
 * Last modification information:
 * $Revision: 1.0 $
 * $Date:  $
 * $Author:  $
 *
 * Licence Information
 * Copyright 2007 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.io.File;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTViewFactory;
import org.concord.otrunk.OTMLToXHTMLConverter;

/**
 * OTXHTMLViewFileSaver
 * Class name and description
 *
 * Date created: Jan 14, 2008
 *
 * @author Ingrid Moncada<p>
 *
 */
public class OTXHTMLViewFileSaver
{
	protected OTViewer otViewer;
	OTViewerHelper viewerHelper;
	
	protected OTObject rootObject;
	protected org.concord.framework.otrunk.view.OTViewEntry rootViewEntry;
	protected OTViewFactory viewFactory;

	/**
	 * @param args
	 * Arguments are:
	 */
	public static void main(String[] args)
	{
		OTXHTMLViewFileSaver viewSaver = new OTXHTMLViewFileSaver();
		viewSaver.init(args);

	}
	
	public void init(String[] args)
	{
		viewerHelper = new OTViewerHelper();
				
		try{
			viewerHelper.init(args);
			
			initFields();
			
			OTMLToXHTMLConverter conv = new OTMLToXHTMLConverter(viewFactory, rootObject, 
				rootViewEntry,
				OTConfig.getSystemPropertyViewMode());

			String outputFolderStr = System.getProperty("otrunk.html_output.folder");
			if(outputFolderStr == null){
				outputFolderStr = System.getProperty("user.home");
			}
			File outputFolder = new File(outputFolderStr);
			outputFolder.mkdirs();
			File fileToSave = new File(outputFolder, "index.html");
			
			conv.setXHTMLParams(fileToSave, 800, 600);

			(new Thread(conv)).start();
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Copied from OTViewContainerPanel.setCurrentObject()
	 * 
     * @return
     */
    private void initFields()
    	throws Exception
    {
    	rootObject = viewerHelper.getRootObject();
    	
    	if(rootObject instanceof OTViewChild){
    		rootViewEntry = ((OTViewChild)rootObject).getViewid();
    		rootObject = ((OTViewChild)rootObject).getObject();
    	}
    	viewFactory = viewerHelper.getViewFactory();
    }
}
