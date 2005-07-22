
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
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
 */

/*
 * Last modification information:
 * $Revision: 1.13 $
 * $Date: 2005-07-22 16:50:43 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTFrame;
import org.concord.framework.otrunk.view.OTObjectView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTViewContainerListener;
import org.concord.swing.util.ComponentScreenshot;


/**
 * OTViewContainerPanel
 * Class name and description
 *
 * Date created: Jan 20, 2005
 *
 * @author scott<p>
 *
 */
public class OTViewContainerPanel extends JPanel
	implements OTViewContainer
{
    OTObject currentObject = null;
    OTObjectView currentView = null;
    
	private OTViewFactory otViewFactory;
	
	protected OTFrameManager frameManager;

	private JFrame myFrame;

	Vector containerListeners = new Vector();
	
	/**
	 * 
	 */
	public OTViewContainerPanel(OTFrameManager frameManager, JFrame frame)
	{	
		super(new BorderLayout());
		this.frameManager = frameManager;
		myFrame = frame;
		add(new JLabel("Loading..."));
	}

	public void setOTViewFactory(OTViewFactory factory)
	{
		otViewFactory = factory;
	}
		
	public void setMessage(String message)
	{
	    removeAll();
	    add(new JLabel(message));
	}
	
	public void showFrame()
	{
		myFrame.setVisible(true);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewContainer#setCurrentObject(org.concord.framework.otrunk.OTObject, org.concord.otrunk.view.OTFrame)
	 */
	public void setCurrentObject(OTObject pfObject, OTFrame otFrame)
	{

		if(otFrame != null) {
			frameManager.setFrameObject(pfObject, otFrame);
			return;
		}
		
		if(currentView != null) {
		    currentView.viewClosed();
		}
			
		currentObject = pfObject;

		removeAll();
		add(new JLabel("Loading..."));
		revalidate();		
		
		SwingUtilities.invokeLater(new Runnable(){
		    public void run()
		    {
				JComponent newComponent = null;
				if(currentObject != null) {
				    currentView = otViewFactory.getObjectView(currentObject, OTViewContainerPanel.this);
				    if(currentView == null) {
				        newComponent = new JLabel("No view for object: " + currentObject);
						

				    } else {
				        newComponent = currentView.getComponent(true);
				    }
				} else {
					newComponent = new JLabel("Null object");
				}

				removeAll();
				add(newComponent, BorderLayout.CENTER);
				revalidate();
				notifyListeners();
				newComponent.requestFocus();		        
		    }
		});
	}

	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewContainer#setCurrentObject(org.concord.framework.otrunk.OTObject, org.concord.otrunk.view.OTFrame)
	 */
	/*public void setCurrentObject(Vector users, OTFrame otFrame)
	{
		if(users == null || users.size() == 0) return;
		
		if(currentView != null) {
		    currentView.viewClosed();
		}

		currentObject = pfObject;

		removeAll();
		add(new JLabel("Loading..."));
		revalidate();		
		
		SwingUtilities.invokeLater(new Runnable(){
		    public void run()
		    {
				JComponent newComponent = null;
				if(currentObject != null) {
				    currentView = otViewFactory.getObjectView(currentObject, OTViewContainerPanel.this);
				    if(currentView == null) {
				        newComponent = new JLabel("No view for object: " + currentObject);
						

				    } else {
				        newComponent = currentView.getComponent(true);
				    }
				} else {
					newComponent = new JLabel("Null object");
				}

				removeAll();
				add(newComponent, BorderLayout.CENTER);
				revalidate();
				notifyListeners();
				newComponent.requestFocus();		        
		    }
		});
	}*/

    public Component getCurrentComponent()
    {
        return getComponent(0);
    }
    
	public OTObject getCurrentObject()
	{
	    return currentObject;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.otrunk.view.OTViewContainer#getComponent(org.concord.framework.otrunk.OTObject, org.concord.otrunk.view.OTViewContainer, boolean)
	 */
	public JComponent getComponent(OTObject pfObject, boolean editable)
	{
	    return otViewFactory.getComponent(pfObject, this, editable);
	}

	public void addViewContainerListener(OTViewContainerListener listener)
	{
	    containerListeners.add(listener);
	}
	
	public void removeViewContainerListener(OTViewContainerListener listener)
	{
	    containerListeners.remove(listener);
	}
	
	public void notifyListeners()
	{
	    for(int i=0; i<containerListeners.size(); i++) {
	        ((OTViewContainerListener)containerListeners.get(i)).
	        	currentObjectChanged(this);
	        	
	    }
	}


    /**
     * This code attempts to save an image of the component.  
     * It does 3 things that are a bit odd but seem to make things work.
     * 1. It calls addNotify on the component.  This tricks it into thinking
     *    it has a parent, so it can be laid out.
     * 2. It calls validate on the component that makes it get laid out.
     * 
     * 3. The image saving code is placed into a invoke and wait call.
     *    Both setSize and validate cause events to be queued so we use
     *    Invoke and wait so these events get processed before we save the
     *    image by calling paint on it.
     * 
     */
	public String saveImage(JComponent comp, float scaleX, float scaleY, File folder, OTObject otObject) 
    {
	    Dimension compSize = comp.getSize();
        
        if(compSize.height <= 0 || compSize.width <= 0) {
            throw new RuntimeException("Component size width: " + compSize.width +
                    " height: " + compSize.height + " cannot be <=0");
        }
        
        comp.addNotify();
        comp.validate();

        ImageSaver saver = new ImageSaver(comp, folder, otObject, scaleX, scaleY);
        
        try{
            SwingUtilities.invokeAndWait(saver);
        } catch (InterruptedException e){
            e.printStackTrace();
        } catch (InvocationTargetException e){
            e.printStackTrace();
        }
                       
        return saver.getText();
    }

    class ImageSaver implements Runnable
    {
        JComponent comp;
        File folder;
        OTObject otObject;
        String text = null;
        float scaleX = 1;
        float scaleY = 1;
        
        ImageSaver(JComponent comp, File folder, OTObject otObject,
            float scaleX, float scaleY)
        {
            this.comp = comp;
            this.folder = folder;
            this.otObject = otObject;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
        }
        
        public void run()
        {
            // TODO Auto-generated method stub
            try{
                String seperator = System.getProperty("file.separator");
                            
                String id = otObject.getGlobalId().toString();
                id = id.replaceAll("/", "_");
                id = id.replaceAll("!", "") + ".png";
                
                if(!folder.isDirectory()) {
                    text = null;
                    return;
                }
                
                File newFile = new File(folder, id);

                BufferedImage bim = 
                    ComponentScreenshot.makeComponentImageAlpha(comp, scaleX, scaleY);
                ComponentScreenshot.saveImageAsFile(bim, newFile, "png");
                
                text = folder + "/" + id;
                return;
                            
            }catch(Throwable t){
                t.printStackTrace();
            }
            text = null;
        }
                    
        String getText()
        {
            return text;
        }
    }

}
