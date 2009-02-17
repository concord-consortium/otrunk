/*
 * Created on Feb 9, 2009 by aunger
 *
 * Copyright (c) 2009 Concord Consortium. Created
 * by Concord Consortium.
 *
 * This software is distributed under the GNU Lesser General Public License, v2.
 *
 * Permission is hereby granted, without written agreement and without license
 * or royalty fees, to use, copy, modify, and distribute this software and its
 * documentation for any purpose, provided that the above copyright notice and
 * the following two paragraphs appear in all copies of this software.
 *
 * Concord Consortium SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
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
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;

import org.concord.framework.util.IResourceLoader;

/**
 * @author aunger
 *
 */
public class OTrunkResourceLoader implements IResourceLoader{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(OTrunkResourceLoader.class.getName());

	private URL url;
	private long lastModified = 0;
	private boolean promptRetryQuit;
	private URLConnection connection;
	private InputStream urlInputStream;
	private int tryCount;
	private int responseCode = -1;
	
	// FIXME this will fail in a secure environment that can't access system properties
	private static boolean silentMode = Boolean.getBoolean("sail.rloader.silent");
	
	protected OTrunkResourceLoader(URL url, boolean promptRetryQuit)
	{
		this.url = url;
		this.promptRetryQuit = promptRetryQuit;
	}
	
	public URL getURL()
	{
		return url;
	}
	
	/**
	 * Get an input stream from a URL.  The simple way to do this is just:
	 * url.openConnection().getInputStream()
	 * 
	 * This method does more than that.  It sets some request properties indicating
	 * it wants an xml file, and it can handle gzip encoding.
	 * 
	 * Then it checks the response code.
	 * 
	 * Finally it checks if the returned contentEncoding is "gzip", in which case
	 * it uses a GZIPInputStream to uncompress the content.
	 * 
	 * FIXME It currently throws an ResourceLoadError if there is a problem.  That is a hold over
	 * from when this was a RequiredResourcedLoader.  If the resource isn't required it should
	 * throw something nicer.  Or the concept of required should be rethought a bit.
	 * 
	 * @param resourceUrl
	 * @return
	 */
	public InputStream getInputStream() throws ResourceLoadException
	{
		logger.info("loading: " + url.toString());
		tryCount = 0;
		while (true) {
			HttpURLConnection httpConnection = null;
			try {
	        	// This should not actually connect to the server yet 
	        	// that happens with the connect method.
				connection = url.openConnection();
			} catch (IOException e){
				// CHECKME there isn't much point in retrying here.  We haven't connected to the 
				// server so if there is an error at this point it will probably happen everytime.
				// However it still might be better to inform the user before just shutting down the 
				// application.
				throw new ResourceLoadException("Error opening connection", this, e, false);
			}
			
			connection.setRequestProperty("Accept", "application/xml");			
			connection.setRequestProperty("Accept-Encoding", "gzip");

			try{		
				connection.connect();
			} catch (IOException e) {
				failed(e, "Error connecting", false);
				continue;
			}
			
			if (connection instanceof HttpURLConnection) {
				httpConnection = (HttpURLConnection) connection;
				try {
					// check if this is a valid response
					responseCode = httpConnection.getResponseCode();
				} catch (IOException e) {
					failed(e, "Error getting response code", true);
					continue;
				}

				if((responseCode / 100) != 2) {
					failed(null, "Non 2XX response code: " + responseCode, true);
					continue;
				}
			}
		
			try {
				urlInputStream = connection.getInputStream();
			} catch (IOException e) {
				failed(e, "Error opening input stream", true);
				continue;
			}
	
			String encoding = connection.getContentEncoding();
			if(encoding != null && encoding.toLowerCase().equals("gzip")){
				try {
					urlInputStream = new GZIPInputStream(urlInputStream);
				} catch (IOException e) {
					failed(e, "Error ungzipping", true);
					continue;
				}
			}

			lastModified = connection.getLastModified();

			logger.fine("RequiredResourceLoader - Done.");
			return urlInputStream;
		}
	}
		
	private void failed(IOException e, String message, boolean connectionOpen) throws ResourceLoadException
	{
		tryCount++;
		logger.info("Failed attempt: " + message + 
				". Count: " + tryCount);
		if (tryCount > 1 || promptForAction() == false) {
			throw new ResourceLoadException(message, this, e, connectionOpen);
		}
	}
		
	private boolean promptForAction() {
		if (silentMode || !promptRetryQuit) { return false; }
		String[] options = new String[]{"Retry", "Quit"};
		int choice = JOptionPane.showOptionDialog(null, "There was an error downloading one or more required resources.\nPlease ensure you are connected to the Internet and retry,\n or select quit and launch the project again.", "Download Error", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		return choice == 0;
	}
	
	public long getLastModified() {
		return lastModified;
	}
	
	/**
	 * Returns -1 if there is no reponse code available.
	 * @return
	 */
	public int getHttpResponseCode() {
		return responseCode;
	}	
	
	public void writeResourceErrorDetails(PrintWriter writer, boolean printBody)
	{
		
		HttpURLConnection httpConnection = null;
		if (connection instanceof HttpURLConnection) {
			httpConnection = (HttpURLConnection) connection;
		}		

		if (httpConnection != null) {
			try {
				writer.println("Response code: " + httpConnection.getResponseCode());
			} catch (IOException ioe){
				// can't get the response code for some reason
			}
		}		

		if (urlInputStream != null) {
			try {
				writer.println("available bytes in input stream: " + urlInputStream.available());
			} catch (IOException e1){
				// can't get the available bytes for some reason.
			}
		}

		Map<String, List<String>> headerFields = connection.getHeaderFields();
		if(headerFields != null){
			for (Entry<String, List<String>> entry : headerFields.entrySet()) {
				writer.println(entry.getKey() + ": " + entry.getValue());
			}
		}

		if(!printBody){
			return;
		}
		
		writer.println("==== error body ====");
		
		String encoding = connection.getContentEncoding();
		
		if(httpConnection != null){
			InputStream errorStream = httpConnection.getErrorStream();
			if(errorStream != null){
				try {
					StreamUtil.writeFromStream(writer, errorStream, encoding);
					return;
				} catch (IOException e) {
					writer.println("Exception getting error body (errorStream): " + e);
				}
			}				
		}
			
		if(urlInputStream == null){
			// Open the connection stream if it hasn't been opened
			try {
				urlInputStream = connection.getInputStream();
				StreamUtil.writeFromStream(writer, urlInputStream, encoding);				
			} catch (Exception e) {
				// If we are here the caller already know there is a problem
				// and if the getInputStream method throws an exception then the
				// getErrorStream method should have returned some reason for the problem
				// in which case the body would be printed already.  
				// So if an exception is thrown here it is unexpected.  Put that in the 
				// print out so we can attempt to track this down later
				writer.println("Exception getting error body (inputStream): " + e);
			}
		}
	} 
}
