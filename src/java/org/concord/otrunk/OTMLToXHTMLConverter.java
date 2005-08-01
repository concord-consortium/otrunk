
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

package org.concord.otrunk;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTObjectView;
import org.concord.framework.otrunk.view.OTPrintDimension;
import org.concord.framework.otrunk.view.OTView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.framework.otrunk.view.OTXHTMLHelper;
import org.concord.framework.otrunk.view.OTXHTMLView;
import org.concord.otrunk.view.OTViewFactory;
import org.concord.swing.util.ComponentScreenshot;

public class OTMLToXHTMLConverter implements Runnable, OTXHTMLHelper{
	
	private DefaultOTObject pfDocument;
	private OTViewContainer viewContainer;
	private OTViewFactory viewFactory;
	
    private int containerDisplayWidth;
    private int containerDisplayHeight;
    private File outputFile;
    
	public OTMLToXHTMLConverter() {
		
	}
	
	public OTMLToXHTMLConverter(OTViewFactory viewFactory,
								OTViewContainer viewContainer) {
		setViewContainer(viewContainer);			
		setViewFactory(viewFactory);
	}
	
    
    
	public void setViewContainer(OTViewContainer viewContainer) {
		this.viewContainer = viewContainer;
		if(viewContainer.getCurrentObject() != null) {
            this.pfDocument = (DefaultOTObject)viewContainer.getCurrentObject();
        }
	}
	
	public void setViewFactory(OTViewFactory viewFactory) {
		this.viewFactory = viewFactory;
	}
	
    //This is not applicable.
//	public void setPfDocument(PfDocument pfDocument) {
//		this.pfDocument = pfDocument;
//	}
	
	
    public void setXHTMLParams(File file, int pageWidth, int pageHeight)
    {
        outputFile = file;
        containerDisplayWidth = pageWidth;
        containerDisplayHeight = pageHeight;
    }
    
    public void run() {
		
        if(outputFile == null) return;
        String fileSeparator = System.getProperty("file.separator");
        File folder = new File(outputFile.getParent() + fileSeparator + "images");
        if(!folder.exists()) folder.mkdir();
        if(!folder.isDirectory()) return;

        String text = null;
		if(pfDocument != null) {
			OTObjectView objView = viewFactory.getObjectView(pfDocument, viewContainer);
            OTXHTMLView xhtmlView = null;
            String bodyText = "";
            if(objView instanceof OTXHTMLView) {
                xhtmlView = (OTXHTMLView) objView;
            	bodyText = xhtmlView.getXHTMLText(pfDocument);
            }
            //System.out.println(bodyText);
			
			Pattern p = Pattern.compile("<object refid=\"([^\"]*)\"[^>]*>");
			Matcher m = p.matcher(bodyText);
			StringBuffer parsed = new StringBuffer();
			while(m.find()) {
				String id = m.group(1);
				OTObject referencedObject = pfDocument.getReferencedObject(id);
				
				//System.out.println(referencedObject.getClass());
                String url = embedOTObject(referencedObject);
				//String url = objView.getXHTMLText(folder, containerDisplayWidth, containerDisplayHeight);
				if(url != null) {
					try {
						m.appendReplacement(parsed, url);
					} catch (IllegalArgumentException e) {
						System.err.println("bad replacement: " + url);
						e.printStackTrace();
					}
				}
			}
			m.appendTail(parsed);
			text =  parsed.toString();			
		} else {
			OTObject oto = (OTObject) viewContainer.getCurrentObject();
            text = embedOTObject(oto);
			//OTObjectView objView = viewFactory.getObjectView(oto, viewContainer);
			//text = objView.getXHTMLText(folder, containerDisplayWidth, containerDisplayHeight);
		}
        
        try {
            FileWriter fos = new FileWriter(outputFile);
            fos.write(text);
            fos.close();
        } catch (FileNotFoundException exp) {
            exp.printStackTrace();
        } catch (IOException exp) {
            exp.printStackTrace();
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
    public String embedComponent(JComponent comp, float scaleX, float scaleY, OTObject otObject) 
    {
        Dimension compSize = comp.getSize();
        
        if(compSize.height <= 0 || compSize.width <= 0) {
            throw new RuntimeException("Component size width: " + compSize.width +
                    " height: " + compSize.height + " cannot be <=0");
        }
        
        comp.addNotify();
        comp.validate();

        String seperator = System.getProperty("file.separator");
        File folder = new File(outputFile.getParent() + seperator + "images");
        if(!folder.exists()) folder.mkdir();
        if(!folder.isDirectory()) return null;
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


    public String embedOTObject(OTObject obj) {
        OTView view = viewFactory.getView(obj, OTPrintDimension.class);
        if(view == null) view = (OTView)viewFactory.getView(obj, OTObjectView.class);
        
        Dimension dim = null;
        if(view instanceof OTPrintDimension) {
            OTPrintDimension dimView = (OTPrintDimension) view;
            dim = dimView.getPrintDimention(containerDisplayWidth, containerDisplayHeight);            
        }
        
        OTObjectView objView = (OTObjectView)viewFactory.getObjectView(obj, viewContainer);
        
        JComponent comp = objView.getComponent(false);
        if(dim != null) comp.setSize(dim);
        else {
            Dimension dim2 = comp.getPreferredSize();
            if(dim2.width == 0) dim2.width = 1;
            if(dim2.height == 0) dim2.height = 1;
            comp.setSize(dim2);
        }
        
        String url = embedComponent(comp, 1, 1, obj);
        url = "<img src='" + url + "'>";
        
        return url;
        //return null;
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
                
                text = folder + seperator + id;
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
