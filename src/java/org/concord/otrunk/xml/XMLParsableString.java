/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-10-25 05:33:57 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.doomdark.uuid.UUID;


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
	
	public String parse(Hashtable localIdMap)
	{
		Pattern p = Pattern.compile("\\$\\{(.*)\\}");
		Matcher m = p.matcher(content);
		StringBuffer parsed = new StringBuffer();
		while(m.find()) {
			String localId = m.group(1);
			UUID globalId = (UUID)localIdMap.get(localId);
			if(globalId != null) {
				String globalIdStr = globalId.toString();
				m.appendReplacement(parsed, globalIdStr);
			}
		}
		m.appendTail(parsed);
		return parsed.toString();
	}
}
