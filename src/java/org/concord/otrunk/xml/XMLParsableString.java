/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2004-12-06 03:51:35 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concord.framework.otrunk.OTID;

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
			OTID globalId = (OTID)localIdMap.get(localId);
			if(globalId != null) {
				String globalIdStr = globalId.toString();
				m.appendReplacement(parsed, globalIdStr);
			}
		}
		m.appendTail(parsed);
		return parsed.toString();
	}
}
