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
 * $Revision: 1.8 $
 * $Date: 2007-10-04 21:28:21 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concord.framework.otrunk.OTID;
import org.concord.framework.otrunk.OTXMLString;

/**
 * XMLParsableString
 * Class name and description
 *
 * Date created: Oct 23, 2004
 *
 * @author scott<p>
 *
 */
public class XMLParsableString
{
	private String content;
	
	public XMLParsableString(String content)
	{
		this.content = content;
	}
	/**
	 * @return Returns the content.
	 */
	public String getContent()
	{
		return content;
	}
	/**
	 * @param content The content to set.
	 */
	public void setContent(String content)
	{
		this.content = content;
	}
	
	public OTXMLString parse(HashMap<String, OTID> localIdMap)
	{
		Pattern p = Pattern.compile("\\$\\{([^}]*)\\}");
		Matcher m = p.matcher(content);
		StringBuffer parsed = new StringBuffer();
		while(m.find()) {
			String localId = m.group(1);
			OTID globalId = localIdMap.get(localId);
			if(globalId != null) {
				String globalIdStr = globalId.toExternalForm();
				m.appendReplacement(parsed, globalIdStr);
			}
		}
		m.appendTail(parsed);
		
		return new OTXMLString(parsed.toString());
	}
}
