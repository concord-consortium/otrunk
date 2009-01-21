/**
 * 
 */
package org.concord.otrunk.view.document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.view.OTViewEntry;

/**
 * @author scott
 *
 */
public class OTDocumentViewConfig extends DefaultOTObject implements DocumentConfig, OTViewEntry 
{
	public static interface ResourceSchema extends OTResourceSchema, OTViewEntry {
    	
		public String getMode();
		public void setMode(String mode);
		
		public String getCss();
		public void setCss(String css);
		
		public OTObjectList getCssBlocks();
		
		public boolean getViewContainerIsUpdateable();
		public void setViewContainerIsUpdateable(boolean viewContainerIsUpdateable);
		public static boolean DEFAULT_viewContainerIsUpdateable = true;
    }
	
	private ResourceSchema resources;
	private String cssText;
	
	public OTDocumentViewConfig(ResourceSchema resources)
    {
		super(resources);
	    this.resources = resources;
    }
	
	// Methods duplicated here because reflection won't understand that these are indeed
	// extended from DocumentConfig. These don't do anything, but must remain.
	
	
	public String getObjectClass()
    {
	    return resources.getObjectClass();
    }
	public String getViewClass()
    {
	    return resources.getViewClass();
    }
	public void setObjectClass(String objectClass)
    {
		resources.setObjectClass(objectClass);
    }
	public void setViewClass(String viewClass)
    {
		resources.setViewClass(viewClass);
    }
	public String getLocalId()
    {
	    return resources.getLocalId();
    }
	public boolean isResourceSet(String name)
    {
	    return resources.isResourceSet(name);
    }
	public void notifyOTChange(String property, String operation, Object value,
        Object previousValue)
    {
		resources.notifyOTChange(property, operation, value, previousValue);
    }
	public void setDoNotifyChangeListeners(boolean doNotify)
    {
		resources.setDoNotifyChangeListeners(doNotify);
    }

	public String getCss()
    {
	    return resources.getCss();
    }

	public OTObjectList getCssBlocks()
    {
	    return resources.getCssBlocks();
    }

	public String getMode()
    {
	    return resources.getMode();
    }

	public boolean getViewContainerIsUpdateable()
    {
	    return resources.getViewContainerIsUpdateable();
    }

	public void setCss(String css)
    {
		resources.setCss(css);
    }

	public void setMode(String mode)
    {
		resources.setMode(mode);
    }

	public void setViewContainerIsUpdateable(boolean viewContainerIsUpdateable)
    {
		resources.setViewContainerIsUpdateable(viewContainerIsUpdateable);
    }
	
	/** 
	 * Retrieves the CSS style text used for the document view.
	 * This will store the found css in memory so as not to have to find
	 * it each time.
	 * @return String containing (raw) CSS definitions, or a blank string.
	 */
	public String getCssText() {
		if (cssText != null){
			return cssText;
		}
		cssText = "";
		
		if (getCss() != null && getCss().length() > 0){
			cssText += getCss();
		}
		
		if (getCssBlocks() != null && getCssBlocks().getVector().size() > 0){
			for(OTObject obj: getCssBlocks()){
                OTCssText otCssText = (OTCssText) obj;
                
                // retrieve CSS definitions (originally) from the otml file
                String text = otCssText.getCssText();
                
                // if no cssText, then get src which is a URL for the css file
                if (text == null) {
                	text = "";
                	URL url = otCssText.getSrc();
                	
                	if (url != null) {
                		try {
                			URLConnection urlConnection = url.openConnection();
                			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                			String line = reader.readLine();
                			while (line != null) {
                				text += line;
                				line = reader.readLine();
                			}
                		}
                		catch (IOException e){
                			text = "";
                			e.printStackTrace();
                		}
                	}
                }
                cssText += " " + text;
            }
		}
		return cssText;
	}
}
