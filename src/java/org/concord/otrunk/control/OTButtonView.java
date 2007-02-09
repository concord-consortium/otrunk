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
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2007-02-09 22:02:52 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.concord.framework.otrunk.OTChangeEvent;
import org.concord.framework.otrunk.OTChangeListener;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTAction;
import org.concord.framework.otrunk.view.OTObjectView;

public class OTButtonView
    implements OTObjectView, OTChangeListener
{
    OTButton otButton;
    JButton jButton;
    
    public JComponent getComponent(OTObject otObject, boolean editable)
    {
        otButton = (OTButton)otObject;

        OTAction action = otButton.getAction();

        String text;
        String buttonText = otButton.getText();
        String actionText = null;
        if(action != null){
        	action.getActionText();
        }
        
        if(buttonText != null) {
            text = buttonText;
        } else if(actionText != null) {
            text = actionText;
        } else {
            text = "default";
        }
        
        jButton = new JButton(text);
        
        if(action != null){
        	jButton.addActionListener(new ActionListener(){

        		public void actionPerformed(ActionEvent arg0)
        		{
        			otButton.getAction().doAction();                
        		}            
        	});
        }
        
        otButton.addOTChangeListener(this);
        return jButton;
    }

	/* (non-Javadoc)
     * @see org.concord.framework.otrunk.OTChangeListener#stateChanged(org.concord.framework.otrunk.OTChangeEvent)
     */
    public void stateChanged(OTChangeEvent e)
    {
        if(jButton != null) {
            jButton.setText(otButton.getText());
            return;
        }
    }
    

    public void viewClosed()
    {
        // TODO Auto-generated method stub

    }

}
