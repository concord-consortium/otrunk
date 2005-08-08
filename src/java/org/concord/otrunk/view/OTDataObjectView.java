/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-08-08 18:58:14 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.concord.otrunk.OTXMLString;
import org.concord.otrunk.datamodel.OTDataObject;

public class OTDataObjectView extends JComponent
{
    OTDataObject dataObject = null;
    GridBagLayout gridBag = null;
    GridBagConstraints constraints = null;
    
    public OTDataObjectView(OTDataObject dObject)
    {
        dataObject = dObject;
    
        setOpaque(false);
        gridBag = new GridBagLayout();
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(2,2,2,2);
        
        setLayout(gridBag);

        // add the id as the first field:        
        // return new OTJavaObjectNode("id", "" + pfParent.getGlobalId());
        addEntry("id", dataObject.getGlobalId());
        
        String [] keys = dataObject.getResourceKeys();
        for(int i=0; i<keys.length; i++) {            
            String key = keys[i];
            Object child = dataObject.getResource(key);
            if(OTDataObjectNode.isChildNode(child)) {
                continue;
            }
            
            addEntry(key, child);
        }
    }
    
    public void addEntry(String key, Object value)
    {
        JLabel keyLabel = new JLabel(key);
        keyLabel.setOpaque(false);
        constraints.weightx = 0;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBag.setConstraints(keyLabel, constraints);
        add(keyLabel);
        
        JComponent valueComponent = new JTextArea(value.toString());
        valueComponent.setOpaque(false);
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.LINE_START;
        gridBag.setConstraints(valueComponent, constraints);             
        add(valueComponent);        
    }
}
