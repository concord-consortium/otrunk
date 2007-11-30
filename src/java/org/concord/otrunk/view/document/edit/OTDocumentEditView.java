/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*
 * Created on Aug 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view.document.edit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.view.OTExternalAppService;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.framework.otrunk.view.OTView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewEntry;
import org.concord.framework.otrunk.view.OTViewEntryAware;
import org.concord.framework.otrunk.view.OTXHTMLView;
import org.concord.otrunk.OTrunkUtil;
import org.concord.otrunk.view.OTObjectEditViewConfig;
import org.concord.otrunk.view.OTObjectListViewer;
import org.concord.otrunk.view.document.DocumentConfig;
import org.concord.otrunk.view.document.OTCompoundDoc;
import org.concord.otrunk.view.document.OTCssText;
import org.concord.otrunk.view.document.OTDocument;
import org.concord.otrunk.view.document.OTDocumentEditorKit;
import org.concord.otrunk.view.document.OTDocumentView;
import org.concord.otrunk.view.document.OTDocumentViewConfig;
import org.concord.otrunk.view.document.OTHTMLFactory;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import com.hexidec.ekit.action.CustomAction;
import com.hexidec.ekit.action.FormatAction;
import com.hexidec.ekit.action.SetFontFamilyAction;
import com.hexidec.ekit.action.StylesAction;
import com.hexidec.ekit.component.ExtendedHTMLDocument;
import com.hexidec.ekit.component.JButtonNoFocus;
import com.hexidec.ekit.component.JComboBoxNoFocus;
import com.hexidec.ekit.component.JToggleButtonNoFocus;
import com.hexidec.ekit.component.SimpleInfoDialog;
import com.hexidec.ekit.component.UnicodeDialog;
import com.hexidec.ekit.thirdparty.print.DocumentRenderer;
import com.hexidec.util.Translatrix;


/**
 * @author sfentress
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class OTDocumentEditView extends OTDocumentView implements
		ChangeListener, HyperlinkListener, OTXHTMLView, OTViewEntryAware, ActionListener {
	
	boolean showMenuIcons = true;
	
	DocumentEditConfig documentEditConfig;

	public JComponent getComponent(OTObject otObject) {
		this.otObject = otObject;
		setup(otObject);
		initTextAreaModel();
		
		if (documentConfig instanceof OTDocumentEditViewConfig){
			documentEditConfig = (DocumentEditConfig) documentConfig;
		}
		
		if (tabbedPane != null) {
			tabbedPane.removeChangeListener(this);
		}

		// need to use the PfCDEditorKit
		updateFormatedView();

		setReloadOnViewEntryChange(true);
		
		if (documentConfig != null){
			OTViewContainer thisViewContainer = getViewContainer();
			if (thisViewContainer != null){
				getViewContainer().setUpdateable(documentConfig.getViewContainerIsUpdateable());
			}
		}

		// JScrollPane renderedScrollPane = new JScrollPane(previewComponent);
		// renderedScrollPane.getViewport().setViewPosition(new Point(0,0));

	//	JPanel wrapper = new JPanel(new BorderLayout());
	//	wrapper.add(previewComponent, BorderLayout.CENTER);
		
		if (otObject instanceof OTCompoundDoc && ((OTCompoundDoc)otObject).getShowEditBar()){
			JPanel wrapper = new JPanel(new BorderLayout());
			JScrollPane pageScroller = new JScrollPane(previewComponent);
			pageScroller.setPreferredSize(new Dimension(350,100));
			((JEditorPane)previewComponent).setCaretPosition(0);
			wrapper.add(pageScroller, BorderLayout.CENTER);
			JPanel leftJustify = new JPanel(new FlowLayout(FlowLayout.LEADING));
			
			EditBar editBar = new EditBar();
			leftJustify.add(editBar);
			
			JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
			JButton addObjectButton = new JButton("Insert Object");
			buttonsPanel.add(addObjectButton);
			addObjectButton.setActionCommand("insertObject");
			addObjectButton.addActionListener(this);
			
			wrapper.add(buttonsPanel, BorderLayout.SOUTH);
			
			
			wrapper.add(leftJustify, BorderLayout.NORTH);
			return wrapper;
		}
	//	final JScrollPane pageScroller = new JScrollPane();
	//	pageScroller.setViewportView(previewComponent);
		return previewComponent;
	}

	

	public String updateFormatedView() {
		if (pfObject == null)
			return null;

		// System.out.println(this+" updateFormatedView");

		String markupLanguage = pfObject.getMarkupLanguage();
		if (markupLanguage == null) {
			markupLanguage = System.getProperty("org.concord.portfolio.markup",
					null);
		}

		String bodyText = pfObject.getDocumentText();
		bodyText = substituteIncludables(bodyText);

		// default to html viewer for now
		// FIXME the handling of the plain markup is to test the new view entry
		// code
		// it isn't quite valid because plain text might have html chars in it
		// so it will get parsed incorrectly.
		if (markupLanguage == null
				|| markupLanguage.equals(OTDocument.MARKUP_PFHTML)
				|| markupLanguage.equals(OTDocument.MARKUP_PLAIN)) {
			if (editorPane == null) {
				editorPane = new JEditorPane();
				OTHTMLFactory kitViewFactory = new OTHTMLFactory(this);
				OTDocumentEditorKit editorKit = new OTDocumentEditorKit(
						kitViewFactory);
				editorPane.setEditorKit(editorKit);
				editorPane.setEditable(true);
				editorPane.addHyperlinkListener(this);
			//	editorPane.setContentType("text/plain") ;
			}
			bodyText = htmlizeText(bodyText);

			if (documentConfig != null) {
				
				String css = getCssText();
				
				String XHTML_PREFIX = XHTML_PREFIX_START + css
						+ XHTML_PREFIX_END;
				bodyText = XHTML_PREFIX + bodyText + XHTML_SUFFIX;
			}
			// when this text is set it will recreate all the
			// OTDocumentObjectViews, so we need to clear and
			// close all the old panels first
			removeAllSubViews();

			editorPane.setText(bodyText);

			previewComponent = editorPane;

			// we used to set thie caret pos so the view would
			// scroll to the top, but now we disable scrolling
			// during the load, and that seems to work better
			// there is no flicker that way.
			// editorPane.setCaretPosition(0);
		} else {
			System.err.println("xhtml markup not supported");
		}

		if (parsedTextArea == null) {
			parsedTextArea = new JTextArea();
		}
		parsedTextArea.setText(bodyText);
		
		return bodyText;
	}
	
	
	
	private OTObject getObjectToInsertFromUser()
	{
		OTObject otObj = null;
		
		System.out.println(documentEditConfig);
		otObj = OTObjectListViewer.showDialog(previewComponent,
		        "Choose object to add", getFrameManager(), getViewFactory(),
		        documentEditConfig.getObjectsToInsert(), otObject
		                .getOTObjectService(), true, true);

		return otObj;
	}
	
	public void setViewEntry(OTViewEntry viewEntry) {
		super.setViewEntry(viewEntry);
		this.viewEntry = viewEntry;
		if (viewEntry instanceof OTDocumentEditViewConfig) {
			documentConfig = new DocumentEditConfig((OTDocumentEditViewConfig)viewEntry);
			documentEditConfig = (DocumentEditConfig) documentConfig;
			setViewMode(documentConfig.getMode());
		}
	}



	public void actionPerformed(ActionEvent e)
    {
		if (e.getActionCommand().equals("insertObject")) {

			OTObject objToInsert = getObjectToInsertFromUser();

			if (objToInsert == null) {
				// No object to insert. Either we couldn't find one, or the user
				// changed his mind
				return;
			}

			OTObjectService objectService = otObject.getOTObjectService();
			String strObjID = objectService.getExternalID(objToInsert);

			String paragraphBreak = "<p/>";
			String strObjText = "<object refid=\"" + strObjID + "\" />"
			        + paragraphBreak;

			int pos = editorPane.getSelectionStart();
			StringBuffer bodyText = new StringBuffer(pfObject.getDocumentText());
			System.out.println(bodyText.toString());
			bodyText.insert(pos, strObjText);
		//	bodyText.setCharAt(1, '.');
			System.out.println("pso = "+pos+" obj = "+strObjText);
			System.out.println(bodyText.toString());
			pfObject.setDocumentText(bodyText.toString());
			
            updateFormatedView();

		}
    }
}
