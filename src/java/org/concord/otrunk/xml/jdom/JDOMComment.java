package org.concord.otrunk.xml.jdom;

import org.concord.otrunk.xml.OTXMLComment;
import org.jdom.Comment;

public class JDOMComment extends JDOMContent
	implements OTXMLComment
{
	Comment comment;
	
	public JDOMComment(Comment child)
    {
		this.comment = child;
    }

	public String getText()
	{
		return comment.getText();
	}
	
	public void setText(String text)
	{
		comment.setText(text);
	}
}
