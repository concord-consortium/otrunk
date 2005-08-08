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
 * $Revision: 1.2 $
 * $Date: 2005-08-08 19:01:10 $
 * $Author: maven $
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
