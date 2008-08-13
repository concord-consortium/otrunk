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

package org.concord.otrunk.transfer;

import java.util.EventObject;

/**
**/
public class TransferEvent
extends EventObject
{
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	protected String item;
    protected String sourceItem;
    protected Exception error;
    protected int totalLength;
    protected int progress;
    
    public TransferEvent(Transfer transfer, String sourceContent, String contentName, int contentLength)
    {
        super(transfer);
        setSourceContent(sourceContent);
        setContentName(contentName);
        setContentLength(contentLength);
    }
    
    public String getContentName()
    {
        return item;
    }
    
    public void setContentName(String contentName)
    {
        item = contentName;
    }
    
    public String getSourceContent()
    {
        return sourceItem;
    }
    
    public void setSourceContent(String contentName)
    {
        sourceItem = contentName;
    }
    
	/**
	 * If this returns -1 then the content length is unknown
	 */
    public int getContentLength()
    {
        return totalLength;
    }
    
	/**
	 * If this is set to -1 then the content length is unknown
	 */
    public void setContentLength(int length)
    {
        totalLength = length;
    }
    
    public int getProgress()
    {
        return progress;
    }
    
    public void setProgress(int currentTotal)
    {
        progress = currentTotal;
    }
    
    public Exception getException()
    {
        return error;
    }
    
    public void setException(Exception exception)
    {
        error = exception;
    }
}

