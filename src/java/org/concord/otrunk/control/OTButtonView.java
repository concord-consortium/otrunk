/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2006-05-05 16:00:32 $
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
import javax.swing.JLabel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTAction;
import org.concord.framework.otrunk.view.OTObjectView;
import org.concord.framework.otrunk.view.OTViewContainer;

public class OTButtonView
    implements OTObjectView
{
    OTButton otButton;
    
    public void initialize(OTObject otObject, OTViewContainer viewContainer)
    {
        otButton = (OTButton)otObject;

    }

    public JComponent getComponent(boolean editable)
    {
        OTAction action = otButton.getAction();

        if(action == null) {
            return new JLabel("invalid action");
        }
        
        String text;
        String buttonText = otButton.getText();
        String actionText = action.getActionText();
        if(buttonText != null) {
            text = buttonText;
        } else if(actionText != null) {
            text = actionText;
        } else {
            text = "default";
        }
        
        JButton myButton = new JButton(text);
        myButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent arg0)
            {
                otButton.getAction().doAction();                
            }            
        });
        return myButton;
    }

    public void viewClosed()
    {
        // TODO Auto-generated method stub

    }

}
