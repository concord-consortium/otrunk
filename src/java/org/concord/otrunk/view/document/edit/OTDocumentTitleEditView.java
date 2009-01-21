package org.concord.otrunk.view.document.edit;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;
import org.concord.otrunk.view.document.OTDocument;

/**
 * The TitleEditView is a view for a document that would be a section of a larger
 * document, to be used in a template authoring system such as RITES.
 * 
 * This view is simply a text area, used to edit a title of a page or section.
 * 
 * @author sfentress
 *
 */
public class OTDocumentTitleEditView extends AbstractOTJComponentView
{

	private OTDocument document;

	public JComponent getComponent(OTObject otObject)
    {
		this.document = (OTDocument)otObject;
		
		JTextField textField = new JTextField(document.getDocumentText());
		textField.setFont(textField.getFont().deriveFont(Font.BOLD, 14));
		
		String name = document.getName();
		if (name == null) name = "Title";
		Border title = BorderFactory.createTitledBorder(name);
		Border buffer = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		textField.setBorder(BorderFactory.createCompoundBorder(buffer, title));
		
	    return textField;
    }

}
