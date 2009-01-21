package org.concord.otrunk.view.document.edit;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import com.hexidec.ekit.Ekit;
import com.hexidec.ekit.component.JButtonNoFocus;
import com.hexidec.ekit.component.JComboBoxNoFocus;
import com.hexidec.ekit.component.JToggleButtonNoFocus;
import com.hexidec.util.Translatrix;

public class EditBar extends JToolBar implements ActionListener
{
	StyledEditorKit.BoldAction actionFontBold = new StyledEditorKit.BoldAction();
	
	StyledEditorKit.ItalicAction actionFontItalic = new StyledEditorKit.ItalicAction();
	StyledEditorKit.UnderlineAction actionFontUnderline   = new StyledEditorKit.UnderlineAction();
// FormatAction actionFontStrike = new FormatAction(this,
// Translatrix.getTranslationString("FontStrike"), HTML.Tag.STRIKE);
// FormatAction actionFontSuperscript = new FormatAction(this,
// Translatrix.getTranslationString("FontSuperscript"), HTML.Tag.SUP);
// FormatAction actionFontSubscript = new FormatAction(this,
// Translatrix.getTranslationString("FontSubscript"), HTML.Tag.SUB);
// ListAutomationAction actionListUnordered = new ListAutomationAction(this,
// Translatrix.getTranslationString("ListUnordered"), HTML.Tag.UL);
// ListAutomationAction actionListOrdered = new ListAutomationAction(this,
// Translatrix.getTranslationString("ListOrdered"), HTML.Tag.OL);
// SetFontFamilyAction actionSelectFont = new SetFontFamilyAction(this,
// "[MENUFONTSELECTOR]");
	StyledEditorKit.AlignmentAction actionAlignLeft       = new StyledEditorKit.AlignmentAction(Translatrix.getTranslationString("AlignLeft"), StyleConstants.ALIGN_LEFT);
	StyledEditorKit.AlignmentAction actionAlignCenter     = new StyledEditorKit.AlignmentAction(Translatrix.getTranslationString("AlignCenter"), StyleConstants.ALIGN_CENTER);
	StyledEditorKit.AlignmentAction actionAlignRight      = new StyledEditorKit.AlignmentAction(Translatrix.getTranslationString("AlignRight"), StyleConstants.ALIGN_RIGHT);
	StyledEditorKit.AlignmentAction actionAlignJustified  = new StyledEditorKit.AlignmentAction(Translatrix.getTranslationString("AlignJustified"), StyleConstants.ALIGN_JUSTIFIED);
// CustomAction actionInsertAnchor = new CustomAction(this,
// Translatrix.getTranslationString("InsertAnchor") + menuDialog, HTML.Tag.A);
	
	private JMenuBar jMenuBar;
	private JMenu jMenuFont;
	
	/* Constants */
	// Menu Keys
	public static final String KEY_MENU_FILE   = "file";
	public static final String KEY_MENU_EDIT   = "edit";
	public static final String KEY_MENU_VIEW   = "view";
	public static final String KEY_MENU_FONT   = "font";
	public static final String KEY_MENU_FORMAT = "format";
	public static final String KEY_MENU_INSERT = "insert";
	public static final String KEY_MENU_TABLE  = "table";
	public static final String KEY_MENU_FORMS  = "forms";
	public static final String KEY_MENU_SEARCH = "search";
	public static final String KEY_MENU_TOOLS  = "tools";
	public static final String KEY_MENU_HELP   = "help";
	public static final String KEY_MENU_DEBUG  = "debug";

	// Tool Keys
	public static final String KEY_TOOL_SEP       = "SP";
	public static final String KEY_TOOL_NEW       = "NW";
	public static final String KEY_TOOL_OPEN      = "OP";
	public static final String KEY_TOOL_SAVE      = "SV";
	public static final String KEY_TOOL_PRINT     = "PR";
	public static final String KEY_TOOL_CUT       = "CT";
	public static final String KEY_TOOL_COPY      = "CP";
	public static final String KEY_TOOL_PASTE     = "PS";
	public static final String KEY_TOOL_UNDO      = "UN";
	public static final String KEY_TOOL_REDO      = "RE";
	public static final String KEY_TOOL_BOLD      = "BL";
	public static final String KEY_TOOL_ITALIC    = "IT";
	public static final String KEY_TOOL_UNDERLINE = "UD";
	public static final String KEY_TOOL_STRIKE    = "SK";
	public static final String KEY_TOOL_SUPER     = "SU";
	public static final String KEY_TOOL_SUB       = "SB";
	public static final String KEY_TOOL_ULIST     = "UL";
	public static final String KEY_TOOL_OLIST     = "OL";
	public static final String KEY_TOOL_ALIGNL    = "AL";
	public static final String KEY_TOOL_ALIGNC    = "AC";
	public static final String KEY_TOOL_ALIGNR    = "AR";
	public static final String KEY_TOOL_ALIGNJ    = "AJ";
	public static final String KEY_TOOL_UNICODE   = "UC";
	public static final String KEY_TOOL_UNIMATH   = "UM";
	public static final String KEY_TOOL_FIND      = "FN";
	public static final String KEY_TOOL_ANCHOR    = "LK";
	public static final String KEY_TOOL_SOURCE    = "SR";
	public static final String KEY_TOOL_STYLES    = "ST";
	public static final String KEY_TOOL_FONTS     = "FO";
	public static final String KEY_TOOL_INSTABLE  = "TI";
	public static final String KEY_TOOL_EDITTABLE = "TE";
	public static final String KEY_TOOL_EDITCELL  = "CE";
	public static final String KEY_TOOL_INSERTROW = "RI";
	public static final String KEY_TOOL_INSERTCOL = "CI";
	public static final String KEY_TOOL_DELETEROW = "RD";
	public static final String KEY_TOOL_DELETECOL = "CD";

	public static final String TOOLBAR_DEFAULT_MULTI  = "NW|OP|SV|PR|SP|CT|CP|PS|SP|UN|RE|SP|FN|SP|UC|UM|SP|SR|*|BL|IT|UD|SP|SK|SU|SB|SP|AL|AC|AR|AJ|SP|UL|OL|SP|LK|*|ST|SP|FO";
	public static final String TOOLBAR_DEFAULT_SINGLE = "BL|IT|UD|SP|SP|CT|CP|PS|SP|UN|RE|SP|SP|FN|SP|UC|SP|LK|SP|SR|SP|PR|";

	public static final int TOOLBAR_SINGLE = 0;
	public static final int TOOLBAR_MAIN   = 1;
	public static final int TOOLBAR_FORMAT = 2;
	public static final int TOOLBAR_STYLES = 3;
	
	private static HashMap<String, JMenu> htMenus = new HashMap<String, JMenu>();
	private static HashMap<String, JComponent> htTools = new HashMap<String, JComponent>();
	
	
	private JButtonNoFocus jbtnBold;
	private JButtonNoFocus jbtnItalic;
	private JButtonNoFocus jbtnUnderline;
	private JButtonNoFocus jbtnAlignLeft;
	private JButtonNoFocus jbtnAlignCenter;
	private JButtonNoFocus jbtnAlignRight;
	private JButtonNoFocus jbtnAlignJustified;
	private JButtonNoFocus jbtnInsertTable;
	private JButtonNoFocus jbtnEditTable;
	private JButtonNoFocus jbtnEditCell;
	private JButtonNoFocus jbtnInsertRow;
	private JButtonNoFocus jbtnInsertColumn;
	private JButtonNoFocus jbtnDeleteRow;
	private JButtonNoFocus jbtnDeleteColumn;
	
	/**
	 * Creates the default, single line toolbar
	 */
	public EditBar(){
		this(TOOLBAR_DEFAULT_SINGLE);
	}

	public EditBar(String toolbarStyle)
    {
		super(JToolBar.HORIZONTAL);
		createMenus();
		setFloatable(false);
		initializeSingleToolbar(toolbarStyle);
		setVisible(true);
    }
	
	private void createMenus()
    {
		/* FONT Menu */
		jMenuFont              = new JMenu(Translatrix.getTranslationString("Font"));
		htMenus.put(KEY_MENU_FONT, jMenuFont);
		JMenuItem jmiBold      = new JMenuItem(actionFontBold);      jmiBold.setText(Translatrix.getTranslationString("FontBold"));           jmiBold.setAccelerator(KeyStroke.getKeyStroke('B', KeyEvent.CTRL_MASK, false));      jmiBold.setIcon(getEkitIcon("Bold"));           jMenuFont.add(jmiBold);
		JMenuItem jmiItalic    = new JMenuItem(actionFontItalic);    jmiItalic.setText(Translatrix.getTranslationString("FontItalic"));       jmiItalic.setAccelerator(KeyStroke.getKeyStroke('I', KeyEvent.CTRL_MASK, false));    jmiItalic.setIcon(getEkitIcon("Italic"));       jMenuFont.add(jmiItalic);
		JMenuItem jmiUnderline = new JMenuItem(actionFontUnderline); jmiUnderline.setText(Translatrix.getTranslationString("FontUnderline")); jmiUnderline.setAccelerator(KeyStroke.getKeyStroke('U', KeyEvent.CTRL_MASK, false)); jmiUnderline.setIcon(getEkitIcon("Underline")); jMenuFont.add(jmiUnderline);
	// JMenuItem jmiStrike = new JMenuItem(actionFontStrike);
	// jmiStrike.setText(Translatrix.getTranslationString("FontStrike"));
	// if(showMenuIcons) { jmiStrike.setIcon(getEkitIcon("Strike")); }
	// jMenuFont.add(jmiStrike);
	// JMenuItem jmiSupscript = new JMenuItem(actionFontSuperscript);
	// if(showMenuIcons) { jmiSupscript.setIcon(getEkitIcon("Super")); }
	// jMenuFont.add(jmiSupscript);
	// JMenuItem jmiSubscript = new JMenuItem(actionFontSubscript);
	// if(showMenuIcons) { jmiSubscript.setIcon(getEkitIcon("Sub")); }
	// jMenuFont.add(jmiSubscript);
		jMenuFont.addSeparator();
	// jMenuFont.add(new JMenuItem(new FormatAction(this,
	// Translatrix.getTranslationString("FormatBig"), HTML.Tag.BIG)));
	// jMenuFont.add(new JMenuItem(new FormatAction(this,
	// Translatrix.getTranslationString("FormatSmall"), HTML.Tag.SMALL)));
		JMenu jMenuFontSize = new JMenu(Translatrix.getTranslationString("FontSize"));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize1"), 8)));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize2"), 10)));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize3"), 12)));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize4"), 14)));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize5"), 18)));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize6"), 24)));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize7"), 32)));
		jMenuFont.add(jMenuFontSize);
		jMenuFont.addSeparator();
		JMenu jMenuFontSub      = new JMenu(Translatrix.getTranslationString("Font"));
	// JMenuItem jmiSelectFont = new JMenuItem(actionSelectFont);
	// jmiSelectFont.setText(Translatrix.getTranslationString("FontSelect") +
	// menuDialog); if(showMenuIcons) {
	// jmiSelectFont.setIcon(getEkitIcon("FontFaces")); }
	// jMenuFontSub.add(jmiSelectFont);
	// JMenuItem jmiSerif = new
	// JMenuItem((Action)actions.get("font-family-Serif"));
	// jmiSerif.setText(Translatrix.getTranslationString("FontSerif"));
	// jMenuFontSub.add(jmiSerif);
	// JMenuItem jmiSansSerif = new
	// JMenuItem((Action)actions.get("font-family-SansSerif"));
	// jmiSansSerif.setText(Translatrix.getTranslationString("FontSansserif"));
	// jMenuFontSub.add(jmiSansSerif);
	// JMenuItem jmiMonospaced = new
	// JMenuItem((Action)actions.get("font-family-Monospaced"));
	// jmiMonospaced.setText(Translatrix.getTranslationString("FontMonospaced"));
	// jMenuFontSub.add(jmiMonospaced);
		jMenuFont.add(jMenuFontSub);
		jMenuFont.addSeparator();
		JMenu jMenuFontColor = new JMenu(Translatrix.getTranslationString("Color"));
// jMenuFontColor.add(new JMenuItem(new CustomAction(this,
// Translatrix.getTranslationString("CustomColor") + menuDialog, HTML.Tag.FONT,
// customAttr)));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorAqua"),    new Color(  0,255,255))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorBlack"),   new Color(  0,  0,  0))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorBlue"),    new Color(  0,  0,255))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorFuschia"), new Color(255,  0,255))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorGray"),    new Color(128,128,128))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorGreen"),   new Color(  0,128,  0))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorLime"),    new Color(  0,255,  0))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorMaroon"),  new Color(128,  0,  0))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorNavy"),    new Color(  0,  0,128))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorOlive"),   new Color(128,128,  0))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorPurple"),  new Color(128,  0,128))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorRed"),     new Color(255,  0,  0))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorSilver"),  new Color(192,192,192))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorTeal"),    new Color(  0,128,128))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorWhite"),   new Color(255,255,255))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorYellow"),  new Color(255,255,  0))));
		jMenuFont.add(jMenuFontColor);

		/* FORMAT Menu */
/*
 * jMenuFormat = new JMenu(Translatrix.getTranslationString("Format"));
 * htMenus.put(KEY_MENU_FORMAT, jMenuFormat); JMenu jMenuFormatAlign = new
 * JMenu(Translatrix.getTranslationString("Align")); JMenuItem jmiAlignLeft =
 * new JMenuItem(actionAlignLeft); if(showMenuIcons) {
 * jmiAlignLeft.setIcon(getEkitIcon("AlignLeft")); };
 * jMenuFormatAlign.add(jmiAlignLeft); JMenuItem jmiAlignCenter = new
 * JMenuItem(actionAlignCenter); if(showMenuIcons) {
 * jmiAlignCenter.setIcon(getEkitIcon("AlignCenter")); };
 * jMenuFormatAlign.add(jmiAlignCenter); JMenuItem jmiAlignRight = new
 * JMenuItem(actionAlignRight); if(showMenuIcons) {
 * jmiAlignRight.setIcon(getEkitIcon("AlignRight")); };
 * jMenuFormatAlign.add(jmiAlignRight); JMenuItem jmiAlignJustified = new
 * JMenuItem(actionAlignJustified); if(showMenuIcons) {
 * jmiAlignJustified.setIcon(getEkitIcon("AlignJustified")); };
 * jMenuFormatAlign.add(jmiAlignJustified); jMenuFormat.add(jMenuFormatAlign);
 * jMenuFormat.addSeparator(); JMenu jMenuFormatHeading = new
 * JMenu(Translatrix.getTranslationString("Heading"));
 * jMenuFormatHeading.add(new JMenuItem(new FormatAction(this,
 * Translatrix.getTranslationString("Heading1"), HTML.Tag.H1)));
 * jMenuFormatHeading.add(new JMenuItem(new FormatAction(this,
 * Translatrix.getTranslationString("Heading2"), HTML.Tag.H2)));
 * jMenuFormatHeading.add(new JMenuItem(new FormatAction(this,
 * Translatrix.getTranslationString("Heading3"), HTML.Tag.H3)));
 * jMenuFormatHeading.add(new JMenuItem(new FormatAction(this,
 * Translatrix.getTranslationString("Heading4"), HTML.Tag.H4)));
 * jMenuFormatHeading.add(new JMenuItem(new FormatAction(this,
 * Translatrix.getTranslationString("Heading5"), HTML.Tag.H5)));
 * jMenuFormatHeading.add(new JMenuItem(new FormatAction(this,
 * Translatrix.getTranslationString("Heading6"), HTML.Tag.H6)));
 * jMenuFormat.add(jMenuFormatHeading); jMenuFormat.addSeparator(); JMenuItem
 * jmiUList = new JMenuItem(actionListUnordered); if(showMenuIcons) {
 * jmiUList.setIcon(getEkitIcon("UList")); } jMenuFormat.add(jmiUList);
 * JMenuItem jmiOList = new JMenuItem(actionListOrdered); if(showMenuIcons) {
 * jmiOList.setIcon(getEkitIcon("OList")); } jMenuFormat.add(jmiOList);
 * jMenuFormat.add(new JMenuItem(new FormatAction(this,
 * Translatrix.getTranslationString("ListItem"), HTML.Tag.LI)));
 * jMenuFormat.addSeparator(); jMenuFormat.add(new JMenuItem(new
 * FormatAction(this, Translatrix.getTranslationString("FormatBlockquote"),
 * HTML.Tag.BLOCKQUOTE))); jMenuFormat.add(new JMenuItem(new FormatAction(this,
 * Translatrix.getTranslationString("FormatPre"), HTML.Tag.PRE)));
 * jMenuFormat.add(new JMenuItem(new FormatAction(this,
 * Translatrix.getTranslationString("FormatStrong"), HTML.Tag.STRONG)));
 * jMenuFormat.add(new JMenuItem(new FormatAction(this,
 * Translatrix.getTranslationString("FormatEmphasis"), HTML.Tag.EM)));
 * jMenuFormat.add(new JMenuItem(new FormatAction(this,
 * Translatrix.getTranslationString("FormatTT"), HTML.Tag.TT)));
 * jMenuFormat.add(new JMenuItem(new FormatAction(this,
 * Translatrix.getTranslationString("FormatSpan"), HTML.Tag.SPAN)));
 */
		jMenuBar = new JMenuBar();
		jMenuBar.add(jMenuFont);
		
		/* Create toolbar tool objects */
	/*
		jbtnPrint = new JButtonNoFocus(getEkitIcon("Print"));
			jbtnPrint.setToolTipText(Translatrix.getTranslationString("PrintDocument"));
			jbtnPrint.setActionCommand("print");
			jbtnPrint.addActionListener(this);
			htTools.put(KEY_TOOL_PRINT, jbtnPrint);
		jbtnCut = new JButtonNoFocus(new DefaultEditorKit.CutAction());
			jbtnCut.setIcon(getEkitIcon("Cut"));
			jbtnCut.setText(null);
			jbtnCut.setToolTipText(Translatrix.getTranslationString("Cut"));
			htTools.put(KEY_TOOL_CUT, jbtnCut);
		jbtnCopy = new JButtonNoFocus(new DefaultEditorKit.CopyAction());
			jbtnCopy.setIcon(getEkitIcon("Copy"));
			jbtnCopy.setText(null);
			jbtnCopy.setToolTipText(Translatrix.getTranslationString("Copy"));
			htTools.put(KEY_TOOL_COPY, jbtnCopy);
		jbtnPaste = new JButtonNoFocus(new DefaultEditorKit.PasteAction());
			jbtnPaste.setIcon(getEkitIcon("Paste"));
			jbtnPaste.setText(null);
			jbtnPaste.setToolTipText(Translatrix.getTranslationString("Paste"));
			htTools.put(KEY_TOOL_PASTE, jbtnPaste);
	*/
	// jbtnUndo = new JButtonNoFocus(undoAction);
	// jbtnUndo.setIcon(getEkitIcon("Undo"));
	// jbtnUndo.setText(null);
	// jbtnUndo.setToolTipText(Translatrix.getTranslationString("Undo"));
	// htTools.put(KEY_TOOL_UNDO, jbtnUndo);
	// jbtnRedo = new JButtonNoFocus(redoAction);
	// jbtnRedo.setIcon(getEkitIcon("Redo"));
	// jbtnRedo.setText(null);
	// jbtnRedo.setToolTipText(Translatrix.getTranslationString("Redo"));
	// htTools.put(KEY_TOOL_REDO, jbtnRedo);
		jbtnBold = new JButtonNoFocus(actionFontBold);
			jbtnBold.setIcon(getEkitIcon("Bold"));
			jbtnBold.setText(null);
			jbtnBold.setToolTipText(Translatrix.getTranslationString("FontBold"));
			htTools.put(KEY_TOOL_BOLD, jbtnBold);
		jbtnItalic = new JButtonNoFocus(actionFontItalic);
			jbtnItalic.setIcon(getEkitIcon("Italic"));
			jbtnItalic.setText(null);
			jbtnItalic.setToolTipText(Translatrix.getTranslationString("FontItalic"));
			htTools.put(KEY_TOOL_ITALIC, jbtnItalic);
		jbtnUnderline = new JButtonNoFocus(actionFontUnderline);
			jbtnUnderline.setIcon(getEkitIcon("Underline"));
			jbtnUnderline.setText(null);
			jbtnUnderline.setToolTipText(Translatrix.getTranslationString("FontUnderline"));
			htTools.put(KEY_TOOL_UNDERLINE, jbtnUnderline);
	// jbtnStrike = new JButtonNoFocus(actionFontStrike);
	// jbtnStrike.setIcon(getEkitIcon("Strike"));
	// jbtnStrike.setText(null);
	// jbtnStrike.setToolTipText(Translatrix.getTranslationString("FontStrike"));
	// htTools.put(KEY_TOOL_STRIKE, jbtnStrike);
	// jbtnSuperscript = new JButtonNoFocus(actionFontSuperscript);
	// jbtnSuperscript.setIcon(getEkitIcon("Super"));
	// jbtnSuperscript.setText(null);
	// jbtnSuperscript.setToolTipText(Translatrix.getTranslationString("FontSuperscript"));
	// htTools.put(KEY_TOOL_SUPER, jbtnSuperscript);
	// jbtnSubscript = new JButtonNoFocus(actionFontSubscript);
	// jbtnSubscript.setIcon(getEkitIcon("Sub"));
	// jbtnSubscript.setText(null);
	// jbtnSubscript.setToolTipText(Translatrix.getTranslationString("FontSubscript"));
	// htTools.put(KEY_TOOL_SUB, jbtnSubscript);
	// jbtnUList = new JButtonNoFocus(actionListUnordered);
	// jbtnUList.setIcon(getEkitIcon("UList"));
	// jbtnUList.setText(null);
	// jbtnUList.setToolTipText(Translatrix.getTranslationString("ListUnordered"));
	// htTools.put(KEY_TOOL_ULIST, jbtnUList);
	// jbtnOList = new JButtonNoFocus(actionListOrdered);
	// jbtnOList.setIcon(getEkitIcon("OList"));
	// jbtnOList.setText(null);
	// jbtnOList.setToolTipText(Translatrix.getTranslationString("ListOrdered"));
	// htTools.put(KEY_TOOL_OLIST, jbtnOList);
		jbtnAlignLeft = new JButtonNoFocus(actionAlignLeft);
			jbtnAlignLeft.setIcon(getEkitIcon("AlignLeft"));
			jbtnAlignLeft.setText(null);
			jbtnAlignLeft.setToolTipText(Translatrix.getTranslationString("AlignLeft"));
			htTools.put(KEY_TOOL_ALIGNL, jbtnAlignLeft);
		jbtnAlignCenter = new JButtonNoFocus(actionAlignCenter);
			jbtnAlignCenter.setIcon(getEkitIcon("AlignCenter"));
			jbtnAlignCenter.setText(null);
			jbtnAlignCenter.setToolTipText(Translatrix.getTranslationString("AlignCenter"));
			htTools.put(KEY_TOOL_ALIGNC, jbtnAlignCenter);
		jbtnAlignRight = new JButtonNoFocus(actionAlignRight);
			jbtnAlignRight.setIcon(getEkitIcon("AlignRight"));
			jbtnAlignRight.setText(null);
			jbtnAlignRight.setToolTipText(Translatrix.getTranslationString("AlignRight"));
			htTools.put(KEY_TOOL_ALIGNR, jbtnAlignRight);
		jbtnAlignJustified = new JButtonNoFocus(actionAlignJustified);
			jbtnAlignJustified.setIcon(getEkitIcon("AlignJustified"));
			jbtnAlignJustified.setText(null);
			jbtnAlignJustified.setToolTipText(Translatrix.getTranslationString("AlignJustified"));
			htTools.put(KEY_TOOL_ALIGNJ, jbtnAlignJustified);
			
	/*
		jbtnUnicode = new JButtonNoFocus();
			jbtnUnicode.setActionCommand("insertunicode");
			jbtnUnicode.addActionListener(this);
			jbtnUnicode.setIcon(getEkitIcon("Unicode"));
			jbtnUnicode.setText(null);
			jbtnUnicode.setToolTipText(Translatrix.getTranslationString("ToolUnicode"));
			htTools.put(KEY_TOOL_UNICODE, jbtnUnicode);
		jbtnUnicodeMath = new JButtonNoFocus();
			jbtnUnicodeMath.setActionCommand("insertunicodemath");
			jbtnUnicodeMath.addActionListener(this);
			jbtnUnicodeMath.setIcon(getEkitIcon("Math"));
			jbtnUnicodeMath.setText(null);
			jbtnUnicodeMath.setToolTipText(Translatrix.getTranslationString("ToolUnicodeMath"));
			htTools.put(KEY_TOOL_UNIMATH, jbtnUnicodeMath);
		jbtnFind = new JButtonNoFocus();
			jbtnFind.setActionCommand("find");
			jbtnFind.addActionListener(this);
			jbtnFind.setIcon(getEkitIcon("Find"));
			jbtnFind.setText(null);
			jbtnFind.setToolTipText(Translatrix.getTranslationString("SearchFind"));
			htTools.put(KEY_TOOL_FIND, jbtnFind);
	*/
	// jbtnAnchor = new JButtonNoFocus(actionInsertAnchor);
	// jbtnAnchor.setIcon(getEkitIcon("Anchor"));
	// jbtnAnchor.setText(null);
	// jbtnAnchor.setToolTipText(Translatrix.getTranslationString("ToolAnchor"));
	// htTools.put(KEY_TOOL_ANCHOR, jbtnAnchor);
	// jtbtnViewSource = new JToggleButtonNoFocus(getEkitIcon("Source"));
	// jtbtnViewSource.setText(null);
	// jtbtnViewSource.setToolTipText(Translatrix.getTranslationString("ViewSource"));
	// jtbtnViewSource.setActionCommand("viewsource");
	// jtbtnViewSource.addActionListener(this);
	// jtbtnViewSource.setPreferredSize(jbtnAnchor.getPreferredSize());
	// jtbtnViewSource.setMinimumSize(jbtnAnchor.getMinimumSize());
	// jtbtnViewSource.setMaximumSize(jbtnAnchor.getMaximumSize());
	// htTools.put(KEY_TOOL_SOURCE, jtbtnViewSource);
// jcmbStyleSelector = new JComboBoxNoFocus();
// jcmbStyleSelector.setToolTipText(Translatrix.getTranslationString("PickCSSStyle"));
// jcmbStyleSelector.setAction(new StylesAction(jcmbStyleSelector));
// htTools.put(KEY_TOOL_STYLES, jcmbStyleSelector);
	/*
	 * String[] fonts =
	 * java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	 * Vector vcFontnames = new Vector(fonts.length + 1);
	 * vcFontnames.add(Translatrix.getTranslationString("SelectorToolFontsDefaultFont"));
	 * for(int i = 0; i < fonts.length; i++) { vcFontnames.add(fonts[i]); }
	 * Collections.sort(vcFontnames); jcmbFontSelector = new
	 * JComboBoxNoFocus(vcFontnames); jcmbFontSelector.setAction(new
	 * SetFontFamilyAction(this, "[EKITFONTSELECTOR]"));
	 * htTools.put(KEY_TOOL_FONTS, jcmbFontSelector);
	 */	jbtnInsertTable = new JButtonNoFocus();
			jbtnInsertTable.setActionCommand("inserttable");
			jbtnInsertTable.addActionListener(this);
			jbtnInsertTable.setIcon(getEkitIcon("TableCreate"));
			jbtnInsertTable.setText(null);
			jbtnInsertTable.setToolTipText(Translatrix.getTranslationString("InsertTable"));
			htTools.put(KEY_TOOL_INSTABLE, jbtnInsertTable);
		jbtnEditTable = new JButtonNoFocus();
			jbtnEditTable.setActionCommand("edittable");
			jbtnEditTable.addActionListener(this);
			jbtnEditTable.setIcon(getEkitIcon("TableEdit"));
			jbtnEditTable.setText(null);
			jbtnEditTable.setToolTipText(Translatrix.getTranslationString("TableEdit"));
			htTools.put(KEY_TOOL_EDITTABLE, jbtnEditTable);
		jbtnEditCell = new JButtonNoFocus();
			jbtnEditCell.setActionCommand("editcell");
			jbtnEditCell.addActionListener(this);
			jbtnEditCell.setIcon(getEkitIcon("CellEdit"));
			jbtnEditCell.setText(null);
			jbtnEditCell.setToolTipText(Translatrix.getTranslationString("TableCellEdit"));
			htTools.put(KEY_TOOL_EDITCELL, jbtnEditCell);			
		jbtnInsertRow = new JButtonNoFocus();
			jbtnInsertRow.setActionCommand("inserttablerow");
			jbtnInsertRow.addActionListener(this);
			jbtnInsertRow.setIcon(getEkitIcon("InsertRow"));
			jbtnInsertRow.setText(null);
			jbtnInsertRow.setToolTipText(Translatrix.getTranslationString("InsertTableRow"));
			htTools.put(KEY_TOOL_INSERTROW, jbtnInsertRow);
		jbtnInsertColumn = new JButtonNoFocus();
			jbtnInsertColumn.setActionCommand("inserttablecolumn");
			jbtnInsertColumn.addActionListener(this);
			jbtnInsertColumn.setIcon(getEkitIcon("InsertColumn"));
			jbtnInsertColumn.setText(null);
			jbtnInsertColumn.setToolTipText(Translatrix.getTranslationString("InsertTableColumn"));
			htTools.put(KEY_TOOL_INSERTCOL, jbtnInsertColumn);
		jbtnDeleteRow = new JButtonNoFocus();
			jbtnDeleteRow.setActionCommand("deletetablerow");
			jbtnDeleteRow.addActionListener(this);
			jbtnDeleteRow.setIcon(getEkitIcon("DeleteRow"));
			jbtnDeleteRow.setText(null);
			jbtnDeleteRow.setToolTipText(Translatrix.getTranslationString("DeleteTableRow"));
			htTools.put(KEY_TOOL_DELETEROW, jbtnDeleteRow);
		jbtnDeleteColumn = new JButtonNoFocus();
			jbtnDeleteColumn.setActionCommand("deletetablecolumn");
			jbtnDeleteColumn.addActionListener(this);
			jbtnDeleteColumn.setIcon(getEkitIcon("DeleteColumn"));
			jbtnDeleteColumn.setText(null);
			jbtnDeleteColumn.setToolTipText(Translatrix.getTranslationString("DeleteTableColumn"));
			htTools.put(KEY_TOOL_DELETECOL, jbtnDeleteColumn);
    }
	
	/**
	 * Convenience method for fetching icon images from jar file
	 */
	private ImageIcon getEkitIcon(String iconName)
	{
		URL imageURL = Ekit.class.getResource("icons/"+iconName + "HK.png");
		if(imageURL != null)
		{
			return new ImageIcon(Toolkit.getDefaultToolkit().getImage(imageURL));
		}
		imageURL = Ekit.class.getResource(iconName + "HK.gif");
		if(imageURL != null)
		{
			return new ImageIcon(Toolkit.getDefaultToolkit().getImage(imageURL));
		}
		return (ImageIcon)null;
	}
	
	/**
	 * Convenience method for creating the single toolbar from a sequence string
	 */
	public void initializeSingleToolbar(String toolbarSeq)
	{
		Vector<String> vcToolPicks = new Vector<String>();
		StringTokenizer stToolbars = new StringTokenizer(toolbarSeq.toUpperCase(), "|");
		while(stToolbars.hasMoreTokens())
		{
			String sKey = stToolbars.nextToken();
			if(sKey.equals("*"))
			{
				// ignore "next toolbar" symbols in single toolbar processing
			}
			else
			{
				vcToolPicks.add(sKey);
			}
		}

		customizeToolBar(TOOLBAR_SINGLE, vcToolPicks, true);
	}
	
	public void customizeToolBar(int whichToolBar, Vector<String> vcTools, boolean isShowing)
	{
		for(int i = 0; i < vcTools.size(); i++)
		{
			String toolToAdd = vcTools.elementAt(i).toUpperCase();
			if(toolToAdd.equals(KEY_TOOL_SEP))
			{
				add(new JToolBar.Separator());
			}
			else if(htTools.containsKey(toolToAdd))
			{
				if(htTools.get(toolToAdd) instanceof JButtonNoFocus)
				{
					add((JButtonNoFocus)(htTools.get(toolToAdd)));
				}
				else if(htTools.get(toolToAdd) instanceof JToggleButtonNoFocus)
				{
					add((JToggleButtonNoFocus)(htTools.get(toolToAdd)));
				}
				else if(htTools.get(toolToAdd) instanceof JComboBoxNoFocus)
				{
					add((JComboBoxNoFocus)(htTools.get(toolToAdd)));
				}
				else
				{
					add((JComponent)(htTools.get(toolToAdd)));
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e)
    {
	    // TODO Auto-generated method stub
	    
    }

}
