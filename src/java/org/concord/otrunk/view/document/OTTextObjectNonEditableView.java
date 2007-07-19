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
 * Created on Jul 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view.document;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTChangeNotifying;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OTTextObjectNonEditableView extends AbstractOTJComponentView
	implements DocumentListener, OTChangeListener
{
	OTDocument pfObject;
	
	protected PlainDocument textAreaModel = null;
	protected JTextArea textArea;

    JLabel labelView = null;
    private boolean changingText = false;
	
	protected void setup(OTObject pfTextObject)
	{
		this.pfObject = (OTDocument)pfTextObject;
		if(pfTextObject instanceof OTChangeNotifying){
		    ((OTChangeNotifying)pfTextObject).addOTChangeListener(this);
		}
	}

	public void initTextAreaModel()
	{
		if(textAreaModel == null) {
			textAreaModel = new PlainDocument();
			try
			{
				textAreaModel.insertString(0, pfObject.getDocumentText(), null);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			textAreaModel.addDocumentListener(this);
		}		
	}
	
	public JComponent getComponent(OTObject otObject, boolean editable)
	{
		setup(otObject);

		labelView = new JLabel(pfObject.getDocumentText());
		return labelView;

	}


	public void changedUpdate(DocumentEvent event)
	{
		try {
		    changingText = true;
			pfObject.setDocumentText(textAreaModel.getText(0, textAreaModel.getLength()));
			changingText = false;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTChangeListener#stateChanged(org.concord.framework.otrunk.OTChangeEvent)
     */
    public void stateChanged(OTChangeEvent e)
    {
        if(changingText){
            // we have caused this event ourselves
            return;
        }
        
        if(labelView != null) {
            labelView.setText(pfObject.getDocumentText());
            return;
        }
        
        try {
            textAreaModel.replace(0, textAreaModel.getLength(), pfObject.getDocumentText(), null);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
    
	public void insertUpdate(DocumentEvent event)
	{
		try {
		    changingText = true;
			pfObject.setDocumentText(textAreaModel.getText(0, textAreaModel.getLength()));
			changingText = false;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void removeUpdate(DocumentEvent event)
	{
		try {
		    changingText = true;
			pfObject.setDocumentText(textAreaModel.getText(0, textAreaModel.getLength()));
			changingText = false;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.view.OTJComponentView#viewClosed()
     */
    public void viewClosed()
    {
        if(textAreaModel != null) {
            textAreaModel.removeDocumentListener(this);
        }

		if(pfObject instanceof OTChangeNotifying){
		    ((OTChangeNotifying)pfObject).removeOTChangeListener(this);
		}

    }

}
