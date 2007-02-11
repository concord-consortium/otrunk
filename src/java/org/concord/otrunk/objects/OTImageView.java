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
 * $Revision: 1.1 $
 * $Date: 2007-02-11 02:11:16 $
 * $Author: imoncada $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.objects;

import java.awt.MediaTracker;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTObjectView;


/**
 * PfImageObjectView
 * Class name and description
 *
 * Date created: Sep 10, 2004
 *
 * @author imoncada<p>
 *
 */
public class OTImageView
	implements OTObjectView
{
	OTImage otObject;
	
	/**
	 * 
	 * @return
	 */
	public JComponent getComponent(OTObject otObject, boolean editable)
	{		
		this.otObject = (OTImage)otObject;

		/*		JTabbedPane tabbedPane = new JTabbedPane();
*/		
		JComponent view = getImageComponent();
		return view;
	/*	
		tabbedPane.add("View", view);
		
		JTextField txtImageURL = new JTextField(pfObject.getImageURL()); 
		txtImageURL.addCaretListener(updateListener);		
		tabbedPane.add("Edit", txtImageURL);
		
		return tabbedPane;
		*/
	}
	
	public JComponent getImageComponent()
	{
		byte [] imageBytes = otObject.getImageBytes();
		try{
			ImageIcon icon = new ImageIcon(imageBytes);
			if (icon.getImageLoadStatus() != MediaTracker.COMPLETE){
				throw new Exception();
			}
			JLabel imageLabel = new JLabel();
			imageLabel.setIcon(icon);
//			imageLabel.setBackground(Color.RED);
			imageLabel.setOpaque(false);
//			JScrollPane scrollPane = new JScrollPane(imageLabel);
		
			return imageLabel;
		}
		catch(Exception ex){
		    JLabel errorLabel = new JLabel("Image error");
		    System.err.println("Image "+ otObject.getName() +" not found");
		    errorLabel.setOpaque(true);
		    return errorLabel;
		}
	}
	
	public void viewClosed()
	{
	    
	}
	
	CaretListener updateListener = new CaretListener(){

		public void caretUpdate(CaretEvent e)
		{
			otObject.setImageURL(((JTextField)e.getSource()).getText());		
		}
	};
}
