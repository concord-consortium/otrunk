package org.concord.otrunk;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTUser;
import org.concord.framework.otrunk.OTrunk;
import org.concord.framework.otrunk.view.OTJComponentService;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTMainComponentProvider;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTXHTMLView;
import org.concord.otrunk.util.ComponentImageSaver;
import org.concord.otrunk.view.AbstractOTJComponentContainerView;
import org.concord.otrunk.view.OTViewContainerPanel;

public class PageXHTMLExporter implements Runnable
{
	private OTObject rootObject;
//	private OTViewContainerPanel viewContainerPanel;
	private OTJComponentService jComponentService;
//	private OTViewFactory viewFactory;
	private OTViewContainer viewContainer;
	private OTJComponentView rootObjectView;
	private Component rootObjectComponent;
	private File outputFile;
	
	public PageXHTMLExporter(OTViewContainerPanel viewPanel) {
		this.rootObject = viewPanel.getCurrentObject();
		this.rootObjectView = viewPanel.getView();
		this.rootObjectComponent = getRootObjectComponent(viewPanel);
		if (rootObjectView instanceof AbstractOTJComponentContainerView) {
			this.jComponentService = ((AbstractOTJComponentContainerView)rootObjectView).getJComponentService();
		}

		this.viewContainer = viewPanel.getViewContainer();
	}
	
	private Component getRootObjectComponent(OTViewContainerPanel viewPanel) {
		LayoutManager layout = viewPanel.getLayout();
		if (! (layout instanceof BorderLayout)) {
			return viewPanel;
		}
		Component comp = ((BorderLayout)layout).getLayoutComponent(BorderLayout.CENTER);
		if (comp instanceof JScrollPane) {
			comp = ((JScrollPane)comp).getViewport().getView();
		}
		return comp;
	}
	
	public void run()
	{
		if (outputFile == null || rootObject == null) {
			return;
		}
		
		String allTexts = "";
			String text = null;

			OTJComponentView objView = getOTJComponentView(rootObject, null);

			OTXHTMLView xhtmlView = null;
			String bodyText = "";
			if (objView instanceof OTXHTMLView) {
				xhtmlView = (OTXHTMLView) objView;
				bodyText = xhtmlView.getXHTMLText(rootObject);

				Pattern p = Pattern.compile("<object(?:[^>]*)refid=\"([^\"]*)\"(?:[^>]*)>");
				Matcher m = p.matcher(bodyText);
				StringBuffer parsed = new StringBuffer();
				OTObjectService objectService = rootObject.getOTObjectService();
				while (m.find()) {
					String id = m.group(1);
					OTID otid = objectService.getOTID(id);
					OTObject referencedObject = null;
					try {
						referencedObject = objectService.getOTObject(otid);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 

					Pattern userPat = Pattern.compile("user=\"([^\"]*)\"");
					Matcher userMatcher = userPat.matcher(m.group(0));
					if(userMatcher.find()){
						String userId = userMatcher.group(1);
						referencedObject = getRuntimeObject(referencedObject, userId);
					}

					String url = Matcher.quoteReplacement(embedOTObject(referencedObject));
					if (url != null) {
						try {
							m.appendReplacement(parsed, url);
						} catch (IllegalArgumentException e) {
							System.err.println("bad replacement: " + url);
							e.printStackTrace();
						} catch (IndexOutOfBoundsException e) {
							System.err.println("bad replacement (non-matching group reference): " + url);
							e.printStackTrace();
						}
					}
				}
				m.appendTail(parsed);
				text = "<div>" + parsed.toString() + "</div><hr/>";
			} else {
				text = "<img src='" + embedComponent(rootObjectComponent, 1, 1, rootObject) + "' />";
			}

			allTexts = allTexts + text;

		try {
			FileWriter fos = new FileWriter(outputFile);
			fos.write("<html><body>" + allTexts + "</body></html>");
			fos.close();
		} catch (FileNotFoundException exp) {
			exp.printStackTrace();
		} catch (IOException exp) {
			exp.printStackTrace();
		}
	}
	
	/*
	 * Modified methods from OTMLToXHTMLExporter below here....
	 */
	
	public void setXHTMLParams(File file)
	{
		outputFile = file;
	}
	
	/// this was simplified...
	private OTJComponentView getOTJComponentView(OTObject obj, String mode) {
		OTJComponentView view = null;
		if (jComponentService.getJComponentViewContext() != null) {
			view = jComponentService.getJComponentViewContext().getViewByObject(obj);
		}
		if (view != null) {
			return view;
		}
		return jComponentService.getObjectView(obj, viewContainer, mode);
	}
	
	public OTObject getRuntimeObject(OTObject object, String userStr) {
		try {
			OTObjectService objectService = object.getOTObjectService();
			OTrunk otrunk = objectService.getOTrunkService(OTrunk.class);
			OTID userId = objectService.getOTID(userStr);
			OTUser user = (OTUser) objectService.getOTObject(userId);
			return otrunk.getUserRuntimeObject(object, user);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// this was modified to remove the OTViewEntry argument...
	public String embedOTObject(OTObject obj)
	{
		OTJComponentView view = getOTJComponentView(obj, null);
		Component comp;
		if (view instanceof OTMainComponentProvider) {
			comp = ((OTMainComponentProvider)view).getMainComponent();
		} else {
			comp = jComponentService.getJComponentViewContext().getComponentByObject(obj);
		}
		if (comp == null) {
			comp = jComponentService.getComponent(obj, view);
		}

		String url = embedComponent(comp, 1, 1, obj);
		url = "<img src='" + url + "' />";

		return url;
	}
	
	/**
	 * This code attempts to save an image of the component.
	 * 
	 */
	public String embedComponent(Component comp, float scaleX, float scaleY,
	    OTObject otObject)
	{
		Dimension compSize = comp.getSize();

		if (compSize.height <= 0 || compSize.width <= 0) {
			throw new RuntimeException("Component size width: "
			        + compSize.width + " height: " + compSize.height
			        + " cannot be <=0");
		}

		String outputFileNameWithoutExtension = outputFile.getName().substring(
		        0, outputFile.getName().lastIndexOf('.'));
		File folder = new File(outputFile.getParent(),
		        outputFileNameWithoutExtension + "_files");
		if (!folder.exists())
			folder.mkdir();
		if (!folder.isDirectory())
			return null;

		ComponentImageSaver saver = new ComponentImageSaver(comp, folder, folder.getName(),
		        otObject, scaleX, scaleY, false);
		
		try {
			SwingUtilities.invokeAndWait(saver);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return saver.getText();
	}
}
