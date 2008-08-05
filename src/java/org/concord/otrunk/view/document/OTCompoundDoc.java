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
 * Created on Aug 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.otrunk.view.document;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTXMLString;
import org.concord.otrunk.view.OTFolderObject;

/**
 * @author scott
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class OTCompoundDoc extends OTFolderObject
    implements OTDocument
{
	public static interface ResourceSchema
	    extends OTFolderObject.ResourceSchema
	{
		public OTXMLString getBodyText();

		public void setBodyText(OTXMLString text);

		public static boolean DEFAULT_input = true;

		public boolean getInput();

		public void setInput(boolean flag);

		public OTObjectList getDocumentRefs();

		public void setDocumentRefs(OTObjectList list);

		public String getMarkupLanguage();

		public void setMarkupLanguage(String lang);
		
		public void setShowEditBar(boolean showEditBar);
		public boolean getShowEditBar();
		public static boolean DEFAULT_showEditBar = true;
	}

	private ResourceSchema resources;

	public OTCompoundDoc(ResourceSchema resources)
	{
		super(resources);
		this.resources = resources;
		
		// It is tempting to try to clean up the document references here, but that will generate a lot of 
		// learner data that we don't really want to generate.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.portfolio.PortfolioObject#getBodyText()
	 */
	public String getBodyText()
	{
		OTXMLString xmlString = resources.getBodyText();
		if (xmlString == null) {
			return null;
		}

		return xmlString.getContent();
	}

	public boolean getInput()
	{
		// How do we set default values for primitives...
		return resources.getInput();
	}

	public void setInput(boolean flag)
	{
		resources.setInput(flag);
	}

	/**
	 * This won't add duplicates
	 * @param otObject
	 */
	public void addDocumentReference(OTObject otObject)
	{
		OTObjectList embedded = resources.getDocumentRefs();
		for(int i=0; i<embedded.size(); i++){
			OTObject ref = embedded.get(i);
			if(ref != null && ref.equals(otObject)){
				return;
			}
		}
		embedded.add(otObject);
	}

	/**
	 * CHECKME this might not work properly in a multi layered situation
	 * @param embeddedId
	 */
	public void addDocumentReference(OTID embeddedId)
	{
		OTObjectList embedded = resources.getDocumentRefs();
		for(int i=0; i<embedded.size(); i++){
			OTObject ref = embedded.get(i);
			if(ref != null && ref.getGlobalId().equals(embeddedId)){
				return;
			}
		}
		embedded.add(embeddedId);
	}

	/**
	 * Returns the list of objects that are actually referenced in the body
	 * text. Not to be confused with getDocumentRefs() in the resource schema.
	 * 
	 * If it finds a referenced object that was not already specified in the
	 * DocumentRefs, it adds it to the list.
	 * 
	 * TODO: This method should be renamed to getReferencedObjects() or
	 * something like that
	 * 
	 * @return
	 */
	public Vector getDocumentRefs()
	{
		String bodyText = getBodyText();

		if (bodyText != null) {
			Pattern p = Pattern.compile("<object refid=\"([^\"]*)\"[^>]*>");
			Matcher m = p.matcher(bodyText);
			while (m.find()) {
				String idStr = m.group(1);
				OTID id = getReferencedId(idStr);
				OTObject obj = getReferencedObject(id);
				if (obj != null)
					addDocumentReference(obj);
			}

			p = Pattern.compile("<a href=\"([^\"]*)\"[^>]*>");
			m = p.matcher(bodyText);
			while (m.find()) {
				String idStr = m.group(1);
				if (!(idStr.startsWith("http:") || idStr.startsWith("file:") || idStr
				        .startsWith("https:"))) {
					OTID id = getReferencedId(idStr);
					OTObject obj = getReferencedObject(id);
					if (obj != null)
						addDocumentReference(obj);
				}
			}
		}
		
		// We had a problem with duplicates getting into this list
		// From now on no duplicates should be added, but in learner mode
		// we don't want to remove existing duplicates because it will generate overriding
		// learner data that isn't good.  
		Vector references = resources.getDocumentRefs().getVector();
		Vector uniqueReferences = new Vector();
		for(int i=0; i<references.size(); i++){
			if(uniqueReferences.contains(references.get(i))){
				continue;
			}
			uniqueReferences.add(references.get(i));
		}
		return uniqueReferences;
	}
	
	public OTObjectList getDocumentRefsAsObjectList(){
		return resources.getDocumentRefs();
	}

	public void removeAllDocumentReference()
	{
		OTObjectList embedded = resources.getDocumentRefs();
		embedded.removeAll();
	}

	public String getDocumentText()
	{
		return getBodyText();
	}

	public void setDocumentText(String text)
	{
		setBodyText(text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.portfolio.objects.PfTextObject#setBodyText(java.lang.String)
	 */
	public void setBodyText(String bodyText)
	{
		OTXMLString xmlString = new OTXMLString(bodyText);
		resources.setBodyText(xmlString);

		removeAllDocumentReference();

		// pattern to match the whole body of a resource file.
		// this should match the object tag not the format of the
		// reference, but currently objects can be referenced in links
		// and in objects. And there can be viewEntry references
		// So this pattern will match any uuid that starts with quotes
		// and then grab everything after that to the end quote
		Pattern p = Pattern
		        .compile("\"([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}[^\"]*)\"");
		Matcher m = p.matcher(bodyText);
		while (m.find()) {
			String idStr = m.group(1);
			OTID id = getReferencedId(idStr);
			addDocumentReference(id);
		}
	}

	public void setMarkupLanguage(String lang)
	{
		resources.setMarkupLanguage(lang);
	}

	public String getMarkupLanguage()
	{
		return resources.getMarkupLanguage();
	}

	public OTObjectService getOTObjectService()
	{
		return resources.getOTObjectService();
	}
	
	public void setShowEditBar(boolean showEditBar)
	{
		resources.setShowEditBar(showEditBar);
	}
	
	public boolean getShowEditBar()
	{
		return resources.getShowEditBar();
	}
}