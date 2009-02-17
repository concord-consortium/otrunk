/*
 * Created on Feb 16, 2009 by scytacki
 *
 * Copyright (c) 2009 Regents of the University of California (Regents). Created
 * by TELS, Graduate School of Education, University of California at Berkeley.
 *
 * This software is distributed under the GNU Lesser General Public License, v2.
 *
 * Permission is hereby granted, without written agreement and without license
 * or royalty fees, to use, copy, modify, and distribute this software and its
 * documentation for any purpose, provided that the above copyright notice and
 * the following two paragraphs appear in all copies of this software.
 *
 * REGENTS SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. THE SOFTWAREAND ACCOMPANYING DOCUMENTATION, IF ANY, PROVIDED
 * HEREUNDER IS PROVIDED "AS IS". REGENTS HAS NO OBLIGATION TO PROVIDE
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 * IN NO EVENT SHALL REGENTS BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
 * SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
 * ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 * REGENTS HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.concord.otrunk.net;

import java.io.IOException;
import java.io.StringWriter;

import org.concord.framework.util.IResourceLoader;

/**
 * @author scytacki
 *
 */
public class ResourceLoadException extends IOException{

	private IResourceLoader resourceLoader;
	private boolean hasDetails;
	
	public ResourceLoadException(String message, IResourceLoader resourceLoader, 
			Throwable cause, boolean hasDetails)
	{
		super(message + " resource: " + resourceLoader.getURL());
		if(cause != null) {
			initCause(cause);
		}
		this.resourceLoader = resourceLoader;
		this.hasDetails = hasDetails;
	}

	
	public void printResourceErrorDetails()
	{
		IndentingPrintWriter writer = new IndentingPrintWriter(System.err);
		writeResourceErrorDetails(writer);
	}
	
	public String getResourceErrorDetails()
	{
		StringWriter sWriter = new StringWriter();
		IndentingPrintWriter writer = new IndentingPrintWriter(sWriter);
		writeResourceErrorDetails(writer);
		return sWriter.toString();
	}

	protected void writeResourceErrorDetails(IndentingPrintWriter writer)
	{
		writer.printFirstln(this.toString());
		if(getCause() != this && getCause() != null){
			writer.println("cause: " + getCause().toString());
		}
		resourceLoader.writeResourceErrorDetails(writer, hasDetails);
		writer.flush();		
	}
	
	public int getHttpResponseCode()
	{
		return resourceLoader.getHttpResponseCode();
	}
	
	@Override
	public void printStackTrace()
	{
	    printResourceErrorDetails();
	    super.printStackTrace();
	}
}
